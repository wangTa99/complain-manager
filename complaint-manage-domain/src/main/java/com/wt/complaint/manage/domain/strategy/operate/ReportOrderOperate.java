package com.wt.complaint.manage.domain.strategy.operate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.StopWatch;
import com.wt.car.soc.api.constant.WorkTypeEnum;
import com.wt.complaint.manage.api.model.enums.JudgeTypeEnum;
import com.wt.complaint.manage.api.model.enums.RelationOrderEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NoGeneratorRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FinishOrderStatusMqMessageGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcExpandOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderExpandGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderExpandGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateExpandSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.KeyWordConstant;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.constant.ReportActionConst;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.enumInfo.WorkFinishTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.domain.model.UserComplaintExpandInfo;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import com.wt.complaint.manage.domain.model.UserComplaintRelateInfo;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.statemachine.StateMachine;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/21 20:46
 */
@Slf4j
@Service(StrategyConstant.REPORT_ORDER_OPERATE)
public class ReportOrderOperate extends AbstractOperate {
    @Resource
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;

    @Resource
    private NoGeneratorRemoteGateway noGeneratorRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Resource
    private RmqGateway rmqGateway;

    @Resource
    private ComplaintMessageInformedEventFactory complaintMessageInformedEventFactory;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @Resource
    private ReportAuthManager reportAuthManager;

    @Resource(name = "reportOrderStateMachine")
    private StateMachine<ReportOrderStatusEnum, UcOrderEventEnum, UcOrderContext> stateMachine;

