package com.wt.complaint.manage.infrastructure.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.ComplaintChannelEnum;
import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.nr.messagehub.sdk.dto.bean.MiOfficeEmail;
import com.xiaomi.nr.messagehub.sdk.dto.bean.NrBox;
import com.xiaomi.nr.messagehub.sdk.dto.bean.NrMiPush;
import com.xiaomi.nr.messagehub.sdk.dto.bean.VoiceCallMid;
import com.xiaomi.nr.messagehub.sdk.dto.enums.BaseChannel;
import com.xiaomi.nr.messagehub.sdk.dto.enums.ChannelEnum;
import com.xiaomi.nr.messagehub.sdk.dto.request.SendRequest;
import com.xiaomi.nr.messagehub.sdk.service.ReceiverDubboService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.api.enums.ComplaintChannelEnum.*;

/**
 * 通知消息事件监听�?
 */

@Slf4j
@Component
@EnableAsync
public class MessageInformedEventListener {

    /*    private static final String OPEN_ONLY_TEST_SWITCH = "open";*/

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @DubboReference(interfaceClass = ReceiverDubboService.class, group = "${dubbo.group.message}",
            version = "1.0", timeout = 3000)
    private ReceiverDubboService receiverDubboService;

    @NacosValue(value = "${whiteListStr}", autoRefreshed = true)
    private String whiteListStr;

    @NacosValue(value = "${onlyTestOrgIds}", autoRefreshed = true)
    private String onlyTestOrgIds;

    @NacosValue(value = "${needUpdateRequestIdComplaintNo}", autoRefreshed = true)
    private String needUpdateRequestIdComplaintNo;

    @Value("${spring.profiles.active}")
    private String profile;

    private static final String MSG_SEND_MD5_SALT = "feishu";

