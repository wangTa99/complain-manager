package com.wt.complaint.manage.domain.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import static com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum.DELIVERY_CENTER_MANAGER;
import static com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum.POSITION_A_LEADER;
import static com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum.POSITION_B_LEADER;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.http.LarkGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.LarkChatCreateParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.LarkChatMessageParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.SendTextMsgContent;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StoreEmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintExpandBO;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.CreateChatGroupEvent;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import com.wt.maindatacommon.enums.BusinessModeEnums;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 拉群监听器，监听客诉单创建事件，根据客诉单类型，拉取相关人员进群，并发送消�?
 *
 * @author zhangzheyang
 * @date 2025/6/16
 */
@Slf4j
@Component
@EnableAsync
public class CreateChatGroupListener {

    public static final String RETAIL = "RETAIL";

    public static final String DELIVER = "DELIVER";

    /**
     * 飞书拉群，最多一次拉�?0�?
     */
    private static final int MAX_CHAT_GROUP_MEMBER_COUNT = 50;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;

    @Resource
    private LarkGateway larkGateway;

    @Resource
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    /**
     * 总部零售运营中台邮箱前缀列表
     */
    @NacosValue(value = "${headquartersStaffMailPrefixStr}", autoRefreshed = true)
    private String headquartersStaffMailPrefixStr;

    /**
     * 一键拉群后, 交付客诉单跳转链�?
     */
    @Value("${deliverHost}")
    private String deliverHost;

    @Value("${spring.profiles.active}")
    private String profile;


    @NacosValue(value = "${whiteChatMemberListStr}", autoRefreshed = true)
    private String whiteChatMemberListStr;