    public static final UcOrderTypeEnum UC_TYPE = UcOrderTypeEnum.REPORT_ORDER;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderSoIn soIn) {
        // 业务逻辑校验
        CreateOrderBizCheck(soIn);

        // 生成单号
        String ucNo = noGeneratorRemoteGateway.generateUcNoWithPrefix(UC_TYPE.getPrefix());

        // 创建举报�?
        UserComplaintOrderInfo userComplaintOrderInfo = buildMainInfo(soIn, ucNo);
        UcOrderInfoGoIn ucOrderInfoGoIn = Convert.convert(UcOrderInfoGoIn.class, userComplaintOrderInfo);
        ucOrderInfoGoIn.setUcType(UcOrderTypeEnum.REPORT_ORDER.getCode());
        userComplaintOrderGateway.createUserComplaintOrder(ucOrderInfoGoIn);

        // 创建举报单扩展表数据
        UserComplaintExpandInfo userComplaintExpandInfo = buildExpandInfo(soIn, ucNo);
        UcOrderExpandGoIn ucOrderExpandGoIn = Convert.convert(UcOrderExpandGoIn.class, userComplaintExpandInfo);
        userComplaintOrderGateway.createUserComplaintOrderExpand(ucOrderExpandGoIn);

        // 创建举报单与维保工单关联关系
        UserComplaintRelateInfo userComplaintRelateInfo = buildRelateInfo(soIn, ucNo);
        ComplaintRelationOrderGoIn complaintRelationOrderGoIn =
                Convert.convert(ComplaintRelationOrderGoIn.class, userComplaintRelateInfo);
        complaintRelationOrderGoIn.setComplaintNo(ucNo);
        complaintRelationOrderRepositoryGateway.save(complaintRelationOrderGoIn);

        // 发送消息提�?
        StopWatch stopWatch = new StopWatch("创建举报�?);
        stopWatch.start("消息发�?);
        UserComplaintOrderDetailSoOut soOut =
                userComplaintOrderGateway.selectDetailByUcNo(ucNo);
        sendCreateMsg(soOut);
        stopWatch.stop();
        log.info("time result:{}", stopWatch.prettyPrint());

        return ucNo;
    }

    @Override
    public String judgeOrder(JudgeOrderSoIn soIn) {
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut = getUcOrderList(soIn.getUcNo());

        // 权限控制
        UserComplaintOrderDetailSoOut detailMes =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        if (!reportAuthManager.hasDetailActionAuth(Long.valueOf(soIn.getUserMid()),
                ReportActionConst.REPORT_JUDGMENT, detailMes)) {
            log.error("接单�?{} 无权限接单，单据号：{}", soIn.getUserMid(), soIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "接单人无权限接单");
        }

        // 获取登陆人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder()
                .miIdList(Collections.singletonList(Long.valueOf(soIn.getUserMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        Map<String, EmployeeInfoGoOut>
                employeeMap =
                employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));

        // 执行举报判定事件
        UcOrderContext context = UcOrderContext.builder()
                .ucNo(soIn.getUcNo())
                .operateMid(soIn.getUserMid())
                .operateName(
                        employeeMap.containsKey(soIn.getUserMid()) ? employeeMap.get(soIn.getUserMid()).getName() : "")
                .operateContent(soIn.getJudgeContent())
                .operateType(soIn.getJudgeType())
                .attachmentList(soIn.getAttachmentList())
                .build();
        executeAction(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().get(0).getOrderStatus(),
                UcOrderEventEnum.JUDGE_ORDER, context);

        // 更新判定结果
        UcOrderExpandGoIn ucOrderExpandGoIn =
                UcOrderExpandGoIn.builder().ucNo(soIn.getUcNo()).build();
        UserComplaintOrderExpandGoOut userComplaintOrderExpandGoOut =
                userComplaintOrderGateway.searchUserComplaintExpandData(ucOrderExpandGoIn);
        if (CollUtil.isEmpty(userComplaintOrderExpandGoOut.getUserComplaintExpandInfoList())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "举报单扩展信息不存在");
        }
        UcExpandOrderGoIn ucExpandOrderGoIn = UcExpandOrderGoIn.builder()
                .ucNo(soIn.getUcNo())
                .judgeType(soIn.getJudgeType())
                .build();
        userComplaintOrderGateway.updateExpandSelective(ucExpandOrderGoIn);

        // 作业单完成，通知soc
        sendFinishMessage(soIn);

        // 发送消息提�?
        StopWatch stopWatch = new StopWatch("举报判定");
        stopWatch.start("消息发�?);
        UserComplaintOrderDetailSoOut soOut =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        sendCustomerServiceMsg(soOut);
        stopWatch.stop();
        log.info("time result:{}", stopWatch.prettyPrint());
        return soIn.getUcNo();
    }

    private void sendFinishMessage(JudgeOrderSoIn judgeOrderSoIn) {
        FinishOrderStatusMqMessageGoIn finishMrOrderStatusMqMessageBO = FinishOrderStatusMqMessageGoIn
                .builder()
                .operateType(WorkFinishTypeEnum.COMPLETED.getCode())
                .workNo(judgeOrderSoIn.getUcNo())
                .workType(WorkTypeEnum.USER_SUPERVISION.getId())
                .build();
        boolean sendFinishMq = rmqGateway.mrOrderStatusFinishMessage(finishMrOrderStatusMqMessageBO);
        if (!sendFinishMq) {
            log.error("onStatusChangeTransactionCommitAfter 发送mq失败");
        }
    }

    /**
     * 执行动作
     *
     * @param statusType 当前状�?
     * @param event 事件
     * @param context 上下�?
     */
    @Override
    void executeAction(int statusType, UcOrderEventEnum event, UcOrderContext context) {
        ReportOrderStatusEnum statusEnum = ReportOrderStatusEnum.getByCode(statusType);
        stateMachine.executeAction(statusEnum, event, context);
    }

    /**
     * 校验业务逻辑，是否满足建�?
     *
     * @param soIn
     */
    private void CreateOrderBizCheck(CreateOrderSoIn soIn) {
        // 幂等校验
        UcOrderInfoGoIn ucOrderInfoGoIn = new UcOrderInfoGoIn();
        ucOrderInfoGoIn.setIdempotentKey(soIn.getIdempotentId());
        ucOrderInfoGoIn.setMaster(true);
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut =
                userComplaintOrderGateway.searchUserComplaintMainData(ucOrderInfoGoIn);
        if (CollUtil.isNotEmpty(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "举报单已存在");
        }
    }

    private UserComplaintOrderInfo buildMainInfo(CreateOrderSoIn soIn, String ucNo) {
        UserComplaintOrderInfo userComplaintOrderInfo = Convert.convert(UserComplaintOrderInfo.class, soIn);
        userComplaintOrderInfo.setUcNo(ucNo);
        // 设置幂等key
        userComplaintOrderInfo.setIdempotentKey(soIn.getIdempotentId());
        // 联系人、手机号加密数据
        userComplaintOrderInfo.setContactNameC(KeyCenterUtil.encrypt(soIn.getContactName()));
        userComplaintOrderInfo.setContactPhoneC(KeyCenterUtil.encrypt(soIn.getContactTel()));
        userComplaintOrderInfo.setOrgId(soIn.getExpandSoIn().getOrgId());
        // 举报详情内容
        userComplaintOrderInfo.setComplaintContent(GsonUtil.toJson(soIn.getExpandSoIn().getComplaintInfo()));
        // 举报单状态：待接�?
        userComplaintOrderInfo.setOrderStatus(ReportOrderStatusEnum.PENDING_ORDER.getCode());
        // 获取门店id
        Object fieldsValue = soIn.getExpandSoIn().getFieldsValue("orgId");
        if (fieldsValue == null) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "门店id不能为空");
        }
        soIn.setOrgId(String.valueOf(fieldsValue));
        userComplaintOrderInfo.setOrgId(String.valueOf(fieldsValue));

        return userComplaintOrderInfo;
    }

    /**
     * 生成举报单扩展表数据
     */
    private UserComplaintExpandInfo buildExpandInfo(CreateOrderSoIn soIn, String ucNo) {
        UserComplaintExpandInfo expandInfo = new UserComplaintExpandInfo();
        ComplaintOrderCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();

        expandInfo.setUcNo(ucNo);
        expandInfo.setReminderTimes(0);

        // 获取门店信息：大区、小区、城�?
        String orgId = soIn.getOrgId();
        StoreInfoGoOut carStore = storeRemoteGateway.getStoreInfo(orgId);
        expandInfo.setZoneId(Objects.nonNull(carStore.getZoneId()) ? carStore.getZoneId() : 0);
        expandInfo.setLittleZoneId(Objects.nonNull(carStore.getLittleZoneId()) ? carStore.getLittleZoneId() : 0);
        expandInfo.setCityId(Objects.nonNull(carStore.getCityId()) ? Integer.parseInt(carStore.getCityId()) : 0);
        // 获取服务场景
        Object fieldsValue = expandSoIn.getFieldsValue("serviceScene");
        expandInfo.setServiceScene(CollUtil.toList(String.valueOf(fieldsValue).split(",")));
        // 联系电话md5加密
        expandInfo.setContactPhoneMd5(KeyCenterUtil.md5(soIn.getContactTel()));
        // 手机号后4�?
        if (StringUtils.isNotEmpty(soIn.getContactTel()) &&
                soIn.getContactTel().length() >= KeyWordConstant.PHONE_SUFFIX_LEN) {
            expandInfo.setContactPhoneSuffix(
                    Integer.valueOf(soIn.getContactTel()
                            .substring(soIn.getContactTel().length() - KeyWordConstant.PHONE_SUFFIX_LEN)));
        }
        // vin�?�?
        List<CarInfoGoOut> carSimpleInfo = carRemoteGateway.getCarSimpleInfo(CollUtil.toList(soIn.getVid()), null);
        if (CollUtil.isEmpty(carSimpleInfo)) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "车辆信息查询失败");
        }
        CarInfoGoOut carInfoGoOut = carSimpleInfo.get(0);
        String vin = carInfoGoOut.getVin();
        if (StringUtils.isNotBlank(vin)) {
            expandInfo.setVinSuffix(vin.substring(vin.length() - KeyWordConstant.VIN_SUFFIX_LEN));
        }
        // 判定结果
        expandInfo.setJudgeType(JudgeTypeEnum.NOT_JUDGE.getCode());
        // 联系人尊�?
        expandInfo.setContactGender(soIn.getContactTitle());
        // 车牌�?
        expandInfo.setCarNo(expandSoIn.getCarNo());
        // 客服处理人mid
        expandInfo.setCustomerServiceMid(Long.parseLong(expandSoIn.getCustomerServiceMid()));

        return expandInfo;
    }

    private UserComplaintRelateInfo buildRelateInfo(CreateOrderSoIn soIn, String ucNo) {
        ComplaintOrderCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();
        UserComplaintRelateInfo userComplaintRelateInfo = new UserComplaintRelateInfo();
        userComplaintRelateInfo.setUcNo(ucNo);
        Object fieldsValue = expandSoIn.getFieldsValue("relatedOrder");
        if (fieldsValue == null) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "维保单号不能为空");
        }
        userComplaintRelateInfo.setBizNo(String.valueOf(fieldsValue));
        userComplaintRelateInfo.setBizType(RelationOrderEnum.SUPER_TICKET_NO.getCode());

        return userComplaintRelateInfo;
    }

    private void sendCreateMsg(UserComplaintOrderDetailSoOut soOut) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("获取消息处理策略");
        ComplaintMessageInformedStrategy messageStrategy =
                complaintMessageInformedEventFactory.getStrategy(PushConstant.NEW_REPORT_TO_DEAL);
        stopWatch.stop();
        stopWatch.start("创建消息发送事件并发布消息");
        CompletableFuture.runAsync(() -> {
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(soOut,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞创建举报单主流�?
            log.error("sendCreateMsg error, 创建订单发送消息失�? ComplaintOrderGoOut:{}", RetailJsonUtil.toJson(soOut),
                    e);
            return null;
        });
        stopWatch.stop();
        log.info("ComplaintOperateServiceImpl#sendCreateMsg, time result:{}", stopWatch.prettyPrint());
    }

    private void sendCustomerServiceMsg(UserComplaintOrderDetailSoOut soOut) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("获取消息处理策略");
        ComplaintMessageInformedStrategy messageStrategy =
                complaintMessageInformedEventFactory.getStrategy(PushConstant.NOTIFY_CUSTOMER_SERVICE);
        stopWatch.stop();
        stopWatch.start("创建消息发送事件并发布消息");
        CompletableFuture.runAsync(() -> {
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(soOut,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞举报判定主流程
            log.error("sendCustomerServiceMsg error, 举报判定发送消息失�? UserComplaintOrderDetailSoOut:{}",
                    RetailJsonUtil.toJson(soOut),
                    e);
            return null;
        });
        stopWatch.stop();
        log.info("ComplaintOperateServiceImpl#sendCustomerServiceMsg, time result:{}", stopWatch.prettyPrint());
    }
}
