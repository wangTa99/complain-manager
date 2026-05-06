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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.NEW_COMPLAINT_TO_VIEW;

/**
 * 新投诉单待查阅提�?
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Slf4j
@Service(PushConstant.NEW_COMPLAINT_TO_VIEW)
public class NewComplaintToViewMessage extends AbstractMessageInformedStrategy{

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("新投诉单待查阅提醒，complaintOrder:{}, extraParam:{}", JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));
        // 消息接收人角色信�? 店长\服务代表\品牌派驻代表
        List<String> receiverRoleList = Arrays.asList(ProretailRoleEnum.CAR_ORG_MANAGER.getKey(),
                ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey(),
                ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey(),
                ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey());
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        // 站内�?消息�?
        String message = "您有新投诉单待查阅，请及时查看详�?;
        // App push 消息�?
        String title = "新投诉单待跟�?;
        String description = "您有新投诉单待查阅，请及时查看详�?;

        return MessageInformedEvent.builder()
                .requestId(String.format(COMPLAINT_REQUEST_ID_FORMAT,
                        PushConstant.NEW_COMPLAINT_TO_VIEW,
                        complaintOrder.getComplaintNo()))
                .orgId(complaintOrder.getOrgId())
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .pushEnum(NEW_COMPLAINT_TO_VIEW)
                .roleList(receiverRoleList)
                .midSet(getMidSetByRoleAndOrg(receiverRoleList, complaintOrder.getOrgId()))
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_TO_BE_FOLLOWED))
                .auth(true)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, receiverRoleList, title, description))
                .build();
    }
}
