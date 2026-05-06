package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.commons.utils.JacksonUtil;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.DELIVER_REASSIGNMENT;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 交付客诉单改派提�?
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Slf4j
@Service(PushConstant.DELIVER_REASSIGNMENT)
public class DeliverReassignmentMessage extends AbstractNewComplaintMessageStrategy {


    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                           Map<String, String> extraParam) {
        log.info("交付客诉单改派提醒，DELIVER_REASSIGNMENT complaintBasicInfo:{}, extraParam:{}",
                JacksonUtil.toStr(complaintBasicInfo), JacksonUtil.toStr(extraParam));
        Set<String> allEmailSet = new HashSet<>();
        getEmailSet(complaintBasicInfo, allEmailSet);
        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .orgId(complaintBasicInfo.getOrgId())
                .pushEnum(DELIVER_REASSIGNMENT)
                .emailSet(allEmailSet)
                .miOfficePayload(getMiOfficePayload(complaintBasicInfo, extraParam))
                .build();
    }

    private Map<String, String> getMiOfficePayload(ComplaintBasicInfo complaintBasicInfo,
                                                   Map<String, String> extraParam) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("complaintOrderId", complaintBasicInfo.getDrNo());
        miOfficePayload.put(PushConstant.PRE_OPERATOR, extraParam.get(PushConstant.PRE_OPERATOR));
        miOfficePayload.put(PushConstant.REASON, extraParam.get(PushConstant.REASON));
        miOfficePayload.put(PushConstant.REASSIGN_OPERATOR, extraParam.get(PushConstant.REASSIGN_OPERATOR));
        return miOfficePayload;
    }
}
