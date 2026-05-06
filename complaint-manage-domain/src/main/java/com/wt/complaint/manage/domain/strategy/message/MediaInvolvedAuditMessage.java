package com.wt.complaint.manage.domain.strategy.message;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.MEDIA_INVOLVED_STORE_AUDIT;

/**
 * 涉媒投诉
 * 发送给门店人员（所有渠道）
 *
 * @author kiro
 * @date 2026/1/27
 */
@Slf4j
@Service(PushConstant.MEDIA_INVOLVED_AUDIT)
public class MediaInvolvedAuditMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("涉媒投诉，MediaInvolvedAuditMessage, complaintOrder:{}, extraParam:{}",
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
        log.info("MediaInvolvedAuditMessage storeSet={}", storeSet);

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());

        // 查询mid对应的邮�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(new ArrayList<>(storeSet)).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        Set<String> emailSet = employeeList.stream().map(EmployeeInfoGoOut::getEmail).collect(Collectors.toSet());

        // 城市服务经理、城市体验专家、区域体验专�?
        Set<String> regionEmailSet = new HashSet<>(getFinalEmailSetByZoneAndPosition(complaintOrder, Arrays.asList(PositionEnum.CITY_SERVICE_MANAGER, PositionEnum.URBAN_EXPERIENCE_EXPERT, PositionEnum.REGIONAL_EXPERIENCE_EXPERT)));
        log.info("MediaInvolvedAuditMessage regionEmailSet={}", regionEmailSet);
        emailSet.addAll(regionEmailSet);

        // 客诉处理（全国）
        List<String> complaintEmails = getEmailListByPositionId(PositionEnum.COMPLAINT_HANDLING.getCode());
        log.info("MediaInvolvedAuditMessage complaintEmails={}", complaintEmails);
        emailSet.addAll(complaintEmails);

        // 根据角色的语音电话消息内�?
        Map<String, String> voiceExtMap = new HashMap<>();
        voiceExtMap.put("orgName", orgName);
        voiceExtMap.put("complaintOrderId", complaintOrder.getComplaintNo());

        // 零售通站内信站内�?消息�?
        String message = "门店有新增涉媒投诉单，请及时处理";
        // 零售通App push 消息�?
        String title = "新增涉媒投诉";
        String description = "门店有新增涉媒投诉单，请及时处理";

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .complaintNo(complaintOrder.getComplaintNo())
                .orgId(complaintOrder.getOrgId())
                .pushEnum(MEDIA_INVOLVED_STORE_AUDIT)
                // 零售�?
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                // 零售�?投诉提醒
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_TO_BE_FOLLOWED))
                .auth(false)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, null, title, description))
                .midSet(storeSet)
                .emailSet(emailSet)
                .miOfficePayload(getMiOfficePayload(complaintOrder, complaintOrder.getId()))
                .voiceExt(voiceExtMap)
                .phoneSet(Sets.newHashSet())
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
