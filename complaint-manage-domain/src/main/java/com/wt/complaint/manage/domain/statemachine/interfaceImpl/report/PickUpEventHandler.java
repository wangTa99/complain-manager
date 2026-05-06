package com.wt.complaint.manage.domain.statemachine.interfaceImpl.report;

import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import org.springframework.stereotype.Component;

/**
 * @author linjiehong
 * @date 2025/5/22 14:15
 */
@Component
public class PickUpEventHandler extends AbstractHandler {
    @Override
    protected ComplaintFollowProcessGoIn buildRecordInfoGoIn(UcOrderEventEnum event, UcOrderContext context) {
        // 构建跟进记录表内�?
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .pickUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .orderReceiverMid(context.getOperateMid())
                .orderReceiverName(context.getOperateName())
                .build();

        // 构建跟进记录表gateway入参
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(context.getUcNo())
                .processType(event.getProcessType().getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }
}
