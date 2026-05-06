package com.wt.complaint.manage.domain.strategy.message;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.utils.GsonUtil;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wt.complaint.manage.domain.api.enums.PushEnum.NEW_COMPLAINT_TO_DEAL;


@Slf4j
@Service(PushConstant.NEW_COMPLAINT_TO_DEAL)
public class NewComplaintMessageToDeal extends AbstractMessageInformedStrategy {

    @Override
    public MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam) {
        log.info("新增待处理客诉单消息组装，complaintOrder:{}, extraParam:{}", JacksonUtil.toStr(complaintOrder),
                JacksonUtil.toStr(extraParam));

        // 门店消息接收人角色信�? 店长\服务代表\品牌派驻代表
        List<String> receiverRoleList = Arrays.asList(ProretailRoleEnum.CAR_ORG_MANAGER.getKey(),
                ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey(),
                ProretailRoleEnum.CAR_SERVICE_MANAGER.getKey(),
                ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey());
        // 当前门店名称
        String orgName = this.getOrgNameByOrgId(complaintOrder.getOrgId());
        // 站内�?消息�?
        String message = "您有新投诉单待处理，请及时接单处�?;
        // App push 消息�?
        String title = "新投诉单待跟�?;
        String description = "您有新投诉单待处理，请及时接单处�?;

        Set<String> allEmailSet = new HashSet<>();
        // 门店邮箱
        allEmailSet.addAll(getFinalEmailSetByZoneAndPosition(complaintOrder,
                Arrays.asList(PositionEnum.CITY_SERVICE_MANAGER, PositionEnum.URBAN_EXPERIENCE_EXPERT)));
        allEmailSet.addAll(getFinalEmailSetByRoleAndOrg(complaintOrder,
                Arrays.asList(ProretailRoleEnum.CAR_ORG_MANAGER.getKey(),
                ProretailRoleEnum.CAR_BRAND_REPRESENTATIVE.getKey())));
        log.info("NewComplaintMessageToDeal#createMessageInformedEvent store emailSet:{}", GsonUtil.toJson(allEmailSet));

        // 区域/中台提醒限制：投诉类型为“产品投诉”、“产品风险”且风险等级为�?级”或�?级�? �?投诉类型为“服务投诉�?
        if ((ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode().equals(complaintOrder.getComplaintType())
                || ComplaintTypeEnum.PRODUCT_RISK.getCode().equals(complaintOrder.getComplaintType()))
                && (complaintOrder.getRiskLevel() == 3 || complaintOrder.getRiskLevel() == 4)) {
            // 投诉分类为“产品投�?产品风险”且风险等级为“L3”或“L4�? 区域体验专家、区域运营管理、客诉处�?
            Set<String> regionMailSet = getFinalEmailSetByZoneAndPosition(complaintOrder,
                    Arrays.asList(PositionEnum.REGIONAL_EXPERIENCE_EXPERT,
                            PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT,
                            PositionEnum.COMPLAINT_HANDLING));
            log.info("NewComplaintMessageToDeal#createMessageInformedEvent L3 or L4, regionMailSet:{}", GsonUtil.toJson(regionMailSet));
            allEmailSet.addAll(regionMailSet);
        } else if (ComplaintTypeEnum.SERVICE_COMPLAINT.getCode().equals(complaintOrder.getComplaintType())) {
            Set<String> regionMailSet = getFinalEmailSetByZoneAndPosition(complaintOrder,
                    Arrays.asList(PositionEnum.REGIONAL_EXPERIENCE_EXPERT, PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT, PositionEnum.SATISFACTION_MANAGEMENT));
            log.info("NewComplaintMessageToDeal#createMessageInformedEvent SERVICE_COMPLAINT, regionMailSet:{}", GsonUtil.toJson(regionMailSet));
            allEmailSet.addAll(regionMailSet);
        }
        return MessageInformedEvent.builder()
                .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                .orgId(complaintOrder.getOrgId())
                .pushEnum(NEW_COMPLAINT_TO_DEAL)
                .appIdEnumName(AppIdEnum.NEW_RETAIL_PAD.getName())
                .inboxEnumName(InboxEnum.RETAI_COMPLAINT_NOTICE.getName())
                .roleList(receiverRoleList)
                .emailSet(allEmailSet)
                .midSet(getMidSetByRoleAndOrg(receiverRoleList, complaintOrder.getOrgId()))
                .nrBoxPayload(constructNrBoxPayload(complaintOrder, message, NR_BOX_STATUS_TO_BE_FOLLOWED))
                .auth(true)
                .nrMiPushPayload(getNrMiPushPayload(complaintOrder, orgName, receiverRoleList, title, description))
                .miOfficePayload(getMiOfficePayload(complaintOrder, orgName))
                .build();
    }
}
