package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.OnlyViewEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ReviewedEnum;
import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.domain.aggregation.ComplaintOrderAggregation;
import com.wt.complaint.manage.domain.aggregation.ComplaintOrderAggregationFactory;
import com.wt.complaint.manage.domain.aggregation.ComplaintOrderBatchAggregation;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FinishOrderStatusMqMessageGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintApplyService;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.SubmitReviewSoOut;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.converter.DomainConverter;
import com.wt.complaint.manage.domain.enumInfo.WorkFinishTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComplaintOperateServiceImpl implements ComplaintOperateService {

    private static final String ADD_FOLLOW_RECORD_ERROR_MSG = "添加跟进信息异常";

    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;
    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Resource
    private ComplaintGateway complaintGateway;
    @Resource
    private ComplaintTagGateway complaintTagGateway;
    @Resource
    private RedisRemoteGateway redisRemoteGateway;
    @Resource
    private NoGeneratorRemoteGateway noGeneratorRemoteGateway;
    @Resource
    private CarRemoteGateway carRemoteGateway;
    @Resource
    private StoreRemoteGateway storeRemoteGateway;
    @Resource
    private EiamRemoteGateway eiamRemoteGateway;
    @Resource
    private FileRemoteGateway fileRemoteGateway;
    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;
    @Resource
    private RmqGateway rmqGateway;
    @Resource
    private ComplaintEditTransactionService complaintEditTransactionService;

    @Resource
    private ComplaintSubmitReviewTransactionService complaintSubmitReviewTransactionService;

    @Resource
    private ComplaintApplyService complaintApplyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ComplaintOrderCreateSoOut createComplaintOrder(ComplaintOrderCreateSoIn soIn) {
        ComplaintOrderCreateSoOut soOut = new ComplaintOrderCreateSoOut();
        soIn.checkCreateSoIn();
        StopWatch stopWatch = new StopWatch("创建客诉�?);
        // 加锁
        stopWatch.start("创建加锁");
        String lockKey = RedisUtil.generateCreateLockKey(soIn.getIdempotentId());
        if (!redisRemoteGateway.lock(lockKey, 10L, TimeUnit.SECONDS)) {
            log.info("当前lockKey正被锁，lockkey;{}, idempotentId:{}", lockKey, soIn.getIdempotentId());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在创建中，请稍后再�?);
        }
        stopWatch.stop();
        try {
            // 幂等
            stopWatch.start("DB数据校验-幂等");
            OrderListGoIn listGoIn = new OrderListGoIn();
            listGoIn.setIdempotentId(soIn.getIdempotentId());
            List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
            if (CollUtil.isNotEmpty(orderList)) {
                log.info("客诉单已创建，idempotentId:{}, soIn:{}", soIn.getIdempotentId(), GsonUtil.toJson(soIn));
                soOut.setComplaintNo(orderList.get(0).getComplaintNo());
                return soOut;
            }
            stopWatch.stop();
            return doCreateComplaintOrder(soIn, stopWatch);
        } finally {
            redisRemoteGateway.unLock(lockKey);
        }
    }

    private ComplaintOrderCreateSoOut doCreateComplaintOrder(ComplaintOrderCreateSoIn soIn, StopWatch stopWatch) {
        // 生成客诉单号
        stopWatch.start("工单号生�?);
        soIn.setComplaintNo(noGeneratorRemoteGateway.generateComplaintNo());
        stopWatch.stop();

        // 查询vin
        if (StringUtils.isNotEmpty(soIn.getVid())) {
            stopWatch.start("查询车辆信息");
            List<CarInfoGoOut> carList = carRemoteGateway.getCarSimpleInfo(Collections.singletonList(soIn.getVid()), null);
            if (CollUtil.isNotEmpty(carList)) {
                CarInfoGoOut carInfoGoOut = carList.get(0);
                soIn.setVin(carInfoGoOut.getVin());
                soIn.setCarType(carInfoGoOut.getCarType());
            }
            stopWatch.stop();
        }

        // 创建客诉�?
        stopWatch.start("创建客诉�?);
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation();
        complaintOrderAggregation.createComplaintOrder(soIn);
        stopWatch.stop();

        // 查询汽车门店所属大区id，小区id，城市id
        stopWatch.start("客诉单门店信息查�?);
        String orgId = complaintOrderAggregation.getComplaintOrderInfoGoIn().getOrgId();
        StoreInfoGoOut carStore = storeRemoteGateway.getStoreInfo(orgId);
        complaintOrderAggregation.getComplaintOrderInfoGoIn().setZoneId(Objects.nonNull(carStore.getZoneId()) ? carStore.getZoneId().toString() : "");
        complaintOrderAggregation.getComplaintOrderInfoGoIn().setLittleZoneId(Objects.nonNull(carStore.getLittleZoneId()) ? carStore.getLittleZoneId().toString() : "");
        complaintOrderAggregation.getComplaintOrderInfoGoIn().setCityId(Objects.nonNull(carStore.getCityId()) ? carStore.getCityId().toString() : "");
        stopWatch.stop();

        // 持久化客诉单
        stopWatch.start("客诉单持久化");
        Boolean saveResult = complaintOrderRepositoryGateway.saveComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        Boolean tagSave = true;
        if (Objects.nonNull(complaintOrderAggregation.getTagSoIn())) {
            tagSave = complaintTagGateway.insertTag(complaintOrderAggregation.getTagSoIn());
        }
        stopWatch.stop();

        if (!saveResult || !tagSave) {
            log.error("持久化客诉单失败");
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "保存客户投诉信息异常");
        }

        ComplaintOrderInfoGoIn orderInfo = complaintOrderAggregation.getComplaintOrderInfoGoIn();
        Integer onlyView = orderInfo.getOnlyView();

        // 发送消息提�?
        stopWatch.start("消息发�?);
        sendCreateMsg(orderInfo);
        stopWatch.stop();

        // 如果满足服务投诉判责条件时持久化审批�?
        stopWatch.start("根据条件持久化服务投诉判责审批单");
        if (Objects.isNull(complaintOrderAggregation.getTagSoIn())) {
            complaintApplyService.persistComplaintAdjudicationApplyRecord(orderInfo, carStore.getOrgName());
        }
        stopWatch.stop();

        log.info("time result:{}", stopWatch.prettyPrint());

        ComplaintOrderCreateSoOut soOut = new ComplaintOrderCreateSoOut();
        soOut.setComplaintNo(orderInfo.getComplaintNo());

        // 如果是仅查阅单，通知工单，该单子已经完结
        if (Objects.equals(onlyView, OnlyViewEnum.YES.getCode())) {
            stopWatch.start("客诉单完成状态同步至工单");
            FinishOrderStatusMqMessageGoIn finishMrOrderStatusMqMessageBO = FinishOrderStatusMqMessageGoIn
                .builder()
                .operateType(WorkFinishTypeEnum.COMPLETED.getCode())
                .workNo(soOut.getComplaintNo())
                .workType(20)
                .build();
            rmqGateway.mrOrderStatusFinishDelayMessage(finishMrOrderStatusMqMessageBO);
            stopWatch.stop();
        }
        return soOut;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderPickUpSoOut pickUpOrder(OrderPickUpSoIn soIn) {
        OrderPickUpSoOut soOut = new OrderPickUpSoOut();
        soIn.checkPickUpSoIn();
        log.info("开始处理接单， soIn:{}", GsonUtil.toJson(soIn));
        // 获取待接单的客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("客诉单不存在�?soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }

        // 填充接单人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getPickUpMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setPickUpName(employeeMap.containsKey(soIn.getPickUpMid()) ? employeeMap.get(soIn.getPickUpMid()).getName() : "");
        // 创建客诉�?
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.pickUpComplaintOrder(soIn);
        // 持久化客诉单
        Boolean updateResult = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        // 持久化跟进记�?
        Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (updateResult && insertRecords) {
            soOut.setResult(CommonConst.SUCCESS);
            return soOut;
        } else {
            log.error("更新失败�?soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderUpdateHandlerSoOut updateHandler(OrderUpdateHandlerSoIn soIn) {
        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        soIn.checkUpdateHandlerSoIn();
        // 获取待接单的客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }
        
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);

        // 填充接单人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getHandlerMid()), Long.valueOf(soIn.getDispatcherMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setHandlerName(employeeMap.containsKey(soIn.getHandlerMid()) ? employeeMap.get(soIn.getHandlerMid()).getName() : "");
        soIn.setDispatcherName(employeeMap.containsKey(soIn.getDispatcherMid()) ? employeeMap.get(soIn.getDispatcherMid()).getName() : "");
        // 创建客诉�?
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.updateHandler(soIn);
        // 持久�?
        Boolean updateResult = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        // 持久化派单记�?
        Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (updateResult && insertRecords) {
            soOut.setResult(CommonConst.SUCCESS);
            return soOut;
        } else {
            log.error("更新失败，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新失败");
        }
    }

    /**
     * 客诉二期之后，此接口废弃，迭代几个版本后删除
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn) {
        OrderFollowUpRecordSoOut soOut = new OrderFollowUpRecordSoOut();
        soIn.checkAddFollowUpRecordSoIn();
        // 获取待接单的客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }

        // 持久化附件的文件
        List<Long> fileIdList = soIn.getAttachmentList().stream().map(e -> e.getId()).collect(Collectors.toList());
        fileRemoteGateway.fileCommit(fileIdList);
        // 获取登陆人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getFollowUpMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setFollowUpName(employeeMap.containsKey(soIn.getFollowUpMid()) ? employeeMap.get(soIn.getFollowUpMid()).getName() : "");

        // 创建跟进记录对象及客诉单对象
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.addFollowUpRecord(soIn);
        // 持久�?
        Boolean insertOrder = true;
        Boolean insertRecords = true;
        if (Objects.nonNull(complaintOrderAggregation.getComplaintOrderInfoGoIn())) {
            insertOrder = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        }
        // 持久化跟进记�?
        insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (insertOrder && insertRecords) {
            soOut.setRecordResult(CommonConst.SUCCESS);
            return soOut;
        } else {
            log.error("addFollowUpRecords 添加跟进信息异常, soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, ADD_FOLLOW_RECORD_ERROR_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderFollowUpRecordSoOut addFollowUpRecordsV2(OrderAddFollowUpRecordSoInV2 soIn) {
        OrderFollowUpRecordSoOut soOut = new OrderFollowUpRecordSoOut();
        soIn.checkAddFollowUpRecordSoIn();
        // 获取待接单的客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("addFollowUpRecordsV2 客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }

        // 持久化附件的文件
        List<Long> fileIdList = soIn.getAttachmentList().stream().map(e -> e.getId()).collect(Collectors.toList());
        fileRemoteGateway.fileCommit(fileIdList);
        // 获取登陆人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getFollowUpMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setFollowUpName(employeeMap.containsKey(soIn.getFollowUpMid()) ? employeeMap.get(soIn.getFollowUpMid()).getName() : "");

        // 创建跟进记录对象及客诉单对象
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.addFollowUpRecordV2(soIn);
        // 持久�?
        Boolean insertOrder = true;
        Boolean insertRecords = true;
        if (Objects.nonNull(complaintOrderAggregation.getComplaintOrderInfoGoIn())) {
            insertOrder = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        }
        // 持久化跟进记�?
        insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (insertOrder && insertRecords) {
            soOut.setRecordResult(CommonConst.SUCCESS);
            return soOut;
        } else {
            log.error("addFollowUpRecordsV2 添加跟进信息异常V2, soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, ADD_FOLLOW_RECORD_ERROR_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderAddDistributionRecordSoOut addDistributionRecords(OrderAddDistributionRecordSoIn soIn) {
        OrderAddDistributionRecordSoOut soOut = new OrderAddDistributionRecordSoOut();
        soIn.checkDistributionRecord();
        // 获取客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.addDistributionRecord(soIn);

        // 持久化跟进记�?
        Boolean b = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (b) {
            soOut.setRecordResult(CommonConst.SUCCESS);
            return soOut;
        } else {
            log.error("addDistributionRecords 添加跟进信息异常, soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, ADD_FOLLOW_RECORD_ERROR_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderRemindSoOut remindOrder(OrderRemindSoIn soIn) {
        OrderRemindSoOut soOut = new OrderRemindSoOut();
        soIn.checkOrderRemind();
        // 加锁
        String lockKey = RedisUtil.generateRemindKey(soIn.getComplaintNo());
        if (!redisRemoteGateway.lock(lockKey, 3L, TimeUnit.SECONDS)) {
            log.info("当前lockKey正被锁，lockkey;{}, idempotentId:{}", lockKey, soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在催单中，请稍后再�?);
        }
        // 幂等
        try {
            OrderListGoIn listGoIn = new OrderListGoIn();
            listGoIn.setComplaintNo(soIn.getComplaintNo());
            List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
            if (CollUtil.isEmpty(orderList)) {
                log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
            }
            
            // 获取登陆人信�?
            EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getReminderMid()))).build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            soIn.setReminderName(employeeMap.containsKey(soIn.getReminderMid()) ? employeeMap.get(soIn.getReminderMid()).getName() : "");

            // 创建催单记录和客诉单对象
            ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
            ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
            complaintOrderAggregation.remindOrder(soIn);

            // 持久�?
            Boolean updateResult = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
            // 持久化派单记�?
            Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
            if (updateResult && insertRecords) {
                soOut.setRemindResult(CommonConst.SUCCESS);
                // 发送催单消�?
                asyncSendRemindMsg(complaintOrderInfoGoIn);
                return soOut;
            } else {
                log.error("更新失败，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新失败");
            }
        } finally {
            redisRemoteGateway.unLock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderUpdateCustomerServiceSoOut updateCustomerService(OrderUpdateCustomerServiceSoIn soIn) {
        OrderUpdateCustomerServiceSoOut soOut = new OrderUpdateCustomerServiceSoOut();
        soIn.checkServiceSoIn();
        // 准备数据
        List<String> stNoList = soIn.getStNo();
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setStNoList(stNoList);
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(goIn);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("客诉单没有查询到该工单的信息，stNoList:{}", GsonUtil.toJson(stNoList));
            soOut.setUpdateResult(Boolean.FALSE);
            return soOut;
        }

        // 更新投诉�?
        ComplaintOrderBatchAggregation complaintOrderBatchAggregation = ComplaintOrderAggregationFactory.getComplaintOrderBatchAggregation(orderList);
        complaintOrderBatchAggregation.updateCustomerServiceInfo(soIn);

        // 持久�?
        if (CollectionUtils.isNotEmpty(complaintOrderBatchAggregation.getComplaintOrderInfoGoInList())) {
            Boolean updateResult = complaintOrderRepositoryGateway.batchUpdateComplaintInfo(complaintOrderBatchAggregation.getComplaintOrderInfoGoInList());
            soOut.setResult(CommonConst.SUCCESS);
            soOut.setUpdateResult(updateResult);
            return soOut;
        }
        return soOut;
    }

    /**
     * 客诉升级投诉
     * @param soIn 升级参数
     * @return 更新结果
     */
    @Override
    public OrderUpdateHandlerSoOut upgradeComplaintOrder(ComplaintOrderUpgradeSoIn soIn) {
        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        log.info("开始处理投诉升级， soIn:{}", GsonUtil.toJson(soIn));
        soIn.checkUpgradeSoIn();
        // 获取待接单的客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("升级投诉客诉单不存在�?soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        
        // 填充接单人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(soIn.getOperatorMid())).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<Long, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, Function.identity()));
        soIn.setOperatorName(employeeMap.containsKey(soIn.getOperatorMid()) ? employeeMap.get(soIn.getOperatorMid()).getName() : "");
        // 查询到客诉单
        ComplaintOrderAggregation complaintOrderAggregation = ComplaintOrderAggregationFactory.getComplaintOrderAggregation(complaintOrderInfoGoIn);
        complaintOrderAggregation.upgradeComplaintOrder(soIn);

        // 持久�?
        Boolean updateResult = complaintOrderRepositoryGateway.updateComplaintInfo(complaintOrderAggregation.getComplaintOrderInfoGoIn());
        // 持久化跟进记�?
        Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(complaintOrderAggregation.getComplaintFollowProcessGoIn());
        if (updateResult && insertRecords) {
            complaintOrderInfoGoIn.setComplaintType(soIn.getTargetType());
            ComplaintOrderGoOut goOut = DomainConverter.INSTANCE.toGoOut(complaintOrderInfoGoIn);
            // 升级消息推�?
            MessageInformedStrategy upgradeStrategy = messageInformedEventFactory.getStrategy(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT);
            if (upgradeStrategy != null) {
                MessageInformedEvent messageInformedEvent = upgradeStrategy.createMessageInformedEvent(goOut,
                        new HashMap<>());
                eventPublisher.publishEvent(messageInformedEvent);
            }
            soOut.setResult(CommonConst.SUCCESS);

            // 如果是来自客服工作台的升级请求，判断升级后的客诉单是否为服务投诉且无免责时自动生成判责审批任�?
            if (SourceEnum.CUSTOMER_SERVICE_WORKBENCH.getCode().equals(soIn.getOperateSource())
                    && Objects.equals(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode(), soIn.getTargetType())) {
                Boolean deleteResult = complaintTagGateway.deleteTag(soIn.getComplaintNo(), TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode());
                log.info("来源于客服，升级到服务投诉，删除免责标签结果，complaintNo:{}, deleteResult:{}", soIn.getComplaintNo(), deleteResult);
                // 数据库中responsibility默认值为0会干扰判责审批单生成, 无论是否有责应该升级后由判责审批人决定是否有�?
                complaintOrderInfoGoIn.setResponsibility(null);

                // RPC：完善门店信�?
                List<String> orgIdList = new ArrayList<>();
                orgIdList.add(complaintOrderInfoGoIn.getOrgId());
                List<StoreInfoGoOut> storeListInfo = storeRemoteGateway.getStoreListInfo(orgIdList);
                complaintApplyService.persistComplaintAdjudicationApplyRecord(complaintOrderInfoGoIn, CollUtil.isEmpty(storeListInfo) ? null : storeListInfo.get(0).getOrgName());
            }
            return soOut;
        } else {
            log.error("升级投诉失败，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "升级投诉失败");
        }
    }

    @Override
    public OrderEditComplaintSoOut editComplaint(OrderEditComplaintSoIn soIn) {
        OrderEditComplaintSoOut soOut = new OrderEditComplaintSoOut();
        soIn.checkEditComplaint();
        log.info("开始处理编辑客诉单�?soIn:{}", GsonUtil.toJson(soIn));

        // 加锁
        String lockKey = "EDIT_COMPLAINT:" + soIn.getComplaintNo();
        if (!redisRemoteGateway.lock(lockKey, 10L, TimeUnit.SECONDS)) {
            log.info("当前lockKey正被锁，lockkey:{}", lockKey);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在编辑中，请稍后再�?);
        }

        try {
            // 校验客诉单是否存�?
            ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(soIn.getComplaintNo());
            if (complaintOrderGoOut == null) {
                log.error("编辑客诉单不存在�?soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
            }

            // 获取操作人信�?
            if (soIn.getOperateMid() != null) {
                EmployeeInfoGoOut employee = eiamRemoteGateway.getEmployee(soIn.getOperateMid());
                soIn.setOperateName(employee != null ? employee.getName() : "");
            }

            // �?ComplaintOrderGoOut 转换�?ComplaintOrderInfoGoIn，用于初始化聚合对象
            ComplaintOrderInfoGoIn originalOrderInfo = DomainConverter.INSTANCE.toGoIn(complaintOrderGoOut);
            ComplaintOrderAggregation complaintOrderAggregation =
                    ComplaintOrderAggregationFactory.getComplaintOrderAggregation(originalOrderInfo);
            complaintOrderAggregation.editComplaint(soIn);

            // 调用事务服务执行数据库更新操�?
            complaintEditTransactionService.doEditComplaintInTransaction(complaintOrderAggregation);

            if (Objects.equals(complaintOrderGoOut.getMediaInvolved(), 0) && Objects.equals(soIn.getMediaInvolved(), "1")) {
                MessageInformedStrategy mediaInvolvedStrategy = messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);
                MessageInformedEvent mediaInvolvedEvent = mediaInvolvedStrategy.createMessageInformedEvent(complaintOrderGoOut,
                        new HashMap<>());
                if (mediaInvolvedEvent != null) {
                    eventPublisher.publishEvent(mediaInvolvedEvent);
                }
            }

            soOut.setResult(CommonConst.SUCCESS);
            return soOut;
        } finally {
            redisRemoteGateway.unLock(lockKey);
        }
    }

    @Override
    public SubmitReviewSoOut submitReview(SubmitReviewSoIn soIn) {
        soIn.checkSubmitReviewSoIn();
        log.info("submitReview soIn:{}", GsonUtil.toJson(soIn));

        String lockKey = "SUBMIT_REVIEW:" + soIn.getComplaintNo();
        if (!redisRemoteGateway.lock(lockKey, 10L, TimeUnit.SECONDS)) {
            log.warn("submitReview 正在提交复盘中，complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在提交复盘中，请稍后再�?);
        }
        try {
            OrderListGoIn listGoIn = new OrderListGoIn();
            listGoIn.setComplaintNo(soIn.getComplaintNo());
            List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
            if (CollUtil.isEmpty(orderList)) {
                log.error("submitReview 客诉单不存在，complaintNo:{}", soIn.getComplaintNo());
                throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
            }
            ComplaintOrderInfoGoIn order = orderList.get(0);

            // 校验创建来源=线上客服
            if (!Objects.equals(CreateSourceEnum.ONLINE_CS.getCode(), order.getCreateSource())) {
                log.warn("submitReview 仅支持创建来源为线上客服的客诉单，complaintNo:{}, createSource:{}",
                        soIn.getComplaintNo(), order.getCreateSource());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "仅支持线上客服创建的客诉单提交复�?);
            }
            // 校验投诉分类=服务投诉
            if (!Objects.equals(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode(), order.getComplaintType())) {
                log.warn("submitReview 仅支持服务投诉提交复盘，complaintNo:{}, complaintType:{}",
                        soIn.getComplaintNo(), order.getComplaintType());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "仅支持服务投诉提交复�?);
            }
            // 校验未提交过复盘
            if (ReviewedEnum.YES.getCode().equals(order.getReviewed())) {
                log.warn("submitReview 已提交过复盘，complaintNo:{}", soIn.getComplaintNo());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "该客诉单已提交过复盘");
            }
            // 校验状态≠申请改派门店待审�?
            if (Objects.equals(ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(), order.getStatus())) {
                log.warn("submitReview 申请改派门店待审核状态下不可提交复盘，complaintNo:{}", soIn.getComplaintNo());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前状态不可提交复�?);
            }

            // 查询操作人姓�?
            if (soIn.getOperatorMid() != null) {
                List<EmployeeInfoGoOut> employees = eiamRemoteGateway.getEmployeeList(
                        EmployeeListGoIn.builder().miIdList(Collections.singletonList(soIn.getOperatorMid())).build());
                if (CollUtil.isNotEmpty(employees)) {
                    soIn.setOperatorName(employees.get(0).getName());
                }
            }

            // 新增「提交复盘」跟进记�?
            RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                    .operateTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                    .operateMid(soIn.getOperatorMid() != null ? soIn.getOperatorMid().toString() : null)
                    .operateName(soIn.getOperatorName())
                    .reviewMaterialUrl(soIn.getReviewMaterial())
                    .build();
            ComplaintFollowProcessGoIn followProcessGoIn = ComplaintFollowProcessGoIn.builder()
                    .complaintNo(soIn.getComplaintNo())
                    .processType(ProcessTypeEnum.SUBMIT_REVIEW.getProcessCode())
                    .processContent(GsonUtil.toJson(recordInfoGoIn))
                    .build();
            ComplaintOrderInfoGoIn updateGoIn = ComplaintOrderInfoGoIn.builder()
                    .complaintNo(soIn.getComplaintNo())
                    .reviewed(ReviewedEnum.YES.getCode())
                    .build();
            complaintSubmitReviewTransactionService.doSubmitReviewInTransaction(followProcessGoIn, updateGoIn);

            // 异步发送投诉复盘消�?
            ComplaintOrderGoOut complaintOrderGoOut = new ComplaintOrderGoOut();
            BeanUtil.copyProperties(order, complaintOrderGoOut);
            CompletableFuture.runAsync(() -> {
                MessageInformedStrategy messageStrategy =
                        messageInformedEventFactory.getStrategy(PushConstant.SUBMIT_REVIEW_CLOSURE);
                MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                        new HashMap<>());
                eventPublisher.publishEvent(messageInformedEvent);
            }, constructMessageEventExecutor).exceptionally((Throwable e) -> {
                // 发消息失败不要阻塞主流程
                log.error("sendCreateMsg error, 发送投诉复盘失�? ComplaintOrderGoOut:{}", RetailJsonUtil.toJson(complaintOrderGoOut), e);
                return null;
            });
            return SubmitReviewSoOut.builder().success(true).build();
        } finally {
            redisRemoteGateway.unLock(lockKey);
        }
    }

    private void sendCreateMsg(ComplaintOrderInfoGoIn goIn) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("获取消息处理策略");
        ComplaintOrderGoOut goOut = DomainConverter.INSTANCE.toGoOut(goIn);
        MessageInformedStrategy messageStrategy;
        // 判断订单是否仅查�?
        if (OnlyViewEnum.YES.getCode().equals(goOut.getOnlyView())) {
            messageStrategy =
                    messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_VIEW);
        } else {
            messageStrategy =
                    messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);

        }
        stopWatch.stop();
        stopWatch.start("创建消息发送事件并发布消息");
        CompletableFuture.runAsync(() -> {
            MessageInformedEvent complaintCreateEvent = messageStrategy.createMessageInformedEvent(goOut,
                    new HashMap<>());
            MessageInformedStrategy mediaInvolvedStrategy = messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);

            if (complaintCreateEvent != null) {
                eventPublisher.publishEvent(complaintCreateEvent);
            }
            if (Objects.equals(goIn.getMediaInvolved(), 1)) {
                MessageInformedEvent mediaInvolvedEvent = mediaInvolvedStrategy.createMessageInformedEvent(goOut,
                        new HashMap<>());
                eventPublisher.publishEvent(mediaInvolvedEvent);
            }
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞创建客诉单主流�?
            log.error("sendCreateMsg error, 创建订单发送消息失�? ComplaintOrderGoOut:{}", RetailJsonUtil.toJson(goOut), e);
            return null;
        });
        stopWatch.stop();
        log.info("ComplaintOperateServiceImpl#sendCreateMsg, time result:{}", stopWatch.prettyPrint());
    }

    private void asyncSendRemindMsg(ComplaintOrderInfoGoIn goIn) {
        ComplaintOrderGoOut goOut = DomainConverter.INSTANCE.toGoOut(goIn);
        MessageInformedStrategy messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.REMIND);
        CompletableFuture.runAsync(() -> {
            eventPublisher.publishEvent(messageStrategy.createMessageInformedEvent(goOut, new HashMap<>()));
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞创建客诉单主流�?
            log.error("asyncSendRemindMsg error, 催单消息发送失�? ComplaintOrderGoOut:{}", RetailJsonUtil.toJson(goOut), e);
            return null;
        });
    }
}
