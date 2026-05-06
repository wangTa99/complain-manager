package com.wt.complaint.manage.domain.statemachine.interfaceImpl.report;

import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderUpdateGoIn;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.statemachine.interfaces.Action;
import com.wt.complaint.manage.domain.statemachine.interfaces.Condition;
import com.wt.complaint.manage.domain.statemachine.interfaces.StateMachineHandler;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2024/10/23 18:31
 */
@Slf4j
public class AbstractHandler implements StateMachineHandler<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> {
    @Resource
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Override
    public Condition<UcOrderContext> condition() {
        return c -> true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Action<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> action() {
        return (ReportOrderStatusEnum from, ReportOrderStatusEnum to, UcOrderEventEnum event, UcOrderContext context) -> {
            // 更新客诉主表状�?
            updateOrderStatus(from, to, context);

            // 记录跟进记录�?
            ComplaintFollowProcessGoIn followUpRecord = buildRecordInfoGoIn(event, context);
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }

            return true;
        };
    }

    /**
     * 更新客诉单主�?
     * @param from 当前状�?
     * @param to 目标状�?
     * @param context 上下�?
     */
    protected void updateOrderStatus(ReportOrderStatusEnum from, ReportOrderStatusEnum to, UcOrderContext context) {
        // 状态发生变更，更新客诉单主�?
        if (from != to) {
            UcOrderUpdateGoIn ucOrderUpdateGoIn = UcOrderUpdateGoIn.builder()
                    .ucNo(context.getUcNo())
                    .orderStatus(to.getCode()).build();
            userComplaintOrderGateway.updateOrderSelective(ucOrderUpdateGoIn);
        }
    }

    /**
     * 构建跟进记录表内�?
     * @param context 信息
     * @return
     */
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
