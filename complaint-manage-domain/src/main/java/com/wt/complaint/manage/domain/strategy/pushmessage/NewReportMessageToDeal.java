package com.wt.complaint.manage.domain.strategy.pushmessage;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import static com.wt.complaint.manage.domain.api.enums.PushEnum.NEW_REPORT_TO_DEAL_ONLY_MI_PUSH;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service(PushConstant.NEW_REPORT_TO_DEAL)
public class NewReportMessageToDeal extends AbstractComplaintMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(UserComplaintOrderDetailSoOut reportOrder, Map<String,
            String> extraParam) {
        log.info("新增待处理举报单消息组装，reportOrder:{}, extraParam:{}", JacksonUtil.toStr(reportOrder),
                JacksonUtil.toStr(extraParam));
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(reportOrder.getOrgId());

        // 获取岗位对应的邮�?
        Set<String> allEmailSet = new HashSet<>(getFinalEmailSetByZoneAndPosition(reportOrder,
                Arrays.asList(PositionEnum.CITY_SERVICE_MANAGER, PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT)));
        return MessageInformedEvent.builder()
                .requestId(String.format(REPORT_REQUEST_ID_FORMAT,
                        PushConstant.NEW_COMPLAINT_TO_DEAL,
                        reportOrder.getUcNo()))
                .orgId(reportOrder.getOrgId())
                .pushEnum(NEW_REPORT_TO_DEAL_ONLY_MI_PUSH)
                .emailSet(allEmailSet)
                .auth(true)
                .miOfficePayload(getMiOfficePayload(reportOrder, orgName))
                .build();
    }

}
