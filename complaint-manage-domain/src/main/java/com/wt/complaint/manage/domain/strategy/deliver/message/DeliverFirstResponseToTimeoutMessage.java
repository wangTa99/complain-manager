package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.DELIVER_FIRST_RESPONSE_TO_TIMEOUT;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.FIRST_RESPONSE_TO_TIMEOUT;

/**
 * @author zhangzheyang
 */
@Service(PushConstant.DELIVER_FIRST_RESPONSE_TO_TIMEOUT)
@Slf4j
public class DeliverFirstResponseToTimeoutMessage extends AbstractNewComplaintMessageStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                           Map<String, String> extraParam) {
        log.info("交付零售投诉单首响即将超时提�?DELIVER_FIRST_RESPONSE_TO_TIMEOUT，complaintBasicInfo:{}",
                JacksonUtil.toStr(complaintBasicInfo));

        Set<String> allEmailSet = new HashSet<>();
        getEmailSet(complaintBasicInfo, allEmailSet);

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .orgId(complaintBasicInfo.getOrgId())
                .pushEnum(DELIVER_FIRST_RESPONSE_TO_TIMEOUT)
                .emailSet(allEmailSet)
                .miOfficePayload(getMiOfficePayloadOnlyOrderId(complaintBasicInfo))
                .build();
    }
}
