package com.wt.complaint.manage.domain.strategy.complaintlist;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service(StrategyConstant.PAD_RELATE_COMPLAINT_LIST_SEARCH)
@Slf4j
public class PadRelateComplaintListSearch extends AbstractComplaintListSearch {

    @Autowired
    private CarRemoteGateway carRemoteGateway;

    @NacosValue(value = "${complaintClosingTag}", autoRefreshed = true)
    private String complaintClosingTagString;

    @Override
    protected ComplaintListSearchGoIn preHandler(ComplaintListSearchGoIn goIn) {
        log.info("before PadRelateComplaintListSearch#preHandler, goIn:{}", RetailJsonUtil.toJson(goIn));
        transformSearchKey(goIn);
        ComplaintListSearchGoIn newGoIn = genNewSearchGoIn(goIn);
        log.info("PadRelateComplaintListSearch#preHandler success, newGoIn:{}", RetailJsonUtil.toJson(newGoIn));
        return newGoIn;
    }

    @Override
    protected void postHandler(ComplaintListSearchGoIn complaintListSearchGoIn, ComplaintListSearchSoOut complaintListSearchSoOut) {

    }

    public void transformSearchKey(ComplaintListSearchGoIn goIn) {
        if (StringUtils.isNotBlank(goIn.getVin())) {
            String vid = carRemoteGateway.getVidByVin(goIn.getVin());
            if (!StringUtils.isEmpty(vid)) {
                goIn.setVin(vid);
            } else {
                goIn.setVin(CommonConst.INVALID_DATA);
            }
        }
        ComplaintListSearchGoIn.PadRelateListGroup padRelateListGroup = new ComplaintListSearchGoIn.PadRelateListGroup();
        padRelateListGroup.createTimeStart = DateUtil.getLastMonth();
        padRelateListGroup.createTimeEnd = DateUtil.getCurDate();
        padRelateListGroup.setInProgressStatus(Arrays.asList(ComplaintStatusEnum.PENDING_ORDER.getCode(), ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(), ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode(), ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode(), ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()));
        List<String> tagList = new ArrayList<>();
        if (StringUtils.isBlank(complaintClosingTagString)) {
            tagList.add(CommonConst.INVALID_DATA);
        } else {
            tagList = JacksonUtil.parseArray(complaintClosingTagString, String.class);
        }
        padRelateListGroup.tagList = tagList;
        padRelateListGroup.setCompleteStatus(Arrays.asList(ComplaintStatusEnum.FINISH_COMPLETE.getCode()));
        goIn.setPadRelateListGroup(padRelateListGroup);
        // 重置条件
        goIn.setCreateTimeStart(null);
        goIn.setCreateTimeEnd(null);
        goIn.setTagList(null);
    }

    public ComplaintListSearchGoIn genNewSearchGoIn(ComplaintListSearchGoIn goIn) {
        // 深拷�?
        ComplaintListSearchGoIn newGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(goIn), ComplaintListSearchGoIn.class);
        return newGoIn;
    }
}