    @Async("messageSendChangeExecutor")
    @EventListener
    public void onInformedMessageSend(MessageInformedEvent messageInformedEvent) {
        log.info("onInformedMessageSend开始监�?{}", JacksonUtil.toStr(messageInformedEvent));

        if (messageInformedEvent.getPushEnum() == null) {
            log.warn("messageInformedEvent 没有推送枚�? messageInformedEvent={}",
                    JacksonUtil.toStr(messageInformedEvent));
            return;
        }
        if (BooleanUtils.isFalse(messageInformedEvent.getPushEnum().isOpen())) {
            log.info("messageInformedEvent 当前消息类型,没有开�? messageInformedEvent={}",
                    RetailJsonUtil.toJson(messageInformedEvent));
            return;
        }

        // 发送消�?
        SendRequest sendRequest = constructSendRequest(messageInformedEvent, false);
        if (PushEnum.NEW_COMPLAINT_TO_DEAL == messageInformedEvent.getPushEnum()) {
            // 同一个事�?新投诉单待处�?飞书和站内信这些是两个不同渠�?要特殊处�?
            // 发送站内信和app push消息,sceneId�?00000333
            sendRequest.setEmailList(null);
            send(sendRequest);

            // 参考文�?https://xiaomi.f.mioffice.cn/docx/doxk44XSL3sb4orDRJ73xYFpMMf
            // 飞书消息,sceneId 和站内信不同,�?00000330
            messageInformedEvent.setPushEnum(PushEnum.NEW_COMPLAINT_TO_DEAL_ONLY_MI_PUSH);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            // 发送飞书消�?
            send(feishuSendRequest);
        } else if (PushEnum.REMIND == messageInformedEvent.getPushEnum()) {
            // 同一个事�?新投诉单待处�?飞书和站内信这些是两个不同渠�?要特殊处�?
            // 发送站内信和app push消息,sceneId�?00000337
            sendRequest.setEmailList(null);
            send(sendRequest);

            // 参考文�?https://xiaomi.f.mioffice.cn/docx/doxk44XSL3sb4orDRJ73xYFpMMf
            //飞书消息,sceneId 和站内信不同,�?00000379
            messageInformedEvent.setPushEnum(PushEnum.REMIND_ONLY_MI_PUSH);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            // 发送飞书消�?
            send(feishuSendRequest);
        } else if (PushEnum.FIRST_RESPONSE_TO_TIMEOUT == messageInformedEvent.getPushEnum()) {
            // 同一个事�?首响即将超时,飞书和站内信这些是两个不同渠�?要特殊处�?
            // 发送站内信和app push消息,sceneId�?00000335
            sendRequest.setEmailList(null);
            send(sendRequest);

            // 参考文�?https://xiaomi.f.mioffice.cn/docx/doxk44XSL3sb4orDRJ73xYFpMMf
            //飞书消息,sceneId 和站内信不同,�?00000377
            messageInformedEvent.setPushEnum(PushEnum.FIRST_RESPONSE_TO_TIMEOUT_ONLY_MI_PUSH);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            // 发送飞书消�?
            send(feishuSendRequest);
        } else if (PushEnum.FINISH_TO_TIMEOUT == messageInformedEvent.getPushEnum()) {
            // 同一个事�?结案即将超时,飞书和站内信这些是两个不同渠�?要特殊处�?
            // 发送站内信和app push消息,sceneId�?00000336
            sendRequest.setEmailList(null);
            send(sendRequest);

            // 参考文�?https://xiaomi.f.mioffice.cn/docx/doxk44XSL3sb4orDRJ73xYFpMMf
            //飞书消息,sceneId 和站内信不同,�?00000378
            messageInformedEvent.setPushEnum(PushEnum.FINISH_TO_TIMEOUT_ONLY_MI_PUSH);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            // 发送飞书消�?
            send(feishuSendRequest);
        } else if (PushEnum.NOTIFY_CUSTOMER_SERVICET_MI_PUSH == messageInformedEvent.getPushEnum()) {
            // 举报处理单已完成通知客服,发送站内信和飞书消�?sceneId�?00000900
            sendRequest.setBizType("");
            send(sendRequest);
        } else if (PushEnum.MEDIA_INVOLVED_STORE_AUDIT == messageInformedEvent.getPushEnum()) {
            // 涉媒投诉
            sendRequest.setEmailList(null);
            send(sendRequest);
            messageInformedEvent.setPushEnum(PushEnum.MEDIA_INVOLVED_ZONE_AUDIT);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            sendRequest.setMidList(null);
            send(feishuSendRequest);
        } else if (PushEnum.PRODUCT_RISK_UPGRADE_STORE_AUDIT == messageInformedEvent.getPushEnum()) {
            // 产品风险升级
            sendRequest.setEmailList(null);
            send(sendRequest);
            messageInformedEvent.setPushEnum(PushEnum.PRODUCT_RISK_UPGRADE_ZONE_AUDIT);
            sendRequest.setMidList(null);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            send(feishuSendRequest);
        }  else if (PushEnum.STORE_RESPONSIBLE_AUDIT == messageInformedEvent.getPushEnum()) {
            // 门店有责
            sendRequest.setEmailList(null);
            send(sendRequest);
            messageInformedEvent.setPushEnum(PushEnum.STORE_RESPONSIBLE_AUDIT_FEI_SHU);
            sendRequest.setMidList(null);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            send(feishuSendRequest);
        } else if (PushEnum.STORE_REPORT_CLOSURE == messageInformedEvent.getPushEnum()) {
            // 门店报备投诉单结案完�?
            sendRequest.setEmailList(null);
            send(sendRequest);
            messageInformedEvent.setPushEnum(PushEnum.STORE_REPORT_CLOSURE_FEI_SHU);
            sendRequest.setMidList(null);
            SendRequest feishuSendRequest = constructSendRequest(messageInformedEvent, true);
            send(feishuSendRequest);
        } else {
            send(sendRequest);
        }
    }

    /**
     * 构建消息推送对�?
     * @param messageInformedEvent 消息事件
     * @param isFeishuSend 是否飞书推�?
     * @return 发送结�?
     */
    private SendRequest constructSendRequest(MessageInformedEvent messageInformedEvent, boolean isFeishuSend) {
        SendRequest sendRequest = new SendRequest();
        
        // 设置请求ID
        setRequestId(sendRequest, messageInformedEvent, isFeishuSend);
        
        // 消息发送时间的秒级时间�?
        sendRequest.setCtime(System.currentTimeMillis() / 1000);
        // 业务场景id
        sendRequest.setSceneId(messageInformedEvent.getPushEnum().getSceneId());
        
        // 处理白名单过�?
        ReceiverSets receiverSets = filterReceiverByWhitelist(messageInformedEvent);
        
        sendRequest.setMidList(receiverSets.midSet);
        sendRequest.setEmailList(receiverSets.emailSet);
        sendRequest.setBizType(messageInformedEvent.getOrgId());
        sendRequest.setToChannelList(buildChannelList(messageInformedEvent));
        
        Set<Long> phoneSet = messageInformedEvent.getPhoneSet();
        if (CollUtil.isNotEmpty(phoneSet)) {
            sendRequest.setPhoneList(phoneSet);
        }
        return sendRequest;
    }

