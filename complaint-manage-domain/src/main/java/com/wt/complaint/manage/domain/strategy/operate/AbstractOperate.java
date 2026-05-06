package com.wt.complaint.manage.domain.strategy.operate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NoGeneratorRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcExpandOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderExpandGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderExpandGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.constant.ReportActionConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author linjiehong
 * @date 2025/5/21 20:46
 */
@Slf4j
public abstract class AbstractOperate implements UserComplaintOperateStrategy {
    @Resource
    private RedisRemoteGateway redisRemoteGateway;

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
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private ReportAuthManager reportAuthManager;

    @Resource
    private ComplaintMessageInformedEventFactory complaintMessageInformedEventFactory;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrderWithLock(CreateOrderSoIn soIn) {
        return executeWithLock(soIn.getIdempotentId(), () -> createOrder(soIn), "创建客诉类单据失�?);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String remindOrderWithLock(OrderRemindSoIn soIn) {
        return executeWithLock(soIn.getUcNo(), () -> remindOrder(soIn), "催单失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String PickUpOrder(OrderPickUpSoIn soIn) {
        // 获取单据信息
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut = getUcOrderList(soIn.getUcNo());

        // 权限控制
        UserComplaintOrderDetailSoOut detailMes =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        if (!reportAuthManager.hasDetailActionAuth(Long.valueOf(soIn.getPickUpMid()),
                ReportActionConst.PICK_UP, detailMes)) {
            log.error("接单�?{} 无权限接单，单据号：{}", soIn.getPickUpMid(), soIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "接单人无权限接单");
        }

        // 获取接单人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getPickUpMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setPickUpName(employeeMap.containsKey(soIn.getPickUpMid()) ? employeeMap.get(soIn.getPickUpMid()).getName() : "");

        // 执行接单事件
        UcOrderContext context = UcOrderContext.builder()
                .ucNo(soIn.getUcNo())
                .operateMid(soIn.getPickUpMid())
                .operateName(employeeMap.containsKey(soIn.getPickUpMid()) ? employeeMap.get(soIn.getPickUpMid()).getName() : "")
                .build();
        executeAction(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().get(0).getOrderStatus(),
                UcOrderEventEnum.PICKUP_ORDER, context);

        // 更新处理人mid
        UcOrderUpdateGoIn ucOrderUpdateGoIn = UcOrderUpdateGoIn.builder()
                .ucNo(soIn.getUcNo())
                .operatorMid(Long.valueOf(soIn.getPickUpMid())).build();
        userComplaintOrderGateway.updateOrderSelective(ucOrderUpdateGoIn);

        return soIn.getUcNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn) {
        // 获取单据信息
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut = getUcOrderList(soIn.getUcNo());

        // 权限控制
        UserComplaintOrderDetailSoOut detailMes =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        if (!reportAuthManager.hasDetailActionAuth(Long.valueOf(soIn.getFollowUpMid()),
                ReportActionConst.ADD_FOLLOW_UP_RECORDS, detailMes)) {
            log.error("用户:{} 无权限填写跟进记录，单据号：{}", soIn.getFollowUpMid(), soIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "无权限填写跟进记�?);
        }

        // 持久化附件的文件
        if (CollUtil.isNotEmpty(soIn.getAttachmentList())) {
            List<Long> fileIdList = soIn.getAttachmentList().stream().map(e -> e.getId()).collect(Collectors.toList());
            fileRemoteGateway.fileCommit(fileIdList);
        }

        // 获取登陆人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getFollowUpMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        // 用stream将员工列表转换为map
        Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
        soIn.setFollowUpName(employeeMap.containsKey(soIn.getFollowUpMid()) ? employeeMap.get(soIn.getFollowUpMid()).getName() : "");

        // 执行添加跟进记录事件
        UcOrderContext context = UcOrderContext.builder()
                .ucNo(soIn.getUcNo())
                .operateMid(soIn.getFollowUpMid())
                .operateName(employeeMap.containsKey(soIn.getFollowUpMid()) ? employeeMap.get(soIn.getFollowUpMid()).getName() : "")
                .operateContent(soIn.getFollowInfo())
                .attachmentList(soIn.getAttachmentList())
                .build();
        executeAction(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().get(0).getOrderStatus(),
                UcOrderEventEnum.ADD_FOLLOW_RECORD, context);

        return soIn.getUcNo();
    }

    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderSoIn soIn) {
        return "";
    }

    @Transactional(rollbackFor = Exception.class)
    public String remindOrder(OrderRemindSoIn soIn) {
        // 获取单据信息
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut = getUcOrderList(soIn.getUcNo());

        // 获取登陆人信�?
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder()
                .miIdList(Collections.singletonList(Long.valueOf(soIn.getReminderMid()))).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
        Map<String, EmployeeInfoGoOut>
                employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));

        // 执行催单事件
        UcOrderContext context = UcOrderContext.builder()
                .ucNo(soIn.getUcNo())
                .operateMid(soIn.getReminderMid())
                .operateName(employeeMap.containsKey(soIn.getReminderMid()) ? employeeMap.get(soIn.getReminderMid()).getName() : "")
                .operateContent(soIn.getOrderRemindInfo())
                .build();
        executeAction(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().get(0).getOrderStatus(),
                UcOrderEventEnum.REMIND_ORDER, context);

        // 催单次数持久�?
        UcOrderExpandGoIn ucOrderExpandGoIn = UcOrderExpandGoIn.builder().ucNo(soIn.getUcNo()).build();
        UserComplaintOrderExpandGoOut userComplaintOrderExpandGoOut =
                userComplaintOrderGateway.searchUserComplaintExpandData(ucOrderExpandGoIn);
        if (CollUtil.isEmpty(userComplaintOrderExpandGoOut.getUserComplaintExpandInfoList())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "举报单扩展信息不存在");
        }
        UcExpandOrderGoIn ucExpandOrderGoIn = UcExpandOrderGoIn.builder()
                .ucNo(soIn.getUcNo())
                .reminderTimes(userComplaintOrderExpandGoOut.getUserComplaintExpandInfoList().get(0).getReminderTimes() + 1)
                .build();
        userComplaintOrderGateway.updateExpandSelective(ucExpandOrderGoIn);