    /**
     * 监听并处理创建群聊事�?
     */
    @Async("createChatGroupExecutor")
    @EventListener
    public void handleEvent(CreateChatGroupEvent event) {
        log.info("start CreateChatGroupListener#handleEvent, event: {}", GsonUtil.toJson(event));

        // 建群的前提条件是,风险等级为L3或L4,对交付和零售客诉单都生效
        if (!RiskLevelEnum.checkHighLevel(event.getRiskLevel())) {
            log.info("CreateChatGroupListener#handleEvent 风险等级不是L3或L4,不拉�? drNo: {}", event.getDrNo());
            return;
        }

        // 1. 根据订单类型处理
        String orderType = determineOrderType(event.getDrNo());
        if (orderType == null) {
            log.error("CreateChatGroupListener#handleEvent unsupported order type, drNo: {}", event.getDrNo());
            return;
        }
        try {
            // 2. 获取拉群成员
            List<String> emailPrefixList = getMembers(event, orderType);
            if (CollectionUtils.isEmpty(emailPrefixList)) {
                log.warn("CreateChatGroupListener#handleEvent 获取成员为空,不拉�? drNo: {}", event.getDrNo());
                return;
            } else if (emailPrefixList.size() > MAX_CHAT_GROUP_MEMBER_COUNT) {
                log.error("CreateChatGroupListener#handleEvent 成员人数超过可以拉群的最大�?0, 只拉取前50�?drNo:{}, originEmailPrefix:{}",
                        event.getDrNo(), GsonUtil.toJson(emailPrefixList));
                emailPrefixList = emailPrefixList.stream().limit(MAX_CHAT_GROUP_MEMBER_COUNT).collect(Collectors.toList());
            }
            // 去重处理
            emailPrefixList = emailPrefixList.stream().distinct().collect(Collectors.toList());
            log.info("CreateChatGroupListener#handleEvent, emailPrefixList:{}", GsonUtil.toJson(emailPrefixList));
            //测试环境和预发环境需要发送白名单内的用户，避免测试消息干扰到其他�?
            if (("staging".equals(profile) || "preview".equals(profile))
                    && StringUtils.isNotBlank(whiteChatMemberListStr)) {
                List<String> whiteEmailPrefixList = JacksonUtil.parseArray(whiteChatMemberListStr, String.class);
                log.info("CreateChatGroupListener#handleEvent whiteEmailPrefixList:{}",
                        GsonUtil.toJson(whiteEmailPrefixList));
                emailPrefixList.retainAll(whiteEmailPrefixList);
                log.info("CreateChatGroupListener#handleEvent after retainAll, emailPrefixList:{}",
                        GsonUtil.toJson(emailPrefixList));
            }

            // 3. 生成群名�?
            String chatName = generateChatName(event, orderType);

            // 4. 创建群聊
            String chatId = createChatGroup(chatName, emailPrefixList);
            if (StringUtils.isBlank(chatId)) {
                log.error("CreateChatGroupListener#handleEvent createChatGroup failed, drNo: {}", event.getDrNo());
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "创建群聊没有生成群聊id异常");
            }

            // 5. 更新扩展�?
            updateComplaintExpand(event.getDrNo(), chatId, chatName, "");

            // 6. 发送消�?
            String message = generateMessage(event, orderType);
            sendChatMessage(chatId, message);
        } catch (Exception e) {
            log.error("CreateChatGroupListener#handleEvent 拉群失败 error, drNo: {}", event.getDrNo(), e);
            updateComplaintExpand(event.getDrNo(), "", "", e.getMessage());
        }
    }

    /**
     * 确定订单类型
     */
    private String determineOrderType(String drNo) {
        if (drNo.startsWith(UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getPrefix())) {
            return RETAIL;
        } else if (drNo.startsWith(UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER.getPrefix())) {
            return DELIVER;
        }
        return null;
    }

    /**
     * 根据订单类型获取拉群成员
     */
    private List<String> getMembers(CreateChatGroupEvent event, String orderType) {
        if (RETAIL.equals(orderType)) {
            return getRetailMembers(event);
        } else if (DELIVER.equals(orderType)) {
            return getDeliverMembers(event);
        }
        return new ArrayList<>();
    }

    /**
     * 生成群名�?
     */
    private String generateChatName(CreateChatGroupEvent event, String orderType) {
        String dateStr = DateUtil.getDateStrByDate(event.getCreateTime());
        String lastComplaintScene = ParseComplaintContentUtil.getLastCategory(event.getComplaintContent());
        if (RETAIL.equals(orderType)) {
            // 群名称格式类�?【L4】零售客�?RC1234-投诉场景末级-20250616
            return String.format("【L%s】零售客�?%s-%s-%s",
                    event.getRiskLevel(),
                    event.getDrNo(),
                    lastComplaintScene,
                    dateStr);
        } else if (DELIVER.equals(orderType)) {
            return String.format("【L%s】交付客�?%s-%s-%s",
                    event.getRiskLevel(),
                    event.getDrNo(),
                    lastComplaintScene,
                    dateStr);
        }
        return "";
    }

    /**
     * 创建群聊
     */
    private String createChatGroup(String chatName, List<String> emailPrefixList) {
        List<String> userIdList = larkGateway.queryUserIdByEmailPrefix(emailPrefixList);
        if (CollUtil.isEmpty(userIdList)) {
            log.error("CreateChatGroupListener#handleEvent createChatGroup failed, emailPrefixList: {}", emailPrefixList);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "创建群聊没有有效用户异常");
        }
        LarkChatCreateParam larkChatCreateParam = new LarkChatCreateParam();
        larkChatCreateParam.setName(chatName);
        larkChatCreateParam.setUserIdList(userIdList);
        return larkGateway.createChat(larkChatCreateParam);
    }

    /**
     * 更新扩展�?
     */
    private void updateComplaintExpand(String drNo, String chatId, String chatName, String createChatFailReason) {
        DeliverComplaintExpandBO deliverComplaintExpandBO = new DeliverComplaintExpandBO();
        deliverComplaintExpandBO.setDrNo(drNo);
        deliverComplaintExpandBO.setChatId(chatId);
        deliverComplaintExpandBO.setChatName(chatName);
        deliverComplaintExpandBO.setCreateChatFailReason(createChatFailReason);
        deliverComplaintExpandGateway.updateSelective(deliverComplaintExpandBO);
    }

    /**
     * 生成消息内容
     */
    private String generateMessage(CreateChatGroupEvent event, String orderType) {
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(event.getOrgId());
        String orgName = storeInfo == null ? "" : storeInfo.getOrgName();

        if (RETAIL.equals(orderType)) {
            String contactName = KeyCenterUtil.decrypt(event.getContactNameC());
            String contactPhone = KeyCenterUtil.decrypt(event.getContactPhoneC());
            return String.format("门店接到高风险投诉，请将相关人员拉入群内，尽快处理\n" +
                    "投诉单号�?s\n" +
                    "联系人姓�?%s\n" +
                    "联系人手机号:%s\n" +
                    "投诉门店:%s\n" +
                    "问题详情:%s",
                    event.getDrNo(),
                    contactName,
                    contactPhone,
                    orgName,
                    event.getProblemDesc());
        } else if (DELIVER.equals(orderType)) {
            Map<Long, String> nameByMid =
                    eiamRemoteGateway.getNameByMid(Collections.singletonList(event.getOperatorMid()));
            // 链接待确�?
            String href = String.format("%s/main-car-delivery/task/complaintTicketDetail?id=%s",
                    deliverHost, event.getDrNo());
            return String.format("接到高风险投诉（风险等级L%s），请将相关人员拉入群内，尽快处理。\n" +
                    "投诉单号�?s %s\n" +
                    "跟进门店�?s\n" +
                    "跟进人员�?s",
                    event.getRiskLevel(),
                    event.getDrNo(),
                    href,
                    orgName,
                    nameByMid.getOrDefault(event.getOperatorMid(), ""));
        }
        return "";
    }

    /**
     * 发送群消息
     */
    private void sendChatMessage(String chatId, String message) {
        LarkChatMessageParam larkChatMessageParam = new LarkChatMessageParam();
        larkChatMessageParam.setReceiveId(chatId);
        SendTextMsgContent sendTextMsgContent = new SendTextMsgContent();
        sendTextMsgContent.setText(message);
        larkChatMessageParam.setContent(GsonUtil.toJson(sendTextMsgContent));
        larkGateway.sendMessage(larkChatMessageParam);
    }

    /**
     * 获取交付拉群成员�?
     *   1. 投诉A岗：对应A岗、A岗主管、B岗主管、店长、区域邀约经理、大区总、二线客服（中台运营，由A岗主�?店长拉进群）
     *   2. 投诉B岗：对应B岗、B岗主管、店长、区域邀约经理、大区总、A岗主管、二线客服（中台运营，由A岗主�?店长拉进群）
     *   3. 投诉A岗主管：对应A岗主管、B岗主管、店长、区域邀约经理、大区总、二线客服（中台运营，由A岗主�?店长拉进群）
     *   4. 投诉B岗主管：对应B岗主管、店长、区域邀约经理、大区总、A岗主管、二线客服（中台运营，由A岗主�?店长拉进群）
     *   5. 投诉店长：对应店长、区域邀约经理、大区总、A岗主管、二线客服（中台运营，由A岗主�?店长拉进群）
     */
    private List<String> getDeliverMembers(CreateChatGroupEvent event) {
        List<String> emailPrefixList = new ArrayList<>();
        if (DeliverPositionEnum.POSITION_A.getPositionId().equals(event.getOperatorPositionId()) ||
                DeliverPositionEnum.POSITION_B.getPositionId().equals(event.getOperatorPositionId())) {
            // 跟进人是A岗或B岗场�?
            // 查询A岗主�?
            getZonePositionUsers(null, event.getLittleZoneId(), POSITION_A_LEADER.getPositionId(), emailPrefixList);
            // 查询B岗主管和店长
            getStoreEmployees(event.getOrgId(),
                    Arrays.asList(POSITION_B_LEADER.getPositionId(), DELIVERY_CENTER_MANAGER.getPositionId()),
                    emailPrefixList);
        } else if (POSITION_A_LEADER.getPositionId().equals(event.getOperatorPositionId())) {
            // 跟进人是 A岗主管场�?
            // 查询B岗主管和店长
            getStoreEmployees(event.getOrgId(),
                    Arrays.asList(POSITION_B_LEADER.getPositionId(), DELIVERY_CENTER_MANAGER.getPositionId()),
                    emailPrefixList);
        } else if (POSITION_B_LEADER.getPositionId().equals(event.getOperatorPositionId())) {
            // 跟进人是B岗主�?
            // 查询A岗主�?
            getZonePositionUsers(null, event.getLittleZoneId(), POSITION_A_LEADER.getPositionId(), emailPrefixList);
        } else if (DELIVERY_CENTER_MANAGER.getPositionId().equals(event.getOperatorPositionId())) {
            // 跟进人是店长场景
            // 查询A岗主�?
            getZonePositionUsers(null, event.getLittleZoneId(), POSITION_A_LEADER.getPositionId(), emailPrefixList);
        }

        // 区域邀约经�?
        getZonePositionUsers(event.getZoneId(), null, DeliverPositionEnum.REGIONAL_INVITE_MANAGER.getPositionId(),
                emailPrefixList);

        // 大区�?
        getZonePositionUsers(event.getZoneId(), null, DeliverPositionEnum.REGIONAL_DELIVERY_HEAD.getPositionId(),
                emailPrefixList);

        List<Long> midList = new ArrayList<>();
        midList.add(event.getOperatorMid());
        midList.add(event.getCustomerServiceMid());
        // 查询mid对应的邮箱前缀
        addEmailPrefixesFromMids(midList, emailPrefixList);

        return emailPrefixList;
    }

    /**
     * 获取区域职位用户的邮箱前缀并添加到列表
     */
    private void getZonePositionUsers(Integer zoneId, Integer littleZoneId, Integer positionId,
                                      List<String> emailPrefixList) {
        ZonePositionUserGoIn goIn = new ZonePositionUserGoIn();
        if (zoneId != null) {
            goIn.setBigZoneIdList(Collections.singletonList(zoneId));
        }
        if (littleZoneId != null) {
            goIn.setLittleZoneIdList(Collections.singletonList(littleZoneId));
        }
        goIn.setPositionId(positionId);
        List<ZonePositionUserGoOut> users = eiamRemoteGateway.getZonePositionUser(goIn);
        if (!CollectionUtils.isEmpty(users)) {
            List<String> zonePositionUserEmailPrefixList = users.stream()
                    .map(e -> fillEmailPrefix(e.getEmail()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            emailPrefixList.addAll(zonePositionUserEmailPrefixList);
            log.info("CreateChatGroupListener#getZonePositionUsers 获取大区职位用户的邮箱前缀, zoneId: {}, positionId: {}, " +
                            "zonePositionUserEmailPrefixList: {}",
                    zoneId, positionId, GsonUtil.toJson(zonePositionUserEmailPrefixList));
        }
    }

    /**
     * 获取门店员工(B岗主管、店�?的邮箱前缀并添加到列表
     */
    private void getStoreEmployees(String orgId, List<Integer> positionIdList, List<String> emailPrefixList) {
        StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
        goIn.setOrgId(orgId);
        goIn.setPositionIdList(positionIdList);
        List<EmployeeInfoGoOut> employees = eiamRemoteGateway.queryEmployeeByStore(goIn);
        if (!CollectionUtils.isEmpty(employees)) {
            List<String> storeEmployeeEmailPrefixList = employees.stream()
                    .map(EmployeeInfoGoOut::getEmailPrefix)
                    .collect(Collectors.toList());
            emailPrefixList.addAll(storeEmployeeEmailPrefixList);
            log.info("CreateChatGroupListener#getStoreEmployees 获取门店员工(B岗主管、店�?的邮箱前缀, orgId: {}, positionIdList: {}, emailPrefixs: {}",
                    orgId, GsonUtil.toJson(positionIdList), GsonUtil.toJson(storeEmployeeEmailPrefixList));
        }
    }

    /**
     * 根据mid列表获取员工邮箱前缀并添加到列表
     */
    private void addEmailPrefixesFromMids(List<Long> midList, List<String> emailPrefixList) {
        Map<Long, CarEmployee> midCarEmployeeMap = carEmployeeRemoteGateway.queryCarEmployee(midList);
        if (!CollectionUtils.isEmpty(midCarEmployeeMap)) {
            List<String> carEmployeeEmailPrefixList = midCarEmployeeMap.values().stream()
                    .map(e -> fillEmailPrefix(e.getEmail()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            emailPrefixList.addAll(carEmployeeEmailPrefixList);
            log.info("CreateChatGroupListener#addEmailPrefixesFromMids 获取员工邮箱前缀, midList: {}, emailPrefixs: {}",
                    GsonUtil.toJson(midList), GsonUtil.toJson(carEmployeeEmailPrefixList));
        }
    }

    /**
     * 获取零售拉群成员列表
     * 1. 直营门店：投诉门店零售店长，门店所在省分车业务负责人、城市经理、总部零售运营中台对接人（mid配置），二线客服�?
     * 2. 商门店：门店所在省分车业务负责人、城市经理、总部零售运营中台对接人（mid配置），二线客服。（不支持拉商门店店长，需要群内人员手动拉店长�?
     */
    private List<String> getRetailMembers(CreateChatGroupEvent event) {
        // 判断门店类型
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(event.getOrgId());
        List<String> emailPrefixList = new ArrayList<>();
        if (storeInfo == null) {
            log.error("CreateChatGroupListener#getRetailMembers getStoreInfo failed, orgId: {}", event.getOrgId());
            return emailPrefixList;
        }
        if (BusinessModeEnums.CAR_MI_MANAGEMENT.getName().equals(storeInfo.getBusinessMode())) {
            // 门店类型为直营店�?投诉门店店长，门店所在省分副总、城市经理、总部零售运营中台对接人，二线客服�?
            StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
            goIn.setOrgId(event.getOrgId());
            goIn.setPositionIdList(Collections.singletonList(PositionEnum.CAR_STORE_MANAGER.getCode()));
            List<EmployeeInfoGoOut> employeeInfoGoOutList = eiamRemoteGateway.queryEmployeeByStore(goIn);
            if (!CollectionUtils.isEmpty(employeeInfoGoOutList)) {
                emailPrefixList.addAll(employeeInfoGoOutList
                        .stream()
                        .map(EmployeeInfoGoOut::getEmailPrefix)
                        .collect(Collectors.toList()));
            }
            log.info("CreateChatGroupListener#getRetailMembers 门店类型为直营店, 拉取店长, orgId: {}, emailPrefixList: {}",
                    event.getOrgId(), GsonUtil.toJson(emailPrefixList));
        } else if (BusinessModeEnums.CAR_AUTHORITY.getName().equals(storeInfo.getBusinessMode()) ||
                BusinessModeEnums.CAR_AGENCY.getName().equals(storeInfo.getBusinessMode())) {
            // 门店类型是商门店, 不拉取店长，其他正常拉取
            log.info("CreateChatGroupListener#getRetailMembers 门店类型是商门店, 不拉取店�? orgId: {}",
                    event.getOrgId());
        } else {
            log.error("CreateChatGroupListener#getRetailMembers 门店不是直营店也不是商门�?异常情况不拉�? orgId: {}",
                    event.getOrgId());
            return new ArrayList<>();
        }

        // 2. 获取省副总邮箱前缀
        Integer zoneId = event.getZoneId();
        if (zoneId != null) {
            ZonePositionUserGoIn goIn = new ZonePositionUserGoIn();
            goIn.setBigZoneIdList(Collections.singletonList(zoneId));
            goIn.setPositionId(PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode());
            // 获取省副总邮箱前缀
            List<ZonePositionUserGoOut> zonePositionUserGoOuts = eiamRemoteGateway.getZonePositionUser(goIn);
            if (!CollectionUtils.isEmpty(zonePositionUserGoOuts)) {
                List<String> businessManagerEmailPrefixList = zonePositionUserGoOuts.stream()
                    .map(e -> fillEmailPrefix(e.getEmail()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                emailPrefixList.addAll(businessManagerEmailPrefixList);
                log.info("CreateChatGroupListener#getRetailMembers 获取省副总邮箱前缀, zoneId: {}, businessManagerEmailPrefixList: {}",
                        zoneId, GsonUtil.toJson(businessManagerEmailPrefixList));
            }
        }

        // 3. 获取城市经理邮箱前缀
        Integer littleZoneId = event.getLittleZoneId();
        if (littleZoneId != null) {
            ZonePositionUserGoIn goIn = new ZonePositionUserGoIn();
            goIn.setLittleZoneIdList(Collections.singletonList(littleZoneId));
            goIn.setPositionId(PositionEnum.CAR_MANAGER_CITY.getCode());
            // 获取城市经理邮箱前缀
            List<ZonePositionUserGoOut> zonePositionUserGoOuts = eiamRemoteGateway.getZonePositionUser(goIn);
            if (!CollectionUtils.isEmpty(zonePositionUserGoOuts)) {
                List<String> cityManagerEmailPrefixList = zonePositionUserGoOuts.stream()
                    .map(e -> fillEmailPrefix(e.getEmail()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                emailPrefixList.addAll(cityManagerEmailPrefixList);
                log.info("CreateChatGroupListener#getRetailMembers 获取城市经理邮箱前缀, littleZoneId: {}, cityManagerEmailPrefixList: {}",
                        littleZoneId, GsonUtil.toJson(cityManagerEmailPrefixList));
            }
        }

        // 4. 总部零售运营中台邮箱前缀
        if (StringUtils.isNotBlank(headquartersStaffMailPrefixStr)) {
            emailPrefixList.addAll(JacksonUtil.parseArray(headquartersStaffMailPrefixStr, String.class));
        }

        // 5. 二线客服的邮箱前缀
        String customerEmailPrefix =
                fillEmailPrefix(carEmployeeRemoteGateway.queryEmailByMid(event.getCustomerServiceMid()));
        if (StringUtils.isNotBlank(customerEmailPrefix)) {
            emailPrefixList.add(customerEmailPrefix);
            log.info("CreateChatGroupListener#getRetailMembers 获取二线客服邮箱前缀, customerServiceMid: {}, customerEmailPrefix: {}",
                    event.getCustomerServiceMid(), customerEmailPrefix);
        }
        return emailPrefixList;
    }

    private String fillEmailPrefix(String email) {
        if (StringUtils.isNotBlank(email) && email.contains("@") && email.contains("xiaomi.com")) {
            return email.substring(0, email.indexOf("@"));
        }
        return null;
    }
}
