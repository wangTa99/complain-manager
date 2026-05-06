package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE;

/**
 * 投诉单申�?2H无法结案被驳回提�?
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Slf4j
@Service(PushConstant.APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE)
public class Application72hCannotBeClosedRefuseMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder,
                                                           Map<String, String> extraParam) {
        log.info("投诉单申�?2H无法结案被驳回提醒，Application72hCannotBeClosedRefuseMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));
        // 消息接收人角色信�? 当前门店店长、品牌派驻代表、投诉单处理�?
        List<String> receiverRoleList = new ArrayList<>();
        receiverRoleList.add(ProretailRoleEnum.CAR_ORG_MANAGER.getKey());
        receiverRoleList.add(ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey());
        Set<Long> midSet = getMidSetByRoleAndOrg(receiverRoleList, complaintOrder.getOrgId());
        // 投诉单的处理人可能是服务代表,所以需要在roleList里添加服务代�?
        receiverRoleList.add(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        receiverRoleList.add(ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey());

        midSet.add(complaintOrder.getOperatorMid());

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        // 站内�?消息�?
        String message = "投诉单申�?2H无法结案被驳回，请及时跟�?;
        // App push 消息�?
        String title = "投诉单进度更�?;
        String description = String.format("投诉�?s申请72H无法结案被驳回，请及时跟�?, complaintOrder.getComplaintNo()) ;

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE)
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .roleList(receiverRoleList)
                .midSet(midSet)
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_PROGRESS_UPDATE))
                .auth(true)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, receiverRoleList, title, description))
                .build();
    }
}
