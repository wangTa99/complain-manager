package com.wt.complaint.manage.domain.strategy.message;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.STORE_RESPONSIBLE_AUDIT;

/**
 * 服务投诉判责结果为门店有责时
 *
 * @author kiro
 * @date 2026/2/28
 */
@Slf4j
@Service(PushConstant.STORE_RESPONSIBLE_AUDIT)
public class StoreResponsibleAuditMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("服务投诉判责结果为门店有责，StoreResponsibleAuditMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        // 消息接收人角色信�? 当前门店店长、服务体验专家（这些通过语音短信、零售通push、零售通站内信、飞书机器人�?
        Set<Long> midSet = getMidSetByPositionIdListAndOrg(Lists.newArrayList(PositionEnum.CAR_SERVICE_STORE_MANAGER.getCode(), CarEmployeeEnum.RECEIVER_EXPERT.getCode()), complaintOrder.getOrgId());
        // 投诉单处理人
        if (complaintOrder.getOperatorMid() != null && !Objects.equals(complaintOrder.getOperatorMid(), 0L)) {
            midSet.add(complaintOrder.getOperatorMid());
        } else {
            log.warn("StoreResponsibleAuditMessage#createMessageInformedEvent 当前处理人mid不合�? operatorMid={}",
                    complaintOrder.getOperatorMid());
        }
        log.info("StoreResponsibleAuditMessage midSet={}", midSet);

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());

        // 查询mid对应的邮�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(new ArrayList<>(midSet)).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        Set<String> emailSet = employeeList.stream().map(EmployeeInfoGoOut::getEmail).collect(Collectors.toSet());

        // 零售通站内信站内�?消息�?
        String message = "服务投诉被判定为门店有责，请及时关注�?;
        // 零售通App push 消息�?
        String title = "服务投诉判定门店有责";
        String description = String.format("服务投诉被判定为门店有责，投诉单�?s，请及时关注", complaintOrder.getComplaintNo());

        return MessageInformedEvent.builder()
                .requestId(String.format(AUDIT_COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.STORE_RESPONSIBLE_AUDIT,
                        complaintOrder.getComplaintNo(),
                        complaintOrder.getId()))
                .complaintNo(complaintOrder.getComplaintNo())
                .orgId(complaintOrder.getOrgId())
                .pushEnum(STORE_RESPONSIBLE_AUDIT)
                // 零售�?
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                // 零售�?投诉提醒
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_TO_BE_FOLLOWED))
                .auth(false)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, null, title, description))
                .midSet(midSet)
                .emailSet(emailSet)
                .miOfficePayload(getMiOfficePayload(complaintOrder))
                .phoneSet(Sets.newHashSet())
                .build();
    }


    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder) {
        Map<String, String> miOfficePayload = new HashMap<>();
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        miOfficePayload.put("orgName", orgName);

        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put("href", pcMainCarMaintenanceUrl + "storeOperation/complaint/complaintListDetails?complaintNo=" + complaintOrder.getComplaintNo());
        return miOfficePayload;
    }
}
