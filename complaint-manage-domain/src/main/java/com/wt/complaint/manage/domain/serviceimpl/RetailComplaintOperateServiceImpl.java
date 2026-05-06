package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt.car.soc.api.constant.WorkTypeEnum;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.constont.FieldNameConstant;
import com.wt.complaint.manage.api.model.enums.DeliverRetailSourceEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ReminderFlagEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.enums.DeliveryStaffPositionEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.CreateRetailComplaintOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailApplyRetailCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailAuthSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintOrderCreateExpandSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailFollowRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailSubmitFinishApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.UpdateRetailOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.ChangeOrgCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.AddFollowRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.CreateRetailComplaintOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.constant.RetailActionConst;
import com.wt.complaint.manage.domain.converter.DomainConverter;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewComplaintMessageStrategy;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewMessageInformedEventFactory;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventFactory;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Service
@Slf4j
public class RetailComplaintOperateServiceImpl implements RetailComplaintOperateService {

    @Resource
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Resource
    private RedisRemoteGateway redisRemoteGateway;

    @Resource
    private NoGeneratorRemoteGateway noGeneratorRemoteGateway;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private NewMessageInformedEventFactory newMessageInformedEventFactory;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @Resource
    private UserComplaintStatusEventFactory factory;

    @Resource
    private CarDeliveryGateway carDeliveryGateway;

    @Autowired
    private RetailComplaintViewService retailComplaintViewService;

    @Autowired
    private ClueGateway clueGateway;

    @NacosValue(value = "${retailFirstResponseHour}", autoRefreshed = true)
    private Integer retailFirstResponseHour;

    @NacosValue(value = "${retailFirstResponseWorkStartTime}", autoRefreshed = true)
    private Integer retailFirstResponseWorkStartTime;

    @NacosValue(value = "${retailFirstResponseWorkEndTime}", autoRefreshed = true)
    private Integer retailFirstResponseWorkEndTime;

    @NacosValue(value = "${retailL1FinishTimeHour}", autoRefreshed = true)
    private Integer retailL1FinishTimeHour;

    @NacosValue(value = "${retailL2L3L4FinishTimeHour}", autoRefreshed = true)
    private Integer retailL2L3L4finishTimeHour;

    @NacosValue(value = "${deliverFirstResponseHour}", autoRefreshed = true)
    private Integer deliverFirstResponseHour;

    @NacosValue(value = "${deliverFinishTimeHour}", autoRefreshed = true)
    private Integer deliverFinishTimeHour;

    @NacosValue(value = "${complaintSceneA}", autoRefreshed = true)
    private String complaintSceneAStr;

    @NacosValue(value = "${complaintSceneB}", autoRefreshed = true)
    private String complaintSceneBStr;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String POSITION_NAME = "线上客诉处理专家";

