package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.commons.utils.JacksonUtil;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.DELIVER_FINISH_TO_TIMEOUT;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Slf4j
@Service(PushConstant.DELIVER_FINISH_TO_TIMEOUT)
public class DeliverFinishToTimeoutMessage extends AbstractNewComplaintMessageStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                           Map<String, String> extraParam) {
        log.info("交付零售投诉单结案即将超时提醒，DELIVER_FINISH_TO_TIMEOUT, complaintBasicInfo:{}",
                JacksonUtil.toStr(complaintBasicInfo));
        // 消息接收人角色信�? 如投诉人为A岗，则提醒A岗、A岗主管；如投诉人为B岗，则提醒B岗、B岗主管、店�?
        Set<String> allEmailSet = new HashSet<>();
        getEmailSet(complaintBasicInfo, allEmailSet);

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .orgId(complaintBasicInfo.getOrgId())
                .pushEnum(DELIVER_FINISH_TO_TIMEOUT)
                .emailSet(allEmailSet)
                .miOfficePayload(getMiOfficePayloadOnlyOrderId(complaintBasicInfo))
                .build();
    }

}