    /**
     * 设置请求ID
     */
    private void setRequestId(SendRequest sendRequest, MessageInformedEvent messageInformedEvent, boolean isFeishuSend) {
        if (StringUtils.isNotBlank(needUpdateRequestIdComplaintNo)
                && needUpdateRequestIdComplaintNo.equals(messageInformedEvent.getComplaintNo())) {
            String requestId = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("needUpdateRequestIdComplaintNo:{} , 当前客诉单需要重新发消息, messageInformedEvent={}, requestId={}",
                    needUpdateRequestIdComplaintNo, RetailJsonUtil.toJson(messageInformedEvent), requestId);
            sendRequest.setRequestId(requestId);
        } else {
            String salt = isFeishuSend ? MSG_SEND_MD5_SALT : "";
            sendRequest.setRequestId(KeyCenterUtil.md5(messageInformedEvent.getRequestId() + salt));
        }
    }

    /**
     * 根据白名单过滤接收人
     */
    private ReceiverSets filterReceiverByWhitelist(MessageInformedEvent messageInformedEvent) {
        Set<Long> midSet = messageInformedEvent.getMidSet();
        Set<String> emailSet = messageInformedEvent.getEmailSet();
        
        log.info("old midSet:{}", JacksonUtil.toStr(midSet));
        log.info("old emailSet:{}", JacksonUtil.toStr(emailSet));
        
        if (StringUtils.isBlank(whiteListStr)) {
            return new ReceiverSets(midSet, emailSet);
        }
        
        // 获取白名�?
        List<Long> whiteMidList = JacksonUtil.parseArray(whiteListStr, Long.class);
        List<EmployeeInfoGoOut> whiteUserList =
                eiamRemoteGateway.getEmployeeList(EmployeeListGoIn.builder().miIdList(whiteMidList).build());
        List<String> whiteEmailList = whiteUserList.stream()
                .map(EmployeeInfoGoOut::getEmail)
                .collect(Collectors.toList());
        
        log.info("MessageInformedEventListener whiteMidList:{}, whiteEmailList:{}",
                JacksonUtil.toStr(whiteMidList), JacksonUtil.toStr(whiteEmailList));
        
        // 判断是否需要应用白名单过滤
        if (!shouldApplyWhitelist(messageInformedEvent.getOrgId())) {
            return new ReceiverSets(midSet, emailSet);
        }
        
        // 应用白名单过�?
        Set<Long> filteredMidSet = applyWhitelistFilter(midSet, new HashSet<>(whiteMidList));
        Set<String> filteredEmailSet = applyWhitelistFilter(emailSet, new HashSet<>(whiteEmailList));
        
        log.info("new midSet:{}", JacksonUtil.toStr(filteredMidSet));
        log.info("new emailSet:{}", JacksonUtil.toStr(filteredEmailSet));
        
        return new ReceiverSets(filteredMidSet, filteredEmailSet);
    }

    /**
     * 判断是否应用白名�?
     */
    private boolean shouldApplyWhitelist(String orgId) {
        // 测试环境和预发环境需要发送白名单内的用户，避免测试消息干扰到其他�?
        if ("staging".equals(profile) || "preview".equals(profile)) {
            return true;
        }
        
        // 生产环境白名单只针对指定测试门店生效
        if (StringUtils.isNotBlank(onlyTestOrgIds)) {
            List<String> onlyTestOrgIdList = JacksonUtil.parseArray(onlyTestOrgIds, String.class);
            log.info("MessageInformedEventListener onlyTestOrgIdList:{},orgId:{}",
                    JacksonUtil.toStr(onlyTestOrgIdList), orgId);
            return onlyTestOrgIdList.contains(orgId);
        }
        
        return false;
    }

