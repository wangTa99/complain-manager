package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.APPLICATION_FOR_CLOSURE_AUDIT;

/**
 * 门店投诉单申请结�?
 * @author zhangzheyang
 * @date 2025/1/2
 */
@Slf4j
@Service(PushConstant.APPLICATION_FOR_CLOSURE_AUDIT)
public class ApplicationForClosureAuditMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("门店投诉单申请结案，ApplicationForClosureAuditMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        ComplaintAuditSoOut auditSoOut = complaintAuditGateway.getRecentAuditByComplaintNo(complaintOrder.getComplaintNo(),
                AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        if (auditSoOut == null || auditSoOut.getId() == null) {
            log.error("ApplicationForClosureAuditMessage#createMessageInformedEvent 未查询到审批�? complaintNo={}",
                    complaintOrder.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_AUDIT_NOT_FOUND);
        }

        return MessageInformedEvent.builder()
                .requestId(String.format(AUDIT_COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.APPLICATION_FOR_CLOSURE_AUDIT,
                        complaintOrder.getComplaintNo(),
                        auditSoOut.getId()))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(APPLICATION_FOR_CLOSURE_AUDIT)
                .emailSet(getEmailSet(complaintOrder.getCustomerServiceMid()))
                .miOfficePayload(getMiOfficePayload(complaintOrder, auditSoOut.getId()))
                .build();
    }

    /**
     *  需要客服作业单处理�?
     */
    private Set<String> getEmailSet(Long operatorMid) {
        List<EmployeeInfoGoOut> employeeInfoGoOutList = eiamRemoteGateway.getEmployeeList(
                EmployeeListGoIn
                        .builder()
                        .miIdList(Collections.singletonList(operatorMid))
                        .build());
        if (CollectionUtils.isEmpty(employeeInfoGoOutList)) {
            log.error("ApplicationForClosureAuditMessage#getEmailSet, 未查询到用户信息, mid={}", operatorMid);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未查询到用户信息");
        }
        Set<String> emailSet = new HashSet<>();
        emailSet.add(employeeInfoGoOutList.get(0).getEmail());
        return emailSet;
    }

    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, Long auditId) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("stNo", complaintOrder.getSuperTicketNo());
        // 给客服工作台发消�?需要超级工单号
        miOfficePayload.put("href",
                pcMainCustomerServiceUrl + "task-center/handle-order/processing/detail?id=" + complaintOrder.getSuperTicketNo());
        return miOfficePayload;
    }
}
