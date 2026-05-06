package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT;

/**
 * 72H无法结案审批
 * @author zhangzheyang
 * @date 2025/1/2
 */
@Slf4j
@Service(PushConstant.APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT)
public class Application72hCannotBeClosedAuditMessage extends AbstractMessageInformedStrategy{

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder,
                                                           Map<String, String> extraParam) {
        log.info("72H无法结案审批消息组装，Application72hCannotBeClosedAuditMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        ComplaintAuditSoOut auditSoOut = complaintAuditGateway.getRecentAuditByComplaintNo(complaintOrder.getComplaintNo(),
                AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        if (auditSoOut == null || auditSoOut.getId() == null) {
            log.error("Application72hCannotBeClosedAuditMessage#createMessageInformedEvent 未查询到审批�? complaintNo={}",
                    complaintOrder.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_AUDIT_NOT_FOUND);
        }

        return MessageInformedEvent.builder()
                .requestId(String.format(AUDIT_COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT,
                        complaintOrder.getComplaintNo(),
                        auditSoOut.getId()))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT)
                .emailSet(getEmailSet(auditSoOut))
                .miOfficePayload(getMiOfficePayload(complaintOrder, auditSoOut.getId()))
                .build();
    }

    /**
     * 查询中台区域体验专家email
     */
    @NotNull
    private Set<String> getEmailSet(ComplaintAuditSoOut complaintOrder) {
        // 1. 根据zoneId和区域体验专家岗位查询人员邮�?
        if (StringUtils.isEmpty(complaintOrder.getZoneId()) || !StringUtils.isNumeric(complaintOrder.getZoneId())) {
            log.error("ProductRiskClosureApplicationAuditMessage#getEmailSet, zoneId为空或非数字, complaintNo={}, zoneId={}",
                    complaintOrder.getComplaintNo(), complaintOrder.getZoneId());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单大区信息异�?);
        }

        // 客诉二期：新增区域体验专家，删除服务满意度管�?
        List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                .positionId(PositionEnum.REGIONAL_EXPERIENCE_EXPERT.getCode())
                .bigZoneIdList(Collections.singletonList(Integer.valueOf(complaintOrder.getZoneId())))
                .build());

        if (CollectionUtils.isEmpty(emailList)) {
            log.error("ProductRiskClosureApplicationAuditMessage#getEmailSet, 未查询到区域体验专家, complaintNo={}, zoneId={}",
                    complaintOrder.getComplaintNo(), complaintOrder.getZoneId());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未查询到区域体验专家");
        }
        return new HashSet<>(emailList);
    }

    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, Long auditId) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put("href", pcMainCarMaintenanceUrl + "storeOperation/complaint/complaintDetail?id=" + auditId);
        return miOfficePayload;
    }
}
