package com.wt.complaint.manage.domain.strategy.consult.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.NEW_CONSULT_REMIND_ONLY;
import static com.wt.complaint.manage.domain.constant.ComplaintInfoConstant.ORG_NAME;


@Slf4j
@Service(PushConstant.CONSULT_REMIND)
public class ConsultRemindMessage extends AbstractConsultMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(UserConsultOrderInfo soOut, Map<String, String> extraParam) {
        log.info("咨询单被催单提醒，RemindMessage, soOut:{}, extraParam:{}", JacksonUtil.toStr(soOut),
                JacksonUtil.toStr(extraParam));
        // 消息接收人角色信�? 已接单给跟进�?未接单给服务主管
        List<String> receiverRoleList = new ArrayList<>();
        receiverRoleList.add(ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey());
        Set<Long> midSet = new HashSet<>();
        //未接单给服务主管
        if(soOut.getOrderStatus() == 1){
           midSet = getMidSetByRoleAndOrg(receiverRoleList, soOut.getOrgId());
        }else{
            midSet.add(soOut.getOperatorMid());
        }

        // 站内�?消息�?
        String message = "咨询单被客服催单，请及时跟进";

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .orgId(soOut.getOrgId())
                .pushEnum(NEW_CONSULT_REMIND_ONLY)
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                .inboxEnumName(InboxEnum.CONSULT_NOTICE.getName())
                .roleList(receiverRoleList)
                .midSet(midSet)
                .nrBoxPayload(constructNrBoxPayload(soOut, message, NR_BOX_STATUS_PROGRESS_UPDATE,extraParam))
                .auth(true)
                .build();
    }


    public Map<String, String> constructNrBoxPayload(UserConsultOrderInfo soOut, String message,
                                                     Integer status, Map<String, String> extraParam) {
        Map<String, Object> nrBoxExt = new HashMap<>();
        nrBoxExt.put("carNo", soOut.getCarNo());
        nrBoxExt.put("carType", this.getCarInfoByVid(soOut.getVid()) == null ? "" :
                this.getCarInfoByVid(soOut.getVid()).getCarType());
        // 站内信扩展消息内�?
        String vin = this.getVinByVid(soOut.getVid());
        nrBoxExt.put("vin", vin);
        nrBoxExt.put("consultType", soOut.getConsultType());
        nrBoxExt.put("status", status);
        nrBoxExt.put("message", message);
        nrBoxExt.put("orgId", soOut.getOrgId());
        nrBoxExt.put(ORG_NAME, this.getOrgNameByOrgId(soOut.getOrgId()));
        nrBoxExt.put("pageUrl",
                "consultationOrder/ConsultationOrderDetail?isFullScreen=true&consultNo=" + soOut.getConsultNo());
        // 触发提醒时间
        nrBoxExt.put("pushTime", DateUtil.getTimeStrByDate(new Date()));
        nrBoxExt.put("consultNo", soOut.getConsultNo());
        Map<String, String> payLoad = new HashMap<>();
        payLoad.put("content", JacksonUtil.toStr(nrBoxExt));
        return payLoad;
    }

    }
