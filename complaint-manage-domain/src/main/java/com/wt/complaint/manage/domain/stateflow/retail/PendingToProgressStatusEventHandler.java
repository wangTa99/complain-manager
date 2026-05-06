package com.wt.complaint.manage.domain.stateflow.retail;

import cn.hutool.core.date.DateUtil;
import com.wt.complaint.manage.api.model.enums.ReminderFlagEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UpdateRetailOrderGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.UpdateRetailOrderSoIn;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PendingToProgressStatusEventHandler extends BaseRetailUserComplaintStatusHandler
        implements UserComplaintStatusEventHandler<UpdateRetailOrderSoIn, Boolean> {

    @Autowired
    private RetailComplaintGateway retailComplaintGateway;

    @Autowired
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Override
    public UcOrderTypeEnum getUcOrderType() {
        return UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER;
    }

    @Override
    public List<Integer> getSourceList() {
        return Collections.singletonList(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode());
    }

    @Override
    public Integer getTarget() {
        return RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode();
    }

    @Override
    @Transactional
    public Boolean handle(UpdateRetailOrderSoIn soIn) {
        boolean insertRecords;
        // 是否首响
        if (soIn.isFirstResp()) {
            retailComplaintGateway.updateOrderByDrNo(
                    UpdateRetailOrderGoIn.builder().drNo(soIn.getDrNo())
                            .orderStatus(RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode())
                            .realFirstResponseTime(DateUtil.date()).build());
        }
        // 更新催单标识
        if (ReminderFlagEnum.TRUE.getCode().equals(soIn.getReminderFlag())) {
            retailComplaintGateway.updateOrderByDrNo(
                    UpdateRetailOrderGoIn.builder().drNo(soIn.getDrNo())
                            .reminderFlag(ReminderFlagEnum.FALSE.getCode()).build());
        }
        // 持久化跟进记�?
        insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(
                soIn.getComplaintFollowProcessGoIn());
        return insertRecords;
    }
}
