package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.DELIVER_FINISH_TO_CUSTOMER_SERVICE;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Resource;

/**
 * 交付客诉单关单通知客服消息组装
 * @author zhangzheyang
 * @date 2025/6/25
 */
@Service(PushConstant.DELIVER_FINISH_TO_CUSTOMER_SERVICE)
@Slf4j
public class DeliverFinishToCustomerServiceMessage extends AbstractNewComplaintMessageStrategy {

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;



    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                           Map<String, String> extraParam) {
        log.info("DeliverFinishToCustomerServiceMessage DELIVER_FINISH_TO_CUSTOMER_SERVICE 交付客诉单关单通知客服, stNo:{}, drNo:{}",
                complaintBasicInfo.getStNo(), complaintBasicInfo.getDrNo());
        if (complaintBasicInfo.getCustomerServiceMid() == null) {
            log.error("DeliverFinishToCustomerServiceMessage 未分配客�? complaintBasicInfo:{}",
                    JacksonUtil.toStr(complaintBasicInfo));
            return new MessageInformedEvent();
        }
        Set<Long> midSet = new HashSet<>();
        midSet.add(complaintBasicInfo.getCustomerServiceMid());
        Set<String> emailSet = new HashSet<>();
        String customerServiceEmail = carEmployeeRemoteGateway.queryEmailByMid(complaintBasicInfo.getCustomerServiceMid());
        emailSet.add(customerServiceEmail);


        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .pushEnum(DELIVER_FINISH_TO_CUSTOMER_SERVICE)
                .appIdEnumName(AppIdEnum.CAR_MANAGER.getName())
                .inboxEnumName(InboxEnum.NOTICE.getName())
                .emailSet(emailSet)
                .midSet(midSet)
                .nrBoxPayload(constructNrBoxPayload(complaintBasicInfo))
                .auth(false)
                .miOfficePayload(getMiOfficePayload(complaintBasicInfo))
                .build();
    }


}
