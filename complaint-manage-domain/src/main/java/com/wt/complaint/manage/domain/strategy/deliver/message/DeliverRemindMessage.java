package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.commons.utils.JacksonUtil;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.DELIVER_REMIND;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Slf4j
@Service(PushConstant.DELIVER_REMIND)
public class DeliverRemindMessage extends AbstractNewComplaintMessageStrategy{
    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                           Map<String, String> extraParam) {
        log.info("交付客诉单催单提醒，DELIVER_REMIND complaintBasicInfo:{}",
                JacksonUtil.toStr(complaintBasicInfo));
        Set<String> allEmailSet = new HashSet<>();
        getEmailSet(complaintBasicInfo, allEmailSet);
        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .orgId(complaintBasicInfo.getOrgId())
                .pushEnum(DELIVER_REMIND)
                .emailSet(allEmailSet)
                .miOfficePayload(getMiOfficePayloadOnlyOrderId(complaintBasicInfo))
                .build();
    }
}
