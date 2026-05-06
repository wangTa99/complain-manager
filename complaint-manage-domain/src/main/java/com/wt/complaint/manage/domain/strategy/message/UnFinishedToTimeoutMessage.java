package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.UN_FINISHED_TO_TIMEOUT;

/**
 * �?5天无法结案审�?
 * @author kiro
 * @date 2026/1/28
 */
@Slf4j
@Service(PushConstant.UN_FINISHED_TO_TIMEOUT)
public class UnFinishedToTimeoutMessage extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder,
                                                           Map<String, String> extraParam) {
        log.info("投诉单结案超15天未结案提醒，UnFinishedToTimeoutMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));
        // 城市体验专家、区域体验专�?
        Set<String> allEmailSet = getFinalEmailSetByZoneAndPosition(complaintOrder, Arrays.asList(PositionEnum.URBAN_EXPERIENCE_EXPERT, PositionEnum.REGIONAL_EXPERIENCE_EXPERT));
        log.info("UnFinishedToTimeoutMessage allEmailSet={}", allEmailSet);

        return MessageInformedEvent.builder()
                .complaintNo(complaintOrder.getComplaintNo())
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(UN_FINISHED_TO_TIMEOUT)
                .emailSet(allEmailSet)
                .miOfficePayload(getMiOfficePayload(complaintOrder, complaintOrder.getId()))
                .build();
    }


    @NotNull
    private Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, Long auditId) {
        Map<String, String> miOfficePayload = new HashMap<>();
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        miOfficePayload.put("orgName", orgName);
        miOfficePayload.put("days", "15");

        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put("href", pcMainCarMaintenanceUrl + "storeOperation/complaint/complaintListDetails?complaintNo=" + complaintOrder.getComplaintNo());
        return miOfficePayload;
    }

}
