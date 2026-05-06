package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.UserComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchLightInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailFrameGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchLightInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.strategy.view.UserComplaintListFactory;
import com.wt.complaint.manage.domain.strategy.view.UserComplaintListStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.annotation.Resource;

@Slf4j
@Service
public class UserComplaintViewServiceImpl implements UserComplaintViewService {

    @Autowired
    private UserComplaintListFactory userComplaintListFactory;

    @Resource
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;

    @Override
    public UserComplaintListSearchSoOut searchUserComplaintList(UserComplaintListSearchGoIn goIn) {
        UserComplaintListStrategy userComplaintListStrategy = userComplaintListFactory.getStrategy(goIn.getUcType());
        if (ObjectUtil.isNull(userComplaintListStrategy)) {
            return new UserComplaintListSearchSoOut();
        }
        return userComplaintListStrategy.searchUserComplaintList(goIn);
    }

    @Override
    public UserComplaintDetailFrameSoOut getUserComplaintFrame(UserComplaintDetailFrameGoIn goIn) {
        UcOrderTypeEnum ucOrderTypeEnum = UcOrderTypeEnum.getByUcNo(goIn.getUcNo());
        UserComplaintListStrategy userComplaintListStrategy =
                userComplaintListFactory.getStrategy(ucOrderTypeEnum.getCode());
        if (ObjectUtil.isNull(userComplaintListStrategy)) {
            return new UserComplaintDetailFrameSoOut();
        }
        return userComplaintListStrategy.getUserComplaintFrame(goIn);
    }

    @Override
    public UserComplaintDetailSoOut getUserComplaintDetail(UserComplaintDetailGoIn goIn) {
        UcOrderTypeEnum ucOrderTypeEnum = UcOrderTypeEnum.getByUcNo(goIn.getUcNo());
        UserComplaintListStrategy userComplaintListStrategy =
                userComplaintListFactory.getStrategy(ucOrderTypeEnum.getCode());
        if (ObjectUtil.isNull(userComplaintListStrategy)) {
            return new UserComplaintDetailSoOut();
        }
        return userComplaintListStrategy.getUserComplaintDetail(goIn);
    }

    @Override
    public UcOrderBatchInfoSoOut getUcOrderInfo(UcOrderBatchInfoSoIn soIn) {
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(soIn.getUcNoList().get(0));
        UserComplaintListStrategy userComplaintListStrategy =
                userComplaintListFactory.getStrategy(ucOrderTypeEnum.getCode());
        if (ObjectUtil.isNull(userComplaintListStrategy)) {
            return new UcOrderBatchInfoSoOut();
        }
        return userComplaintListStrategy.getUcOrderInfo(soIn);
    }

    @Override
    public UcOrderBatchLightInfoSoOut getUcOrderLightInfo(UcOrderBatchLightInfoSoIn soIn) {
        // 查询关联单据信息
        ComplaintRelationOrderListGoIn goIn = new ComplaintRelationOrderListGoIn();
        goIn.setBizNoList(soIn.getBizOrderList());
        List<ComplaintRelationOrderGoOut> relationList = complaintRelationOrderRepositoryGateway.findList(goIn);
        if (CollUtil.isEmpty(relationList)) {
            return new UcOrderBatchLightInfoSoOut();
        }
        soIn.setRelationList(relationList);

        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(relationList.get(0).getComplaintNo());
        UserComplaintListStrategy userComplaintListStrategy =
                userComplaintListFactory.getStrategy(ucOrderTypeEnum.getCode());
        if (ObjectUtil.isNull(userComplaintListStrategy)) {
            return new UcOrderBatchLightInfoSoOut();
        }
        return userComplaintListStrategy.getUcOrderLightInfo(soIn);
    }
}
