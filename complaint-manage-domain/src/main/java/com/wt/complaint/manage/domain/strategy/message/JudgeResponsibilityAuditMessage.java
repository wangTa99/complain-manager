package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.JUDGE_RESPONSIBILITY_AUDIT;

/**
 * 生成投诉单后生成待判责审批任务时
 * 发送服务满意度管理岗位�?74）审批消�?
 * 接收人：服务满意度管理岗位人�?
 *
 * @author kiro
 * @date 2026/2/28
 */
@Slf4j
@Service(PushConstant.JUDGE_RESPONSIBILITY_AUDIT)
public class JudgeResponsibilityAuditMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("生成待判责审批任务，JudgeResponsibilityAuditMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        ComplaintAuditSoOut auditSoOut = complaintAuditGateway.getRecentAuditByComplaintNo(complaintOrder.getComplaintNo(),
                AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        if (auditSoOut == null || auditSoOut.getId() == null) {
            log.warn("JudgeResponsibilityAuditMessage#createMessageInformedEvent 未查询到审批�? complaintNo={}",
                    complaintOrder.getComplaintNo());
            return null;
        }

        return MessageInformedEvent.builder()
                .requestId(String.format(AUDIT_COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.JUDGE_RESPONSIBILITY_AUDIT,
                        complaintOrder.getComplaintNo(),
                        auditSoOut.getId()))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(JUDGE_RESPONSIBILITY_AUDIT)
                .emailSet(getEmailSet())
                .miOfficePayload(getMiOfficePayload(complaintOrder, auditSoOut.getId()))
                .build();
    }

    /**
     * 服务满意度管理人员（全国�?
     */
    private Set<String> getEmailSet() {
        // 服务满意度管理（全国）岗位ID: 174
        List<String> serviceEmails = getEmailListByPositionId(PositionEnum.SATISFACTION_MANAGEMENT.getCode());
        log.info("JudgeResponsibilityAuditMessage serviceEmails={}", serviceEmails);
        return new HashSet<>(serviceEmails);
    }

    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, Long auditId) {
        Map<String, String> miOfficePayload = new HashMap<>();
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        miOfficePayload.put("orgName", orgName);
        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put("href", pcMainCarMaintenanceUrl + "storeOperation/complaint/complaintDetail?id=" + auditId);
        return miOfficePayload;
    }
}