    /**
     * 创建投诉�?
     *
     * @param soIn 创建投诉单请求参�?
     * @return 创建投诉单响应结�?
     */
    @Override
    public CreateRetailComplaintOrderSoOut createComplaintOrder(CreateRetailComplaintOrderSoIn soIn) {
        CreateRetailComplaintOrderSoOut soOut = new CreateRetailComplaintOrderSoOut();
        StopWatch stopWatch = new StopWatch("创建客诉�?);
        // 加锁
        stopWatch.start("创建加锁");
        String lockKey = RedisUtil.generateCreateLockKey(soIn.getIdempotentId());
        if (BooleanUtils.isFalse(RedisUtil.tryLock(lockKey))) {
            log.info(
                    "RetailComplaintOperateServiceImpl#createComplaintOrder当前lockKey正被锁，lockkey;{}, idempotentId:{}",
                    lockKey,
                    soIn.getIdempotentId());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在创建中，请稍后再�?);
        }
        stopWatch.stop();
        try {
            // 幂等
            stopWatch.start("DB数据校验-幂等");
            OrderListGoIn listGoIn = new OrderListGoIn();
            listGoIn.setIdempotentId(soIn.getIdempotentId());
            RetailComplaintDetaiGoOut retailComplaintDetaiGoOut =
                    retailComplaintGateway.findByIdempotentId(FindByIdempotentIdGoIn.builder()
                            .idempotentKey(soIn.getIdempotentId())
                            .useMaster(Boolean.TRUE).build());
            if (ObjectUtil.isNotNull(retailComplaintDetaiGoOut)) {
                log.info("RetailComplaintOperateServiceImpl#createComplaintOrder客诉单已创建，idempotentId:{}, soIn：{}",
                        soIn.getIdempotentId(), GsonUtil.toJson(soIn));
                soOut.setWorkNo(retailComplaintDetaiGoOut.getDrNo());
                return soOut;
            }
            stopWatch.stop();
            // 生成客诉单号
            stopWatch.start("工单号生�?);
            // 生成单号
            String drNo = "";
            // 作业单是零售门店投诉�?
            if (WorkTypeEnum.RETAIL_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
                drNo = noGeneratorRemoteGateway.generateUcNoWithPrefix(
                        UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getPrefix());
                // 作业单是交付门店投诉�?
            } else if (WorkTypeEnum.DELIVER_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
                drNo = noGeneratorRemoteGateway.generateUcNoWithPrefix(
                        UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER.getPrefix());
            }
            soIn.setDrNo(drNo);
            stopWatch.stop();
            // 组装客诉单信�?
            stopWatch.start("创建客诉�?);
            RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn = buildComplaintOrder(soIn);
            stopWatch.stop();
            // 查询汽车门店所属大区id，小区id，城市id
            stopWatch.start("客诉单门店信息查�?);
            String orgId = retailComplaintOrderInfoGoIn.getOrgId();
            StoreInfoGoOut carStore = storeRemoteGateway.getStoreInfo(orgId);
            retailComplaintOrderInfoGoIn.setZoneId(carStore.getZoneId());
            retailComplaintOrderInfoGoIn.setLittleZoneId(carStore.getLittleZoneId());
            retailComplaintOrderInfoGoIn.setCityZoneId(carStore.getCityZoneId());
            retailComplaintOrderInfoGoIn
                    .setCityId(StrUtil.isNotBlank(carStore.getCityId()) ? Integer.valueOf(carStore.getCityId()) : null);
            stopWatch.stop();
            //组装派单信息
            buildAssignOrder(retailComplaintOrderInfoGoIn);
            // 校验派单人信�?
            if (ObjectUtil.isNull(retailComplaintOrderInfoGoIn.getOperatorMid()) ||
                    ObjectUtil.isNull(retailComplaintOrderInfoGoIn.getOperatorPositionId())) {
                log.info("该投诉单未查找到合适的跟进人，派单失败，soIn:{}", soIn);
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "该投诉单未查找到合适的跟进人，派单失败");
            }
            // 组装更跟进记�?
            Map<Long, String> userNameMap = eiamRemoteGateway.getNameByMid(Arrays.asList(
                    retailComplaintOrderInfoGoIn.getCreateMid(),
                    retailComplaintOrderInfoGoIn.getOperatorMid()));
            buildComplaintFollowProcess(retailComplaintOrderInfoGoIn, userNameMap);
            // 调用RPC查询线索id
            if (WorkTypeEnum.RETAIL_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
                GetCLueInfoByPhoneGoOut clueInfoByPhone = null;
                String phone = soIn.getContactTel();
                if (StrUtil.isNotBlank(phone)) {
                    clueInfoByPhone = clueGateway.getClueInfoByPhone(
                            GetClueInfoByPhoneGoIn.builder().phone(phone).build());
                }
                if (clueInfoByPhone != null) {
                    retailComplaintOrderInfoGoIn.setClueId(clueInfoByPhone.getClueId());
                }
            }
            // 创建客诉单待首响事件
            UserComplaintStatusEventHandler<RetailComplaintOrderInfoGoIn, Boolean> handler =
                    factory.getStatusEventHandler(
                            UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                            null,
                            RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode());
            log.info("RetailComplaintOperateServiceImpl.createComplaintOrder retailComplaintOrderInfoGoIn:{}",
                    GsonUtil.toJson(retailComplaintOrderInfoGoIn));
            boolean handleResult = handler.handle(retailComplaintOrderInfoGoIn);
            log.info("RetailComplaintOperateServiceImpl.createComplaintOrder handleResult:{}", handleResult);
            if (handleResult) {
                soOut.setWorkNo(retailComplaintOrderInfoGoIn.getDrNo());
                ComplaintBasicInfo complaintBasicInfo = DomainConverter.INSTANCE.convertToBasicInfo(
                        retailComplaintOrderInfoGoIn);
                complaintBasicInfo.setOperatorName(
                        userNameMap.get(retailComplaintOrderInfoGoIn.getOperatorMid()));
                sendNewComplaintOrRemindMsg(complaintBasicInfo,
                        retailComplaintOrderInfoGoIn.getSource(),
                        PushConstant.DELIVER_NEW_COMPLAINT);
                // 创建群聊
                eventPublisher.publishEvent(
                        DomainConverter.INSTANCE.convertToCreateChatGroupEvent(retailComplaintOrderInfoGoIn));
                log.info("time result:{}", stopWatch.prettyPrint());
                return soOut;
            } else {
                log.error("持久化客诉单失败");
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "保存客户投诉信息异常");
            }
        } finally {
            RedisUtil.unlock(lockKey);
        }
    }

    /**
     * 保存跟进记录
     */
    private void buildComplaintFollowProcess(RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                             Map<Long, String> userNameMap) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                // 操作人操作岗�?
                .operateMid(String.valueOf(retailComplaintOrderInfoGoIn.getCreateMid()))
                .operateName(userNameMap.get(retailComplaintOrderInfoGoIn.getCreateMid()))
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0") // 创建人岗位默认设�?线上客诉处理专家
                .operatePositionName(POSITION_NAME)

                .problemCategory(retailComplaintOrderInfoGoIn.getProblemCategory())
                .riskLevel(RiskLevelEnum.getDescByCode(retailComplaintOrderInfoGoIn.getRiskLevel()))
                .orgId(retailComplaintOrderInfoGoIn.getOrgId())
                .orgName(storeRemoteGateway.getStoreInfo(retailComplaintOrderInfoGoIn.getOrgId()).getOrgName())
                // 跟进人跟进岗�?
                .operatorPositionId(retailComplaintOrderInfoGoIn.getOperatorPositionId())
                .operatorPositionName(
                        DeliverPositionEnum.getDescByCode(retailComplaintOrderInfoGoIn.getOperatorPositionId()))
                .questionDescription(retailComplaintOrderInfoGoIn.getProblemDesc())
                .followUpMid(String.valueOf(retailComplaintOrderInfoGoIn.getOperatorMid()))
                .followUpName(userNameMap.get(retailComplaintOrderInfoGoIn.getOperatorMid()))
                .build();
        ComplaintFollowProcessGoIn followUpRecord = ComplaintFollowProcessGoIn.builder()
                .complaintNo(retailComplaintOrderInfoGoIn.getDrNo())
                .processType(ProcessTypeEnum.CREATE_ORDER.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        retailComplaintOrderInfoGoIn.setFollowUpRecord(followUpRecord);
    }

    /**
     * 发送新建客诉单消息或催单消�?
     */
    private void sendNewComplaintOrRemindMsg(ComplaintBasicInfo complaintBasicInfo, Integer source,
                                             String pushConstant) {
        if (Objects.equals(source, DeliverRetailSourceEnum.RETAIL.getCode())) {
            log.info("sendNewComplaintMsg 零售客诉不需要发push消息, complaintBasicInfo:{}",
                    GsonUtil.toJson(complaintBasicInfo));
            return;
        }
        NewComplaintMessageStrategy messageStrategy =
                newMessageInformedEventFactory.getStrategy(pushConstant);
        CompletableFuture.runAsync(() -> {
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintBasicInfo,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
        }, constructMessageEventExecutor).exceptionally(e -> {
            log.error("sendNewComplaintMsg error,创建交付客诉发送消息失�? drNo:{}",
                    complaintBasicInfo.getDrNo(), e);
            return null;
        });
    }

    /**
     * 构建客诉单信�?
     *
     * @param soIn 构建客诉单请求参�?
     * @return 构建客诉单响应结�?
     */
    public RetailComplaintOrderInfoGoIn buildComplaintOrder(CreateRetailComplaintOrderSoIn soIn) {
        log.info("RetailComplaintOperateServiceImpl.buildComplaintOrder soIn:{}",
                GsonUtil.toJson(soIn));
        RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn = new RetailComplaintOrderInfoGoIn();
        RetailComplaintOrderCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();
        List<TemplateStructSoIn> complaintInfo = expandSoIn.getComplaintInfo();
        // 组装扩展信息
        extractExpandInfo(complaintInfo, retailComplaintOrderInfoGoIn);
        retailComplaintOrderInfoGoIn.setIdempotentKey(soIn.getIdempotentId());
        // 作业单是零售门店投诉�?
        if (WorkTypeEnum.RETAIL_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
            retailComplaintOrderInfoGoIn.setSource(DeliverRetailSourceEnum.RETAIL.getCode());
            // 作业单是交付门店投诉�?
        } else if (WorkTypeEnum.DELIVER_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
            retailComplaintOrderInfoGoIn.setSource(DeliverRetailSourceEnum.DELIVER.getCode());
        }
        retailComplaintOrderInfoGoIn.setSuperTicketNo(soIn.getSuperTicketNo());
        retailComplaintOrderInfoGoIn.setSoNo(soIn.getSoNo());
        retailComplaintOrderInfoGoIn.setOrderStatus(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        retailComplaintOrderInfoGoIn.setDrNo(soIn.getDrNo());
        retailComplaintOrderInfoGoIn.setCustomerServiceMid(Long.valueOf(expandSoIn.getCustomerServiceMid()));
        retailComplaintOrderInfoGoIn.setContactNameC(KeyCenterUtil.encrypt(soIn.getContactName()));
        retailComplaintOrderInfoGoIn.setContactNameMd5(KeyCenterUtil.md5(soIn.getContactName()));
        retailComplaintOrderInfoGoIn.setContactPhoneC(KeyCenterUtil.encrypt(soIn.getContactTel()));
        retailComplaintOrderInfoGoIn.setContactPhoneMd5(KeyCenterUtil.md5(soIn.getContactTel()));
        retailComplaintOrderInfoGoIn.setContactGender(soIn.getContactTitle());
        retailComplaintOrderInfoGoIn.setComplaintContent(GsonUtil.toJson(complaintInfo));
        retailComplaintOrderInfoGoIn.setTestTag(soIn.getTestTag());
        // soIn的createMid为超级工单的创建人，本工单为内部工单，客诉单的创建人取跟进客服mid
        retailComplaintOrderInfoGoIn.setCreateMid(Long.valueOf(expandSoIn.getCustomerServiceMid()));
        // 计算期望首响时间和期望结案时�?
        calculateTime(soIn, retailComplaintOrderInfoGoIn);
        // 解析投诉场景字段
        parseComplaintContent(retailComplaintOrderInfoGoIn);
        log.info("RetailComplaintOperateServiceImpl.buildComplaintOrder retailComplaintOrderInfoGoIn:{}",
                GsonUtil.toJson(retailComplaintOrderInfoGoIn));
        return retailComplaintOrderInfoGoIn;
    }

    private void parseComplaintContent(RetailComplaintOrderInfoGoIn goIn) {
        String complaintScene = ParseComplaintContentUtil.parseComplaintScene(goIn.getComplaintContent());
        goIn.setComplaintScene(complaintScene);
        String complaintSceneCode =
                ParseComplaintContentUtil.parseComplaintSceneCode(goIn.getComplaintContent());
        if (StringUtils.isEmpty(complaintSceneCode)) {
            goIn.setLastComplaintSceneId(0);
        } else {
            goIn.setLastComplaintSceneId(Integer.parseInt(complaintSceneCode));
        }
    }

    /**
     * 计算期望首响时间和期望结案时�?
     *
     * @param soIn                         计算入参
     * @param retailComplaintOrderInfoGoIn 计算结果
     */
    private void calculateTime(CreateRetailComplaintOrderSoIn soIn, RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn) {
        // 计算预期首响时间/预期结案时间
        // 获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        retailComplaintOrderInfoGoIn.setCreateTime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        // 作业单是零售门店投诉�?
        if (WorkTypeEnum.RETAIL_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
            log.info("RetailComplaintOperateServiceImpl.calculateTime retailFirstResponseWorkStartTime:{},retailFirstResponseWorkEndTime:{}," +
                            "retailFirstResponseHour:{},retailL1FinishTimeHour:{},retailL2L3L4finishTimeHour:{}", retailFirstResponseWorkStartTime,
                    retailFirstResponseWorkEndTime, retailFirstResponseHour, retailL1FinishTimeHour, retailL2L3L4finishTimeHour);
            // 预期首响时间
            String expectedFirstResponseTime = DateUtil.calculateDeadline(localDateTime,
                    retailFirstResponseWorkStartTime,
                    retailFirstResponseWorkEndTime,
                    retailFirstResponseHour);
            retailComplaintOrderInfoGoIn.setExpectedFirstResponseTime(
                    cn.hutool.core.date.DateUtil.parse(expectedFirstResponseTime,
                            DATE_TIME_FORMAT));
            // 预期结案时间
            String expectedFinishTime;
            if (RiskLevelEnum.LEVEL_1.getCode().equals(retailComplaintOrderInfoGoIn.getRiskLevel())) {
                expectedFinishTime =
                        DateUtil.calculateFutureTime(localDateTime, retailL1FinishTimeHour);
            } else {
                expectedFinishTime =
                        DateUtil.calculateFutureTime(localDateTime, retailL2L3L4finishTimeHour);
            }
            retailComplaintOrderInfoGoIn.setExpectedFinishTime(cn.hutool.core.date.DateUtil.parse(expectedFinishTime,
                    DATE_TIME_FORMAT));
            //作业单是交付门店投诉�?
        } else if (WorkTypeEnum.DELIVER_ORG_COMPLAINT.getId() == soIn.getWorkType()) {
            log.info("RetailComplaintOperateServiceImpl.calculateTime deliverFirstResponseHour:{},deliverFinishTimeHour:{}",
                    deliverFirstResponseHour, deliverFinishTimeHour);
            // 预期首响时间
            String expectedFirstResponseTime =
                    DateUtil.calculateFutureTime(localDateTime, deliverFirstResponseHour);
            retailComplaintOrderInfoGoIn.setExpectedFirstResponseTime(
                    cn.hutool.core.date.DateUtil.parse(expectedFirstResponseTime,
                            DATE_TIME_FORMAT));
            // 预期结案时间
            String expectedFinishTime =
                    DateUtil.calculateFutureTime(localDateTime, deliverFinishTimeHour);
            retailComplaintOrderInfoGoIn.setExpectedFinishTime(
                    cn.hutool.core.date.DateUtil.parse(expectedFinishTime,
                            DATE_TIME_FORMAT));
        }
    }

    /**
     * 解析模板内容
     *
     * @param complaintInfo                模板内容
     * @param retailComplaintOrderInfoGoIn 客诉单信�?
     */
    private void extractExpandInfo(List<TemplateStructSoIn> complaintInfo,
                                   RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn) {
        for (TemplateStructSoIn templateStructSoIn : complaintInfo) {
            for (TemplateFieldSoIn field : templateStructSoIn.getFields()) {
                switch (field.getFieldCode()) {
                    case ComplaintInfoConstant.COMPLAINT_TYPE:
                        setComplaintType(complaintInfo, retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.ORG_ID:
                        setOrg(complaintInfo, retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.RISK_LEVEL:
                        setRiskLevel(complaintInfo, retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.RESPONSIBILITY:
                        setResponsibility(complaintInfo, retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.PROBLEM_CATEGORY:
                        setProblemCategory(complaintInfo, retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.PROBLEM_DESC:
                        setProblemDesc(retailComplaintOrderInfoGoIn, field);
                        break;
                    case ComplaintInfoConstant.TRADE_ORDER_ID:
                        setTradeOrderId(retailComplaintOrderInfoGoIn, field);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 设置订单�?
     *
     * @param retailComplaintOrderInfoGoIn 客户投诉信息
     * @param field                        field
     */
    private static void setTradeOrderId(RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                        TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String valueCode = (String) field.getValueCode();
            retailComplaintOrderInfoGoIn.setTradeOrderId(valueCode);
        }
    }

    /**
     * 设置问题描述
     *
     * @param retailComplaintOrderInfoGoIn 客诉单信�?
     * @param field                        field
     */
    private static void setProblemDesc(RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                       TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String valueCode = (String) field.getValueCode();
            retailComplaintOrderInfoGoIn.setProblemDesc(valueCode);
        }
    }

    /**
     * 设置问题分类
     *
     * @param complaintInfo                客诉单信�?
     * @param retailComplaintOrderInfoGoIn 客诉单信�?
     * @param field                        field
     */
    private static void setProblemCategory(List<TemplateStructSoIn> complaintInfo,
                                           RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                           TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String pathName = field.getValue().get(0).getPathName();
            String pathId = field.getValue().get(0).getPathId();
            // 获取末级问题类目id
            if (StrUtil.isNotBlank(pathId)) {
                String[] pathSegments = pathId.split("/");
                retailComplaintOrderInfoGoIn.setLastCategoryId(
                        Integer.valueOf(pathSegments[pathSegments.length - 1]));
            }
            // 问题类目
            retailComplaintOrderInfoGoIn.setProblemCategory(pathName);
        } else {
            log.error("problemCategory is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "problemCategory is null");
        }
    }

    /**
     * 设置门店是否有责
     *
     * @param complaintInfo                客诉信息
     * @param retailComplaintOrderInfoGoIn 投诉单信�?
     * @param field                        field
     */
    private static void setResponsibility(List<TemplateStructSoIn> complaintInfo,
                                          RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                          TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String valueCode = (String) field.getValueCode();
            Integer responsibility = Integer.valueOf(valueCode);
            retailComplaintOrderInfoGoIn.setResponsible(responsibility);
        } else {
            log.error("responsibility is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "responsibility is null");
        }
    }

    /**
     * 设置风险等级
     *
     * @param complaintInfo                投诉单信�?
     * @param retailComplaintOrderInfoGoIn 投诉单信�?
     * @param field                        field
     */
    private static void setRiskLevel(List<TemplateStructSoIn> complaintInfo,
                                     RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                     TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String valueCode = (String) field.getValueCode();
            Integer riskLevel = Integer.valueOf(valueCode);
            retailComplaintOrderInfoGoIn.setRiskLevel(riskLevel);
        } else {
            log.error("riskLevel is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "riskLevel is null");
        }
    }

    /**
     * 设置门店id
     *
     * @param complaintInfo                客诉单信�?
     * @param retailComplaintOrderInfoGoIn 客诉单信�?
     * @param field                        field
     */
    private static void setOrg(List<TemplateStructSoIn> complaintInfo,
                               RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn, TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String orgId = field.getValue().get(0).getCode();
            retailComplaintOrderInfoGoIn.setOrgId(orgId);
        } else {
            log.error("orgId is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "orgId is null");
        }
    }

    /**
     * 设置投诉类型
     *
     * @param complaintInfo                客诉单信�?
     * @param retailComplaintOrderInfoGoIn 客诉单信�?
     * @param field                        field
     */
    private static void setComplaintType(List<TemplateStructSoIn> complaintInfo,
                                         RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn,
                                         TemplateFieldSoIn field) {
        if (Objects.nonNull(field.getValueCode())) {
            String valueCode = (String) field.getValueCode();
            Integer complaintType = Integer.valueOf(valueCode);
            retailComplaintOrderInfoGoIn.setComplaintType(complaintType);
        } else {
            log.error("complaintType is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "complaintType is null");
        }
    }

    /**
     * 构建派单信息
     *
     * @param soIn 投诉信息
     */
    private void buildAssignOrder(RetailComplaintOrderInfoGoIn soIn) {
        log.info("RetailComplaintOperateServiceImpl.buildAssignOrder:{}", GsonUtil.toJson(soIn));
        if (Objects.equals(soIn.getSource(), DeliverRetailSourceEnum.DELIVER.getCode())) {
            handleDeliveryComplaint(soIn);
        } else {
            handleRetailComplaint(soIn);
        }
    }

    /**
     * 处理交付投诉�?
     *
     * @param soIn 投诉信息
     */
    private void handleDeliveryComplaint(RetailComplaintOrderInfoGoIn soIn) {
        List<String> complaintSceneA;
        List<String> complaintSceneB;
        if (StringUtils.hasText(complaintSceneAStr)) {
            complaintSceneA = JacksonUtil.parseArray(complaintSceneAStr, String.class);
        } else {
            complaintSceneA = new ArrayList<>();
        }
        log.info("RetailComplaintOperateServiceImpl.handleDeliveryComplaint complaintSceneA:{}",
                GsonUtil.toJson(complaintSceneA));
        if (StringUtils.hasText(complaintSceneBStr)) {
            complaintSceneB = JacksonUtil.parseArray(complaintSceneBStr, String.class);
        } else {
            complaintSceneB = new ArrayList<>();
        }
        log.info("RetailComplaintOperateServiceImpl.handleDeliveryComplaint complaintSceneB:{}",
                GsonUtil.toJson(complaintSceneB));
        List<DeliveryStaffGoOut> deliveryStaffGoOuts = getDeliveryStaff(soIn);
        // 解析投诉场景�?级pathId
        String pathId = parseComplaintScene(soIn.getComplaintContent());
        log.info("RetailComplaintOperateServiceImpl.handleDeliveryComplaint pathId:{}", pathId);
        // 匹配上了nacos中配置的B岗id
        if (complaintSceneB.contains(pathId)) {
            handlePositionB(deliveryStaffGoOuts, soIn);
            // 匹配上了nacos中配置的A岗id
        } else if (complaintSceneA.contains(pathId)) {
            handlePositionA(deliveryStaffGoOuts, soIn);
            // 没匹配上也是给A�?
        } else {
            handlePositionA(deliveryStaffGoOuts, soIn);
        }
    }

    /**
     * 根据订单信息查询交付专员信息
     *
     * @param soIn 订单信息
     * @return 交付专员信息
     */
    private List<DeliveryStaffGoOut> getDeliveryStaff(RetailComplaintOrderInfoGoIn soIn) {
        List<DeliveryStaffGoOut> deliveryStaffGoOuts = carDeliveryGateway.listDeliveryStaff(
                DeliveryStaffGoIn.builder()
                        .orderId(Long.valueOf(soIn.getTradeOrderId()))
                        .forceMaster(Boolean.TRUE)
                        .build());
        log.info("RetailComplaintOperateServiceImpl.getDeliveryStaff deliveryStaffGoOuts:{}",
                GsonUtil.toJson(deliveryStaffGoOuts));
        return deliveryStaffGoOuts;
    }

    /**
     * 处理B�?
     *
     * @param deliveryStaffGoOuts 人员列表
     * @param soIn                投诉信息
     */
    private void handlePositionB(List<DeliveryStaffGoOut> deliveryStaffGoOuts,
                                 RetailComplaintOrderInfoGoIn soIn) {
        // 匹配B�?
        List<Long> positionMids = getPositionMids(deliveryStaffGoOuts, DeliveryStaffPositionEnum.POSITION_B.code);
        log.info("RetailComplaintOperateProviderImpl.handlePositionB positionMids:{}", GsonUtil.toJson(positionMids));
        if (CollUtil.isNotEmpty(positionMids)) {
            soIn.setOperatorMid(positionMids.get(0));
            soIn.setOperatorPositionId(DeliverPositionEnum.POSITION_B.getPositionId());
        } else {
            // B岗主管兜�?
            StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
            goIn.setOrgId(soIn.getOrgId());
            List<Integer> positionIdList =
                    Arrays.asList(DeliverPositionEnum.POSITION_B_LEADER.getPositionId(),
                            DeliverPositionEnum.DELIVERY_CENTER_MANAGER.getPositionId());
            goIn.setPositionIdList(positionIdList);
            log.info("RetailComplaintOperateProviderImpl.handlePositionB goIn:{}", GsonUtil.toJson(goIn));
            List<EmployeeInfoGoOut> employeeInfoGoOutList = eiamRemoteGateway.queryEmployeeByStore(goIn);
            log.info("RetailComplaintOperateProviderImpl.handlePositionB employeeInfoGoOutList:{}",
                    GsonUtil.toJson(employeeInfoGoOutList));
            List<EmployeeInfoGoOut> positionBLeaderList = employeeInfoGoOutList.stream()
                    .filter(employee -> Objects.equals(employee.getPositionId(),
                            DeliverPositionEnum.POSITION_B_LEADER.getPositionId()))
                    .collect(Collectors.toList());
            log.info("RetailComplaintOperateProviderImpl.handlePositionB positionBLeaderList:{}",
                    GsonUtil.toJson(positionBLeaderList));
            List<EmployeeInfoGoOut> deliveryCenterManagerList = employeeInfoGoOutList.stream()
                    .filter(employee -> Objects.equals(employee.getPositionId(),
                            DeliverPositionEnum.DELIVERY_CENTER_MANAGER.getPositionId()))
                    .collect(Collectors.toList());
            log.info("RetailComplaintOperateProviderImpl.handlePositionB deliveryCenterManagerList:{}",
                    GsonUtil.toJson(deliveryCenterManagerList));
            // B岗主�?
            if (CollUtil.isNotEmpty(positionBLeaderList)) {
                soIn.setOperatorMid(RandomUtil.randomEle(positionBLeaderList).getMiId());
                soIn.setOperatorPositionId(DeliverPositionEnum.POSITION_B_LEADER.getPositionId());
            } else {
                // 店长
                soIn.setOperatorMid(RandomUtil.randomEle(deliveryCenterManagerList).getMiId());
                soIn.setOperatorPositionId(DeliverPositionEnum.DELIVERY_CENTER_MANAGER.getPositionId());
            }
        }
    }

    /**
     * 处理A�?
     *
     * @param deliveryStaffGoOuts 人员列表
     * @param soIn                投诉信息
     */
    private void handlePositionA(List<DeliveryStaffGoOut> deliveryStaffGoOuts,
                                 RetailComplaintOrderInfoGoIn soIn) {
        // 匹配A�?
        List<Long> positionMids = getPositionMids(deliveryStaffGoOuts, DeliveryStaffPositionEnum.POSITION_A.code);
        log.info("RetailComplaintOperateProviderImpl.handlePositionA positionMids:{}", GsonUtil.toJson(positionMids));
        if (CollUtil.isNotEmpty(positionMids)) {
            soIn.setOperatorMid(positionMids.get(0));
            soIn.setOperatorPositionId(DeliverPositionEnum.POSITION_A.getPositionId());
        } else {
            // A岗主管兜�?
            List<ZonePositionUserGoOut> zoneEmployeeList = getZoneEmployees(
                    DeliverPositionEnum.POSITION_A_LEADER.getPositionId(), soIn.getLittleZoneId());
            log.info("RetailComplaintOperateProviderImpl.handlePositionA zoneEmployeeList:{}", GsonUtil.toJson(zoneEmployeeList));
            if (CollUtil.isNotEmpty(zoneEmployeeList)) {
                soIn.setOperatorMid(RandomUtil.randomEle(zoneEmployeeList).getMid());
                soIn.setOperatorPositionId(DeliverPositionEnum.POSITION_A_LEADER.getPositionId());
            }
        }
    }

    /**
     * 获取岗位人员mid
     *
     * @param deliveryStaffGoOuts 人员列表
     * @param positionCode        岗位code
     * @return mid列表
     */
    private List<Long> getPositionMids(List<DeliveryStaffGoOut> deliveryStaffGoOuts, Integer positionCode) {
        return deliveryStaffGoOuts.stream()
                .filter(staff -> Objects.equals(staff.getPositionId(), positionCode))
                .map(DeliveryStaffGoOut::getMiId)
                .collect(Collectors.toList());
    }

    /**
     * 查询区域人员信息
     *
     * @param positionId 岗位id
     * @param littleZoneId  小区id
     * @return 人员信息
     */
    private List<ZonePositionUserGoOut> getZoneEmployees(Integer positionId, Integer littleZoneId) {
        List<ZonePositionUserGoOut> zoneEmployeeGoOutList = eiamRemoteGateway.getZonePositionUser(
                ZonePositionUserGoIn.builder()
                        .positionId(positionId)
                        .littleZoneIdList(Collections.singletonList(littleZoneId))
                        .build());
        log.info("zoneEmployeeGoOutList:{}", GsonUtil.toJson(zoneEmployeeGoOutList));
        return zoneEmployeeGoOutList;
    }

    /**
     * 处理零售投诉�?
     *
     * @param soIn 零售投诉单信�?
     */
    private void handleRetailComplaint(RetailComplaintOrderInfoGoIn soIn) {
        List<EmployeeInfoGoOut> employeeInfoGoOutList = getStoreEmployees(soIn);
        if (CollUtil.isNotEmpty(employeeInfoGoOutList)) {
            handleStoreEmployees(employeeInfoGoOutList, soIn);
        } else {
            handleFallback(soIn);
        }
    }

    /**
     * 处理门店人员
     *
     * @param soIn 零售投诉单信�?
     * @return 派单结果
     */
    private List<EmployeeInfoGoOut> getStoreEmployees(RetailComplaintOrderInfoGoIn soIn) {
        StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
        goIn.setOrgId(soIn.getOrgId());
        List<Integer> positionIdList =
                Arrays.asList(PositionEnum.CAR_STORE_MANAGER.getCode(), PositionEnum.CAR_STORE_OA.getCode());
        goIn.setPositionIdList(positionIdList);
        List<EmployeeInfoGoOut> employeeInfoGoOutList = eiamRemoteGateway.queryEmployeeByStore(goIn);
        log.info("employeeInfoGoOutList:{}", GsonUtil.toJson(employeeInfoGoOutList));
        return employeeInfoGoOutList;
    }

    /**
     * 处理门店人员
     *
     * @param employeeInfoGoOutList 门店人员列表
     */
    private void handleStoreEmployees(List<EmployeeInfoGoOut> employeeInfoGoOutList,
                                      RetailComplaintOrderInfoGoIn goIn) {
        List<EmployeeInfoGoOut> carStoreManagerList = employeeInfoGoOutList.stream()
                .filter(employee -> Objects.equals(employee.getPositionId(), PositionEnum.CAR_STORE_MANAGER.getCode()))
                .collect(Collectors.toList());
        log.info("RetailComplaintOperateProviderImpl.handleStoreEmployees carStoreManagerList:{}", GsonUtil.toJson(carStoreManagerList));
        List<EmployeeInfoGoOut> carStoreOaList = employeeInfoGoOutList.stream()
                .filter(employee -> Objects.equals(employee.getPositionId(), PositionEnum.CAR_STORE_OA.getCode()))
                .collect(Collectors.toList());
        log.info("RetailComplaintOperateProviderImpl.handleStoreEmployees carStoreOaList:{}", GsonUtil.toJson(carStoreOaList));
        if (CollUtil.isNotEmpty(carStoreManagerList)) {
            goIn.setOperatorMid(RandomUtil.randomEle(carStoreManagerList).getMiId());
            goIn.setOperatorPositionId(PositionEnum.CAR_STORE_MANAGER.getCode());
        } else if (CollUtil.isNotEmpty(carStoreOaList)) {
            goIn.setOperatorMid(RandomUtil.randomEle(carStoreOaList).getMiId());
            goIn.setOperatorPositionId(PositionEnum.CAR_STORE_OA.getCode());
        }
    }

    /**
     * 零售派单兜底
     *
     * @param goIn 零售投诉单信�?
     */
    private void handleFallback(RetailComplaintOrderInfoGoIn goIn) {
        List<Long> littleZoneMids = getZonePositionMids(
                PositionEnum.CAR_MANAGER_CITY.getCode(),
                Collections.singletonList(goIn.getLittleZoneId()),
                null
        );
        log.info("RetailComplaintOperateProviderImpl.handleFallback littleZoneMids:{}", GsonUtil.toJson(littleZoneMids));
        if (ObjectUtil.isNotEmpty(littleZoneMids)) {
            goIn.setOperatorMid(RandomUtil.randomEle(littleZoneMids));
            goIn.setOperatorPositionId(PositionEnum.CAR_MANAGER_CITY.getCode());
        } else {
            List<Long> bigZoneMids = getZonePositionMids(
                    PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode(),
                    null,
                    Collections.singletonList(goIn.getZoneId())
            );
            log.info("RetailComplaintOperateProviderImpl.handleFallback bigZoneMids:{}", GsonUtil.toJson(bigZoneMids));
            if (ObjectUtil.isNotEmpty(bigZoneMids)) {
                goIn.setOperatorMid(RandomUtil.randomEle(bigZoneMids));
                goIn.setOperatorPositionId(PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode());
            }
        }
    }

    /**
     * 根据岗位id和区域id查询人员mid
     *
     * @param positionId       岗位id
     * @param littleZoneIdList 小区域id列表
     * @param bigZoneIdList    大区id列表
     * @return mid列表
     */
    private List<Long> getZonePositionMids(Integer positionId, List<Integer> littleZoneIdList,
                                           List<Integer> bigZoneIdList) {
        List<ZonePositionUserGoOut> zonePositionUser = eiamRemoteGateway.getZonePositionUser(
                ZonePositionUserGoIn.builder()
                        .positionId(positionId)
                        .littleZoneIdList(littleZoneIdList)
                        .bigZoneIdList(bigZoneIdList)
                        .build()
        );
        return zonePositionUser.stream()
                .filter(user -> user.getUserState() == 1)
                .map(ZonePositionUserGoOut::getMid)
                .collect(Collectors.toList());
    }

    /**
     * 解析投诉场景
     *
     * @param json JSON字符�?
     * @return 最后一个pathName
     */
    public static String parseComplaintScene(String json) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR,
                    "解析投诉场景异常,json=" + json, e);
        }
        List<String> pathIds = new ArrayList<>();
        // 遍历JSON数组
        for (JsonNode groupNode : rootNode) {
            JsonNode fieldsNode = groupNode.path("fields");
            if (!fieldsNode.isArray()) continue;

            for (JsonNode fieldNode : fieldsNode) {
                // 检查是否是投诉场景字段
                processComplaintSceneField(fieldNode, pathIds);
            }
        }
        // 获取最后一个pathId
        return getLastPathIds(pathIds);
    }

    /**
     * 解析客诉场景中的Fields
     *
     * @param fieldNode field节点
     * @param pathIds   目标字段中所有符合条件的pathId列表
     */
    private static void processComplaintSceneField(JsonNode fieldNode, List<String> pathIds) {
        // 检查是否为目标字段
        if (fieldNode.path("fieldName").asText().equals(FieldNameConstant.COMPLAINT_SCENE)) {
            JsonNode valueNode = fieldNode.path("value");
            if (valueNode.isArray() && !valueNode.isEmpty()) {
                // 获取第一个value对象中的pathName
                String pathId = valueNode.get(0).path("pathId").asText();
                if (!pathId.isEmpty()) {
                    pathIds.add(pathId);
                }
            }
        }
    }

    /**
     * 获取最后一个pathId
     *
     * @param pathIds pathId列表
     * @return 最后一个pathId
     */
    private static String getLastPathIds(List<String> pathIds) {
        // 提取目标�?
        String targetId = "";
        if (!pathIds.isEmpty()) {
            String path = pathIds.get(0); // 获取第一个字符串
            String[] segments = path.split("/"); // 按斜杠分�?
            if (segments.length > 0) {
                targetId = segments[segments.length - 1]; // 取最后一�?
            }
        }
        return targetId;
    }

    /**
     * 添加跟进记录
     *
     * @param retailFollowRecordSoIn 添加跟进记录入参
     * @return 添加跟进记录出参
     */
    @Override
    public AddFollowRecordSoOut addFollowRecord(RetailFollowRecordSoIn retailFollowRecordSoIn) {
        AddFollowRecordSoOut soOut = new AddFollowRecordSoOut();
        // 获取客诉单号
        RetailComplaintDetaiGoOut retailComplaintDetaiGoOut = retailComplaintGateway.getRetailComplaintDetail(
                RetailComplaintDetailGoIn.builder().drNo(retailFollowRecordSoIn.getDrNo()).build());
        if (Objects.isNull(retailComplaintDetaiGoOut)) {
            log.error("客诉单不存在，retailFollowRecordSoIn:{}", GsonUtil.toJson(retailFollowRecordSoIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR,
                    "该客诉单" + retailFollowRecordSoIn.getDrNo() + "不存�?);
        }
        // 查询用户操作权限
        RetailComplaintDetailFrameSoOut retailComplaintFrame = retailComplaintViewService.getRetailComplaintDetailAuth(
                RetailComplaintDetailAuthSoIn.builder().drNo(retailFollowRecordSoIn.getDrNo())
                        .mid(retailFollowRecordSoIn.getFollowUpMid()).build());
        log.info("RetailComplaintOperateProviderImpl.addFollowRecord retailComplaintFrame:{}",
                GsonUtil.toJson(retailComplaintFrame));
        // 校验是否有权限操作此按钮
        if (ObjectUtil.isNull(retailComplaintFrame) ||
                ObjectUtil.isNull(retailComplaintFrame.getRetailUserActionAuth()) ||
                CollUtil.isEmpty(retailComplaintFrame.getRetailUserActionAuth().getActionsList()) ||
                !retailComplaintFrame.getRetailUserActionAuth().getActionsList()
                        .contains(RetailActionConst.ADD_FOLLOW_UP_RECORDS)) {
            log.error("无权限操作，单据�?{}", retailFollowRecordSoIn.getDrNo());
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "无权限操�?);
        }
        StopWatch stopWatch = new StopWatch();
        // 加锁
        stopWatch.start("添加跟进记录加锁");
        String lockKey = "RC:" + ":operate:" + retailFollowRecordSoIn.getDrNo();
        try {
            if (BooleanUtils.isFalse(RedisUtil.tryLock(lockKey))) {
                log.info(
                        "RetailComplaintOperateServiceImpl#addFollowRecord当前lockKey正被锁，lockkey;{}, drNo:{}",
                        lockKey,
                        retailFollowRecordSoIn.getDrNo());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在添加跟进记录中，请稍后再�?);
            }
            stopWatch.stop();
            // 持久化附件的文件
            List<Long> attachmentFileIdList =
                    retailFollowRecordSoIn.getAttachmentList().stream().map(AttachmentSoIn::getId)
                            .collect(Collectors.toList());
            fileRemoteGateway.fileCommit(attachmentFileIdList);
            // 获取登陆人信�?
            EmployeeListGoIn
                    eiamGoIn =
                    EmployeeListGoIn.builder()
                            .miIdList(Collections.singletonList(Long.valueOf(retailFollowRecordSoIn.getFollowUpMid())))
                            .build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut>
                    employeeMap =
                    employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            retailFollowRecordSoIn.setFollowUpName(
                    employeeMap.containsKey(retailFollowRecordSoIn.getFollowUpMid()) ?
                            employeeMap.get(retailFollowRecordSoIn.getFollowUpMid()).getName() : "");
            // 是否首响
            boolean isFirstResp =
                    RetailComplaintOrderStatusEnum.canFirstResponse(retailComplaintDetaiGoOut.getOrderStatus());
            // 催单标识
            Integer reminderFlag = retailComplaintDetaiGoOut.getReminderFlag();
            // 催单标识
            // 构建跟进记录参数
            ComplaintFollowProcessGoIn complaintFollowProcessGoIn =
                    buildFollowUpRecord(retailComplaintDetaiGoOut, retailFollowRecordSoIn);
            // 构建跟进记录
            UpdateRetailOrderSoIn soIn = UpdateRetailOrderSoIn.builder().drNo(retailFollowRecordSoIn.getDrNo())
                    .orderStatus(RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode())
                    .realFirstResponseTime(cn.hutool.core.date.DateUtil.date())
                    .isFirstResp(isFirstResp)
                    .reminderFlag(reminderFlag)
                    .complaintFollowProcessGoIn(complaintFollowProcessGoIn).build();
            // 创建保存跟进记录事件
            UserComplaintStatusEventHandler<UpdateRetailOrderSoIn, Boolean> handler =
                    factory.getStatusEventHandler(
                            UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                            RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode(),
                            RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode());
            log.info("RetailComplaintOperateProviderImpl.addFollowRecord soIn:{}",
                    GsonUtil.toJson(soIn));
            boolean result = handler.handle(soIn);
            log.info("RetailComplaintOperateProviderImpl.addFollowRecord result:{}", result);
            if (result) {
                soOut.setResult("SUCCESS");
                return soOut;
            } else {
                log.error("添加跟进信息异常，soIn:{}", GsonUtil.toJson(retailFollowRecordSoIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "添加跟进信息异常");
            }
        } finally {
            RedisUtil.unlock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RemindOrderSoOut remindOrder(RetailRemindOrderSoIn soIn) {
        RemindOrderSoOut soOut = new RemindOrderSoOut();
        // 加锁
        String lockKey = "remindOrder:" + RedisUtil.generateRemindKey(soIn.getDrNo());
        if (BooleanUtils.isFalse(RedisUtil.tryLock(lockKey))) {
            log.info("当前lockKey正被锁，lockkey;{}, drNo:{}", lockKey, soIn.getDrNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在催单中，请稍后再�?);
        }
        // 幂等
        try {
            OrderListGoIn listGoIn = new OrderListGoIn();
            listGoIn.setComplaintNo(soIn.getDrNo());
            RetailComplaintDetaiGoOut retailComplaintDetaiGoOut =
                    retailComplaintGateway.getRetailComplaintDetail(RetailComplaintDetailGoIn.builder().drNo(
                            soIn.getDrNo()).build());
            if (Objects.isNull(retailComplaintDetaiGoOut)) {
                log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getDrNo() + "不存�?);
            }
            // 获取登陆人信�?
            EmployeeListGoIn eiamGoIn =
                    EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getReminderMid())))
                            .build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap =
                    employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            soIn.setReminderName(
                    employeeMap.containsKey(soIn.getReminderMid()) ? employeeMap.get(soIn.getReminderMid()).getName() :
                            "");
            soIn.setSource(retailComplaintDetaiGoOut.getSource());

            // 更新催单次数
            DateTime reminderDate = cn.hutool.core.date.DateUtil.date();
            Boolean updateResult = retailComplaintGateway.updateOrderByDrNo(
                    UpdateRetailOrderGoIn.builder().reminderTimes(retailComplaintDetaiGoOut.getReminderTimes() + 1)
                            .lastReminderTime(reminderDate)
                            .drNo(soIn.getDrNo()).build());

            //更新催单标识
            if (ReminderFlagEnum.FALSE.getCode().equals(retailComplaintDetaiGoOut.getReminderFlag()) &&
                    (RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode()
                            .equals(retailComplaintDetaiGoOut.getOrderStatus()) ||
                            RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode()
                                    .equals(retailComplaintDetaiGoOut.getOrderStatus()))) {
                updateResult = retailComplaintGateway.updateOrderByDrNo(
                        UpdateRetailOrderGoIn.builder().reminderFlag(ReminderFlagEnum.TRUE.getCode())
                                .drNo(soIn.getDrNo()).build());
            }

            // 构建催单信息
            ComplaintFollowProcessGoIn recordInfoGoIn =
                    buildRemindRecordInfo(soIn, reminderDate);

            log.info("RetailComplaintOperateProviderImpl.remindOrder recordInfoGoIn:{}",
                    GsonUtil.toJson(recordInfoGoIn));

            // 持久化派单记�?
            Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(
                    recordInfoGoIn);
            if (updateResult && insertRecords) {
                // 发送催单消�?
                ComplaintBasicInfo complaintBasicInfo = DomainConverter.INSTANCE.convertToBasicInfo(
                        retailComplaintDetaiGoOut);
                sendNewComplaintOrRemindMsg(complaintBasicInfo, soIn.getSource(),
                        PushConstant.DELIVER_REMIND);
                soOut.setResult("SUCCESS");
                return soOut;
            } else {
                log.error("更新失败，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新失败");
            }
        } finally {
            RedisUtil.unlock(lockKey);
        }
    }

    /**
     * 构建跟进记录
     *
     * @param goOut 跟进记录出参
     * @param soIn  跟进记录入参
     * @return 跟进记录
     */
    public ComplaintFollowProcessGoIn buildFollowUpRecord(RetailComplaintDetaiGoOut goOut,
                                                          RetailFollowRecordSoIn soIn) {
        // 1. 参数校验（增强空值检查和错误信息�?
        if (soIn == null) {
            log.error("buildFollowUpRecord failed: soIn is null");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "Input parameter soIn cannot be null");
        }

        // 2. 使用静态方法抽取重复转换逻辑
        List<AttachmentGoIn> fileAttachments = convertAttachmentList(soIn.getAttachmentList());

        // 3. 构建跟进记录（使用抽取的方法�?
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .followUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .followUpMid(soIn.getFollowUpMid())
                .followUpName(soIn.getFollowUpName())
                .followUpContent(soIn.getFollowInfo())
                .attachments(fileAttachments)
                .build();

        // 4. 简化条件表达式（使用三元运算符直接返回结果�?
        String processType = RetailComplaintOrderStatusEnum.canFirstResponse(goOut.getOrderStatus())
                ? ProcessTypeEnum.FIRST_RESPONSE.getProcessCode()
                : ProcessTypeEnum.ADD_FOLLOW_RECORD.getProcessCode();

        // 5. 构建最终返回对�?
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getDrNo())
                .processType(processType)
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

    // 6. 抽取的通用转换方法（处理空值安全）
    private List<AttachmentGoIn> convertAttachmentList(List<AttachmentSoIn> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return sourceList.stream()
                .map(this::convertToAttachmentGoIn)
                .collect(Collectors.toList());
    }

    // 7. 抽取的单个对象转换方�?
    private AttachmentGoIn convertToAttachmentGoIn(AttachmentSoIn source) {
        return AttachmentGoIn.builder()
                .id(source.getId())
                .url(source.getUrl())
                .fileName(source.getFileName())
                .type(source.getType())
                .build();
    }

    /**
     * 构建催单记录
     *
     * @param soIn         催单入参
     * @param reminderDate
     * @return 催单记录
     */
    private static ComplaintFollowProcessGoIn buildRemindRecordInfo(RetailRemindOrderSoIn soIn, DateTime reminderDate) {
        // 构建跟进信息
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .remindOrderTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .orderReminderMid(soIn.getReminderMid())
                .orderReminderName(soIn.getReminderName())
                .orderRemindInfo(soIn.getOrderRemindInfo())
                .build();
        // 交付客诉单需要增加岗位信�?
        if (soIn.getDrNo() != null && soIn.getDrNo().startsWith(UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER.getPrefix())) {
            recordInfoGoIn.setOperateMid(soIn.getReminderMid());
            recordInfoGoIn.setOperateName(soIn.getReminderName());
            recordInfoGoIn.setOperatePositionId("0"); // 岗位默认设为 线上客诉处理专家
            recordInfoGoIn.setOperatePositionName(POSITION_NAME);
            recordInfoGoIn.setOperateTime(DateUtil.getTimeStrByDate(reminderDate));
        }
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getDrNo())
                .processType(ProcessTypeEnum.REMIND.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

    @Override
    public String submitFinishApply(RetailSubmitFinishApplySoIn soIn) {
        UserComplaintStatusEventHandler<RetailSubmitFinishApplySoIn, String> handler = factory.getStatusEventHandler(
                UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                soIn.getOrderStatus(),
                RetailComplaintOrderStatusEnum.APPLICATION_FOR_CLOSURE.getCode());
        return handler.handle(soIn);
    }

    @Override
    public void applyFinishCallback(RetailApplyRetailCallBackSoIn soIn) {
        RetailComplaintOrderStatusEnum targetStatus;

        // 只支�?通过 + 驳回�?其他的不支持
        if (ProcessAction.Accept == soIn.getAction()) {
            targetStatus = RetailComplaintOrderStatusEnum.FINISH_COMPLETE;
        } else if (ProcessAction.Refuse == soIn.getAction() || ProcessAction.Cancel == soIn.getAction()) {
            targetStatus = RetailComplaintOrderStatusEnum.IN_PROGRESS;
        } else {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "非法�?BPM 动作�? + soIn.getAction());
        }

        UserComplaintStatusEventHandler<RetailApplyRetailCallBackSoIn, String> handler = factory.getStatusEventHandler(
                UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                soIn.getOrderStatus(),
                targetStatus.getCode());
        handler.handle(soIn);
    }

    @Override
    public RetailComplaintApplySoOut submitChangeOrgApply(RetailComplaintApplySoIn soIn) {
        UserComplaintStatusEventHandler<RetailComplaintApplySoIn, RetailComplaintApplySoOut> handler =
                factory.getStatusEventHandler(
                        UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                        soIn.getOrderStatus(),
                        RetailComplaintOrderStatusEnum.WAIT_CHANGE_ORG.getCode());
        return handler.handle(soIn);
    }

    @Override
    public void applyOrgChangeCallback(ChangeOrgCallBackSoIn soIn) {
        RetailComplaintOrderStatusEnum targetStatus = RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING;
        // 只支�?通过 + 驳回�?其他的不支持
        if (ProcessAction.Accept != soIn.getAction() && ProcessAction.Refuse != soIn.getAction()) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "非法�?BPM 动作�? + soIn.getAction());
        }
        UserComplaintStatusEventHandler<ChangeOrgCallBackSoIn, String> handler = factory.getStatusEventHandler(
                UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getDesc(),
                soIn.getOrderStatus(),
                targetStatus.getCode());
        handler.handle(soIn);
    }
}