        // 发送催单消�?
        UserComplaintOrderDetailSoOut soOut =
                userComplaintOrderGateway.selectDetailByUcNo(soIn.getUcNo());
        asyncSendRemindMsg(soOut);

        return soIn.getUcNo();
    }

    @Override
    public String judgeOrder(JudgeOrderSoIn soIn) {
        return "";
    }

    /**
     * 执行动作
     * @param statusType 当前状�?
     * @param event 事件
     * @param context 上下�?
     */
    abstract void executeAction(int statusType, UcOrderEventEnum event, UcOrderContext context);

    /**
     * 通用的加锁执行方�?
     * @param lockId 锁ID
     * @param operation 要执行的操作
     * @param errorMessage 错误信息
     * @return 操作结果
     */
    protected <T> T executeWithLock(String lockId, Supplier<T> operation, String errorMessage) {
        StopWatch stopWatch = new StopWatch("创建客诉类单�?);
        stopWatch.start("创建加锁");
        String lockKey = RedisUtil.generateCreateLockKey(lockId);
        if (!redisRemoteGateway.lock(lockKey, 10L, TimeUnit.SECONDS)) {
            log.info("竞争锁失败，lockkey;{}, idempotentId:{}", lockKey, lockId);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在创建中，请稍后再�?);
        }
        stopWatch.stop();

        try {
            return operation.get();
        } catch (Exception e) {
            log.error("{}失败，lockId:{}", errorMessage, lockId, e);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, errorMessage);
        } finally {
            redisRemoteGateway.unLock(lockKey);
        }
    }

    /**
     * 获取单据信息
     * @param ucNo 客诉单号
     * @return 单据信息
     */
    protected UserComplaintOrderMainGoOut getUcOrderList(String ucNo) {
        UcOrderInfoGoIn ucOrderInfoGoIn = new UcOrderInfoGoIn();
        ucOrderInfoGoIn.setUcNoList(CollUtil.toList(ucNo));
        ucOrderInfoGoIn.setMaster(true);
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut =
                userComplaintOrderGateway.searchUserComplaintMainData(ucOrderInfoGoIn);
        if (CollUtil.isEmpty(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "单据不存�?);
        }
        return userComplaintOrderMainGoOut;
    }

    private void asyncSendRemindMsg(UserComplaintOrderDetailSoOut soOut) {
        ComplaintMessageInformedStrategy messageStrategy = complaintMessageInformedEventFactory.getStrategy(PushConstant.REPORT_REMIND);
        CompletableFuture.runAsync(() -> {
            eventPublisher.publishEvent(messageStrategy.createMessageInformedEvent(soOut, new HashMap<>()));
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞催单主流程
            log.error("asyncSendRemindMsg error, 催单消息发送失�? ComplaintOrderGoOut:{}", RetailJsonUtil.toJson(soOut), e);
            return null;
        });
    }



}

