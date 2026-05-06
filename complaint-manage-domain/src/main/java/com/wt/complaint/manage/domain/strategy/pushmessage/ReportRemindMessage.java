package com.wt.complaint.manage.domain.strategy.pushmessage;

import com.wt.commons.utils.JacksonUtil;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.NEW_REPORT_REMIND_ONLY_MI_PUSH;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 举报单被催单提醒
 *
 * @author p-wangkai95
 * @date 2025/1/1
 */
@Slf4j
@Service(PushConstant.REPORT_REMIND)
public class ReportRemindMessage extends AbstractComplaintMessageInformedStrategy {
    @Override
    public MessageInformedEvent createMessageInformedEvent(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut,
                                                           Map<String, String> extraParam) {
        log.info("举报单被催单提醒，RemindMessage, complaintOrder:{}, extraParam:{}",
                JacksonUtil.toStr(userComplaintOrderDetailSoOut),
                JacksonUtil.toStr(extraParam));
        // 处理�?
        Set<Long> midSet = new HashSet<>();
        midSet.add(userComplaintOrderDetailSoOut.getOperatorMid());

        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(userComplaintOrderDetailSoOut.getOrgId());

        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .orgId(userComplaintOrderDetailSoOut.getOrgId())
                .pushEnum(NEW_REPORT_REMIND_ONLY_MI_PUSH)
                .emailSet(getAllEmailSet(userComplaintOrderDetailSoOut))
                .midSet(midSet)
                .auth(true)
                .miOfficePayload(getMiOfficePayload(userComplaintOrderDetailSoOut, orgName))
                .build();
    }
}
