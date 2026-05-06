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

import static com.wt.complaint.manage.domain.api.enums.PushEnum.REMIND;

/**
 * 投诉单被催单提醒
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Slf4j
@Service(PushConstant.REMIND)
public class RemindMessage extends AbstractMessageInformedStrategy {
    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("投诉单被催单提醒，RemindMessage, complaintOrder:{}, extraParam:{}", JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));
        // 消息接收人角色信�? 当前门店店长、品牌派驻代表、投诉单处理�?
        List<String> receiverRoleList = new ArrayList<>();
        receiverRoleList.add(ProretailRoleEnum.CAR_ORG_MANAGER.getKey());
        receiverRoleList.add(ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey());
        Set<Long> midSet = getMidSetByRoleAndOrg(receiverRoleList, complaintOrder.getOrgId());
        midSet.add(complaintOrder.getOperatorMid());
        // 投诉单的处理人可能是服务代表,所以需要在roleList里添加服务代�?
        receiverRoleList.add(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        receiverRoleList.add(ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey());

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        // 站内�?消息�?
        String message = "投诉单被客服催单，请及时跟进";
        // App push 消息�?
        String title = "投诉单进度更�?;
        String description = String.format("投诉�?s被客服催单，请及时跟�?, complaintOrder.getComplaintNo());

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(REMIND)
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .roleList(receiverRoleList)
                .emailSet(getAllEmailSet(complaintOrder))
                .midSet(midSet)
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_PROGRESS_UPDATE))
                .auth(true)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, receiverRoleList, title, description))
                .miOfficePayload(getMiOfficePayload(complaintOrder, orgName))
                .build();
    }
}