    /**
     * 应用白名单过�?
     */
    private <T> Set<T> applyWhitelistFilter(Set<T> originalSet, Set<T> whiteSet) {
        if (originalSet == null) {
            return new HashSet<>();
        }
        originalSet.retainAll(whiteSet);
        return originalSet;
    }

    /**
     * 接收人集�?
     */
    private static class ReceiverSets {
        Set<Long> midSet;
        Set<String> emailSet;

        ReceiverSets(Set<Long> midSet, Set<String> emailSet) {
            this.midSet = midSet;
            this.emailSet = emailSet;
        }
    }

    private List<BaseChannel> buildChannelList(MessageInformedEvent messageInformedEvent) {
        List<ComplaintChannelEnum> complaintChannelEnumList =
                messageInformedEvent.getPushEnum().getComplaintChannelEnumList();
        List<BaseChannel> result = new ArrayList<>();
        for (ComplaintChannelEnum complaintChannelEnum : complaintChannelEnumList) {
            if (complaintChannelEnum == NR_BOX) {
                NrBox nrBox = new NrBox(messageInformedEvent.getAppIdEnumName(),
                        messageInformedEvent.getInboxEnumName(),
                        messageInformedEvent.getNrBoxPayload());
                Map<String, Object> extra = new HashMap<>();
                extra.put("role", messageInformedEvent.getRoleList());
                extra.put("auth", messageInformedEvent.getAuth());
                nrBox.setExtra(RetailJsonUtil.toJson(extra));
                nrBox.setChannelName(ChannelEnum.NR_BOX.name());
                result.add(nrBox);
            } else if (complaintChannelEnum == NR_MI_PUSH) {
                NrMiPush miPushChannel = new NrMiPush(messageInformedEvent.getNrMiPushPayload());
                miPushChannel.setChannelName(ChannelEnum.NR_MI_PUSH.name());
                result.add(miPushChannel);
            } else if (complaintChannelEnum == MI_OFFICE_EMAIL) {
                MiOfficeEmail miOfficeEmail = new MiOfficeEmail(messageInformedEvent.getMiOfficePayload());
                miOfficeEmail.setChannelName(ChannelEnum.MI_OFFICE_EMAIL.name());
                result.add(miOfficeEmail);
            } else if (complaintChannelEnum == VOICE) {
                // 收信人mid
                VoiceCallMid voiceCallMid = new VoiceCallMid();
                Map<String, String> payLoad = new HashMap<>();
                payLoad.putAll(messageInformedEvent.getVoiceExt());
                voiceCallMid.setChannelName(ChannelEnum.VOICE_CALL_MID.name());
                voiceCallMid.setPayload(payLoad);
                result.add(voiceCallMid);
            }
        }
        return result;
    }

    private void send(SendRequest sendRequest) {
        if (CollectionUtils.isEmpty(sendRequest.getMidList()) && CollectionUtils.isEmpty(sendRequest.getEmailList())) {
            log.warn("MessageInformedEventListener#send midList and emailList is empty, return, sendRequest:{}",
                    JacksonUtil.toStr(sendRequest));
            return;
        }
        Result<Boolean> res = null;
        try {
            log.info("start call ReceiverDubboService#send, sendRequest:{}", JacksonUtil.toStr(getSendRequest(sendRequest)));
            res = receiverDubboService.send(sendRequest);
            log.info("call AppPushGatewayImpl#send success, sendRequest:{}, res:{}", JacksonUtil.toStr(sendRequest),
                    JacksonUtil.toStr(res));
        } catch (Exception e) {
            log.error("call ReceiverDubboService#send error, sendRequest:{}", JacksonUtil.toStr(sendRequest), e);
            return;
        }
        if (res == null || res.getCode() != GeneralCodes.OK.getCode() || !res.getData()) {
            log.error("call ReceiverDubboService#send fail, sendRequest:{}, res:{}",
                    JacksonUtil.toStr(sendRequest), JacksonUtil.toStr(res));
        }
    }

    private static SendRequest getSendRequest(SendRequest sendRequest) {
        return sendRequest;
    }
}
