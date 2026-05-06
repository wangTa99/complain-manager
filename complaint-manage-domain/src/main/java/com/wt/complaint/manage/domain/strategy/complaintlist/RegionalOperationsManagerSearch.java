package com.wt.complaint.manage.domain.strategy.complaintlist;

import com.wt.complaint.manage.domain.api.enums.PermissionTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(StrategyConstant.REGIONAL_OPERATIONS_MANAGER_SEARCH)
@Slf4j
public class RegionalOperationsManagerSearch extends AbstractComplaintListSearch {
    private static final String COMPLAINT_NO_PREFIX = "TS";

    @Autowired
    private CarRemoteGateway carRemoteGateway;

    @Override
    protected ComplaintListSearchGoIn preHandler(ComplaintListSearchGoIn goIn) {
        log.info("before RegionalOperationsManagerSearch#preHandler, goIn:{}", RetailJsonUtil.toJson(goIn));
        transformSearchKey(goIn);
        ComplaintListSearchGoIn newGoIn = genNewSearchGoIn(goIn);
        log.info("RegionalOperationsManagerSearch#preHandler success, newGoIn:{}", RetailJsonUtil.toJson(newGoIn));
        return newGoIn;
    }

    @Override
    protected void postHandler(ComplaintListSearchGoIn complaintListSearchGoIn, ComplaintListSearchSoOut complaintListSearchSoOut) {

    }

    public void transformSearchKey(ComplaintListSearchGoIn goIn) {
        if (StringUtils.isNotBlank(goIn.getContactPhone())) {
            goIn.setContactPhone(KeyCenterUtil.md5(goIn.getContactPhone()));
        }
        if (StringUtils.isNotBlank(goIn.getVin())) {
            String vid = carRemoteGateway.getVidByVin(goIn.getVin());
            if (StringUtils.isNotEmpty(vid)) {
                goIn.setVin(vid);
            } else {
                goIn.setVin(CommonConst.INVALID_DATA);
            }
        }
    }

    public ComplaintListSearchGoIn genNewSearchGoIn(ComplaintListSearchGoIn goIn) {
        // 深拷�?
        ComplaintListSearchGoIn newGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(goIn), ComplaintListSearchGoIn.class);
        // 查看大区权限
        newGoIn.getAfterSaleWorkbenchPermissionGroup().setAfterSaleWorkbenchPermissionType(PermissionTypeEnum.BIG_ZONE.getCode());
        return newGoIn;
    }
}

