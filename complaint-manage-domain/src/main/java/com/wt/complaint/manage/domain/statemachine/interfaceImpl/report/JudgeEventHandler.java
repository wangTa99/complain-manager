package com.wt.complaint.manage.domain.statemachine.interfaceImpl.report;

import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.service.converter.OrderOperationConverter;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.statemachine.interfaces.Action;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/22 14:16
 */
@Component
public class JudgeEventHandler extends AbstractHandler {
    @Resource
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Action<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> action() {
        return (ReportOrderStatusEnum from, ReportOrderStatusEnum to, UcOrderEventEnum event, UcOrderContext context) -> {
            // 更新客诉主表状�?
            UcOrderUpdateGoIn ucOrderUpdateGoIn = UcOrderUpdateGoIn.builder()
                    .ucNo(context.getUcNo())
                    .orderStatus(to.getCode())
                    .finishTime(new Date())
                    .build();
            userComplaintOrderGateway.updateOrderSelective(ucOrderUpdateGoIn);

            // 记录跟进记录�?
            ComplaintFollowProcessGoIn followUpRecord = buildRecordInfoGoIn(event, context);
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }

            return true;
        };
    }

    /**
     * 构建跟进记录表内�?
     * @param context 信息
     * @return
     */
    @Override
    protected ComplaintFollowProcessGoIn buildRecordInfoGoIn(UcOrderEventEnum event, UcOrderContext context) {
        // 构建跟进记录表内�?
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .operateTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .operateMid(context.getOperateMid())
                .operateName(context.getOperateName())
                .operateDesc(context.getOperateContent())
                .judgeResult(context.getOperateType())
                .attachments(OrderOperationConverter.INSTANCE.toAttachmentGoIn(context.getAttachmentList()))
                .build();

        // 构建跟进记录表gateway入参
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(context.getUcNo())
                .processType(event.getProcessType().getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }
}
