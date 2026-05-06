package com.wt.complaint.manage.domain.strategy.message;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.PRODUCT_RISK_UPGRADE_STORE_AUDIT;

/**
 * 产品风险升级投诉
 * 发送给门店人员（所有渠道）
 *
 * @author kiro
 * @date 2026/1/27
 */
@Slf4j
@Service(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT)
public class ProductRiskUpgradeAuditMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("产品风险升级，ProductRiskUpgradeStoreAuditMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        // 消息接收人角色信�? 当前门店店长、服务体验专家（这些通过语音短信、零售通push、零售通站内信、飞书机器人�?
        Set<Long> storeSet = getMidSetByPositionIdListAndOrg(Lists.newArrayList(PositionEnum.CAR_SERVICE_STORE_MANAGER.getCode(), CarEmployeeEnum.RECEIVER_EXPERT.getCode()), complaintOrder.getOrgId());
        // 投诉单处理人
        if (complaintOrder.getOperatorMid() != null && !Objects.equals(complaintOrder.getOperatorMid(), 0L)) {
            storeSet.add(complaintOrder.getOperatorMid());
        } else {
            log.warn("MediaInvolvedAuditMessage#createMessageInformedEvent 当前处理人mid不合�? operatorMid={}",
                    complaintOrder.getOperatorMid());
        }
        storeSet.add(complaintOrder.getOperatorMid());
        log.info("ProductRiskUpgradeAuditMessage storeSet={}", storeSet);

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());

        // 查询指定岗位对应的人员信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Lists.newArrayList(storeSet)).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        Set<String> emailSet = employeeList.stream().map(EmployeeInfoGoOut::getEmail).collect(Collectors.toSet());

        // 城市体验专家、区域体验专�?
        Set<String> regionlEmailSet = new HashSet<>(getFinalEmailSetByZoneAndPosition(complaintOrder, Arrays.asList(PositionEnum.URBAN_EXPERIENCE_EXPERT, PositionEnum.REGIONAL_EXPERIENCE_EXPERT)));
        log.info("ProductRiskUpgradeAuditMessage allEmailSet={}", regionlEmailSet);
        emailSet.addAll(regionlEmailSet);

        // 客诉处理（全国）
        List<String> complaintEmails = getEmailListByPositionId(PositionEnum.COMPLAINT_HANDLING.getCode());
        log.info("ProductRiskUpgradeAuditMessage complaintEmails={}", complaintEmails);
        emailSet.addAll(complaintEmails);

        // 服务满意度管理（全国�?
        List<String> serviceEmails = getEmailListByPositionId(PositionEnum.SATISFACTION_MANAGEMENT.getCode());
        log.info("ProductRiskUpgradeAuditMessage serviceEmails={}", serviceEmails);
        emailSet.addAll(serviceEmails);

        // 零售通站内信站内�?消息�?
        String message = "门店处理中产品风险已升级投诉，请及时关注";
        // 零售通App push 消息�?
        String title = "产品风险升级投诉";
        String description = String.format("门店处理中产品风险已升级投诉，投诉单�?s，请及时关注", complaintOrder.getComplaintNo());

        return MessageInformedEvent.builder()
                .requestId(String.format(AUDIT_COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.PRODUCT_RISK_UPGRADE_AUDIT,
                        complaintOrder.getComplaintNo(),
                        complaintOrder.getId()))
                .orgId(complaintOrder.getOrgId())
                .complaintNo(complaintOrder.getComplaintNo())
                .pushEnum(PRODUCT_RISK_UPGRADE_STORE_AUDIT)
                // 零售�?
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                // 零售�?投诉提醒
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_TO_BE_FOLLOWED))
                .auth(false)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, null, title, description))
                .midSet(storeSet)
                .miOfficePayload(getMiOfficePayload(complaintOrder, complaintOrder.getId()))
                .emailSet(emailSet)
                .build();
    }

    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, Long auditId) {
        Map<String, String> miOfficePayload = new HashMap<>();
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        miOfficePayload.put("orgName", orgName);

        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put("triggerTime", DateUtil.formatDateTime(new Date()));
        miOfficePayload.put("href", pcMainCarMaintenanceUrl + "storeOperation/complaint/complaintListDetails?complaintNo=" + complaintOrder.getComplaintNo());
        return miOfficePayload;
    }
}
