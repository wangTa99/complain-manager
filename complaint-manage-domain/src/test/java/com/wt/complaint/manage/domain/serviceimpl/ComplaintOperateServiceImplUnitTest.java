package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintApplyService;
import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderUpgradeSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoInV2;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderEditComplaintSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.SubmitReviewSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderEditComplaintSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderFollowUpRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderUpdateHandlerSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.SubmitReviewSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ComplaintOperateServiceImpl单元测试
 * 测试客诉操作服务核心业务逻辑
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintOperateServiceImplUnitTest {

    @InjectMocks
    private ComplaintOperateServiceImpl complaintOperateService;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Mock
    private ComplaintGateway complaintGateway;

    @Mock
    private ComplaintTagGateway complaintTagGateway;

    @Mock
    private RedisRemoteGateway redisRemoteGateway;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private FileRemoteGateway fileRemoteGateway;

    @Mock
    private RmqGateway rmqGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    private ComplaintApplyService complaintApplyService;

    @Mock
    private ComplaintEditTransactionService complaintEditTransactionService;

    @Mock
    private ComplaintSubmitReviewTransactionService complaintSubmitReviewTransactionService;

    @Mock
    private MessageInformedEventFactory messageInformedEventFactory;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @BeforeEach
    void setUp() {
        // 初始化操�?
    }

    // ============ upgradeComplaintOrder 升级投诉测试 ============

    /**
     * 测试升级投诉 - 升级到产品投诉成�?
     */
    @Test
    void testUpgradeComplaintOrder_ToProductComplaint_Success() {
        // 准备数据
        String complaintNo = "C001";
        ComplaintOrderUpgradeSoIn soIn = TestDataBuilder.buildComplaintOrderUpgradeSoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        
        // Mock 客诉单查�?- 产品风险类型
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperatorMid(), "测试人员");
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 数据库更�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // Mock 升级后门�?区域消息推送（v-zhengshuiguang 客诉二期消息推送）
        MessageInformedStrategy storeStrategy = mock(MessageInformedStrategy.class);
        MessageInformedStrategy zoneStrategy = mock(MessageInformedStrategy.class);
        when(messageInformedEventFactory.getStrategy(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT)).thenReturn(storeStrategy);

        // 执行
        OrderUpdateHandlerSoOut result = complaintOperateService.upgradeComplaintOrder(soIn);

        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getResult());

        // 验证数据库更新被调用
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
        // 验证升级成功后发布事件（strategy �?stub createMessageInformedEvent 时可能为 null�?
        verify(eventPublisher).publishEvent(nullable(Object.class));
    }

    /**
     * 测试升级投诉 - 升级到服务投诉删除免考核标签
     */
    @Test
    void testUpgradeComplaintOrder_ToServiceComplaint_DeleteFreeTag() {
        // 准备数据
        String complaintNo = "C002";
        ComplaintOrderUpgradeSoIn soIn = TestDataBuilder.buildComplaintOrderUpgradeSoIn(
                complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        // 必须设置来源为客服工作台，deleteTag �?persistComplaintAdjudicationApplyRecord 分支才会执行
        soIn.setOperateSource(SourceEnum.CUSTOMER_SERVICE_WORKBENCH.getCode());

        // Mock 客诉单查�?
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperatorMid(), "测试人员");
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 标签删除
        when(complaintTagGateway.deleteTag(eq(complaintNo), eq(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode())))
                .thenReturn(true);
        
        // Mock 数据库更�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        // Mock 升级后门�?区域消息推�?
        when(messageInformedEventFactory.getStrategy(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT)).thenReturn(mock(MessageInformedStrategy.class));

        // 执行
        OrderUpdateHandlerSoOut result = complaintOperateService.upgradeComplaintOrder(soIn);

        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getResult());

        // 验证删除免考核标签被调�?
        verify(complaintTagGateway).deleteTag(eq(complaintNo), eq(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode()));
    }

    /**
     * 测试升级投诉 - 客诉单不存在抛异�?
     */
    @Test
    void testUpgradeComplaintOrder_ComplaintNotExists_ThrowException() {
        // 准备数据
        String complaintNo = "C999";
        ComplaintOrderUpgradeSoIn soIn = TestDataBuilder.buildComplaintOrderUpgradeSoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        
        // Mock 客诉单查�?- 返回�?
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());
        
        // 执行并验�?- 应抛出异�?
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintOperateService.upgradeComplaintOrder(soIn);
        });
        
        assertTrue(exception.getMessage().contains("不存�?));
    }

    /**
     * 测试升级投诉 - 保存跟进记录成功
     */
    @Test
    void testUpgradeComplaintOrder_SaveRecord_Success() {
        // 准备数据
        String complaintNo = "C003";
        ComplaintOrderUpgradeSoIn soIn = TestDataBuilder.buildComplaintOrderUpgradeSoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        
        // Mock 客诉单查�?
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperatorMid(), "测试人员");
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 数据库更�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        when(messageInformedEventFactory.getStrategy(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT)).thenReturn(mock(MessageInformedStrategy.class));

        // 执行
        complaintOperateService.upgradeComplaintOrder(soIn);

        // 验证跟进记录保存被调�?
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    // ============ editComplaint 编辑客诉单测�?============

    /**
     * 测试编辑客诉�?- 所有字段都变更成功
     */
    @Test
    void testEditComplaint_AllFieldsChanged_Success() {
        // 准备数据
        String complaintNo = "C005";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        
        // 设置所有字段变�?
        FieldValueSoIn complaint = TestDataBuilder.buildFieldValueSoIn("SC002", "售后体验", "2/3/4", "售后/售后体验/售后体验");
        soIn.setComplaint(complaint);
        soIn.setRiskLevel("2"); // L2
        soIn.setMediaInvolved("1"); // �?
        soIn.setMediaLink("http://test.com");
        
        // Mock 客诉单查�?
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderGoOut.setSoNo("SO001");
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperateMid(), "测试人员");
        when(eiamRemoteGateway.getEmployee(soIn.getOperateMid())).thenReturn(employee);
        
        // Mock 加锁
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 事务服务
        doNothing().when(complaintEditTransactionService).doEditComplaintInTransaction(any());
        
        // Mock 涉媒变更时消息策略（从非涉媒改为涉媒会触发推送）
        MessageInformedStrategy mediaInvolvedStrategy = mock(MessageInformedStrategy.class);
        when(messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT)).thenReturn(mediaInvolvedStrategy);
        when(mediaInvolvedStrategy.createMessageInformedEvent(any(), any())).thenReturn(null);
        
        // 执行
        OrderEditComplaintSoOut result = complaintOperateService.editComplaint(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals(CommonConst.SUCCESS, result.getResult());
        
        // 验证加锁和解�?
        verify(redisRemoteGateway).lock(anyString(), anyLong(), any(TimeUnit.class));
        verify(redisRemoteGateway).unLock(anyString());
        
        // 验证事务服务被调�?
        verify(complaintEditTransactionService).doEditComplaintInTransaction(any());
    }

    /**
     * 测试编辑客诉�?- 只更新风险等�?
     */
    @Test
    void testEditComplaint_RiskLevelOnly_Success() {
        // 准备数据
        String complaintNo = "C006";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        // 设置complaint字段避免NPE（保持原值不变）
        FieldValueSoIn sameComplaint = TestDataBuilder.buildFieldValueSoIn(
                "SC001", "交车体验", "1/2/3", "交付/交车体验/交车体验");
        soIn.setComplaint(sameComplaint);
        soIn.setRiskLevel("3"); // L3
        
        // Mock 客诉单查�?
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderGoOut.setSoNo("SO001");
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperateMid(), "测试人员");
        when(eiamRemoteGateway.getEmployee(soIn.getOperateMid())).thenReturn(employee);
        
        // Mock 加锁
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 事务服务
        doNothing().when(complaintEditTransactionService).doEditComplaintInTransaction(any());
        
        // 执行
        OrderEditComplaintSoOut result = complaintOperateService.editComplaint(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals(CommonConst.SUCCESS, result.getResult());
        verify(complaintEditTransactionService).doEditComplaintInTransaction(any());
    }

    /**
     * 测试编辑客诉�?- 只更新涉媒信�?
     */
    @Test
    void testEditComplaint_MediaInvolvedOnly_Success() {
        // 准备数据
        String complaintNo = "C007";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        // 设置complaint字段避免NPE（保持原值不变）
        FieldValueSoIn sameComplaint = TestDataBuilder.buildFieldValueSoIn(
                "SC001", "交车体验", "1/2/3", "交付/交车体验/交车体验");
        soIn.setComplaint(sameComplaint);
        soIn.setMediaInvolved("1"); // 涉媒
        soIn.setMediaLink("http://media.test.com");
        
        // Mock 客诉单查�?
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderGoOut.setSoNo("SO001");
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperateMid(), "测试人员");
        when(eiamRemoteGateway.getEmployee(soIn.getOperateMid())).thenReturn(employee);
        
        // Mock 加锁
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 事务服务
        doNothing().when(complaintEditTransactionService).doEditComplaintInTransaction(any());
        
        // Mock 涉媒变更时消息策略（从非涉媒改为涉媒会触发推送）
        MessageInformedStrategy mediaInvolvedStrategy = mock(MessageInformedStrategy.class);
        when(messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT)).thenReturn(mediaInvolvedStrategy);
        when(mediaInvolvedStrategy.createMessageInformedEvent(any(), any())).thenReturn(null);
        
        // 执行
        OrderEditComplaintSoOut result = complaintOperateService.editComplaint(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals(CommonConst.SUCCESS, result.getResult());
    }

    /**
     * 测试编辑客诉�?- 无变更跳过更�?
     * 注意：这个测试实际上会调用到 ComplaintOrderAggregation.editComplaint
     * 在那里会判断无变更并设置为null，这里主要验证流�?
     */
    @Test
    void testEditComplaint_NoChange_SkipUpdate() {
        // 准备数据
        String complaintNo = "C008";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        // 不设置任何变更字�?
        
        // Mock 客诉单查�?
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderGoOut.setSoNo("SO001");
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperateMid(), "测试人员");
        when(eiamRemoteGateway.getEmployee(soIn.getOperateMid())).thenReturn(employee);
        
        // Mock 加锁
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 事务服务
        doNothing().when(complaintEditTransactionService).doEditComplaintInTransaction(any());
        
        // 执行
        OrderEditComplaintSoOut result = complaintOperateService.editComplaint(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getResult());
    }

    /**
     * 测试编辑客诉�?- 客诉单不存在抛异�?
     */
    @Test
    void testEditComplaint_ComplaintNotExists_ThrowException() {
        // 准备数据
        String complaintNo = "C999";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        soIn.setRiskLevel("2");
        
        // Mock 加锁
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 客诉单查�?- 返回null
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(null);
        
        // 执行并验�?- 应抛出异�?
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintOperateService.editComplaint(soIn);
        });
        
        assertTrue(exception.getMessage().contains("不存�?));
        
        // 验证解锁被调�?
        verify(redisRemoteGateway).unLock(anyString());
    }

    /**
     * 测试编辑客诉�?- 加锁编辑成功
     */
    @Test
    void testEditComplaint_WithLock_Success() {
        // 准备数据
        String complaintNo = "C009";
        OrderEditComplaintSoIn soIn = TestDataBuilder.buildOrderEditComplaintSoIn(complaintNo);
        // 设置complaint字段避免NPE（保持原值不变）
        FieldValueSoIn sameComplaint = TestDataBuilder.buildFieldValueSoIn(
                "SC001", "交车体验", "1/2/3", "交付/交车体验/交车体验");
        soIn.setComplaint(sameComplaint);
        soIn.setRiskLevel("2");
        
        // Mock 客诉单查�?
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderGoOut.setSoNo("SO001");
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(soIn.getOperateMid(), "测试人员");
        when(eiamRemoteGateway.getEmployee(soIn.getOperateMid())).thenReturn(employee);
        
        // Mock 加锁成功
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        
        // Mock 事务服务
        doNothing().when(complaintEditTransactionService).doEditComplaintInTransaction(any());
        
        // 执行
        OrderEditComplaintSoOut result = complaintOperateService.editComplaint(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getResult());
        
        // 验证加锁和解锁都被调�?
        verify(redisRemoteGateway).lock(anyString(), anyLong(), any(TimeUnit.class));
        verify(redisRemoteGateway).unLock(anyString());
    }

    // ============ addFollowUpRecordsV2 跟进记录V2测试 ============

    /**
     * 测试跟进记录V2 - 首响更新状�?
     */
    @Test
    void testAddFollowUpRecordsV2_FirstResponse_UpdateStatus() {
        // 准备数据
        String complaintNo = "C010";
        OrderAddFollowUpRecordSoInV2 soIn = TestDataBuilder.buildOrderAddFollowUpRecordSoInV2(complaintNo);
        
        // Mock 客诉单查�?- 待首响状�?
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?);
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 文件提交
        doNothing().when(fileRemoteGateway).fileCommit(anyList());
        
        // Mock 数据库更�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行
        OrderFollowUpRecordSoOut result = complaintOperateService.addFollowUpRecordsV2(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getRecordResult());
        
        // 验证状态更新被调用
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    /**
     * 测试跟进记录V2 - 非首响不更新状�?
     */
    @Test
    void testAddFollowUpRecordsV2_NotFirstResponse_NoStatusChange() {
        // 准备数据
        String complaintNo = "C011";
        OrderAddFollowUpRecordSoInV2 soIn = TestDataBuilder.buildOrderAddFollowUpRecordSoInV2(complaintNo);
        
        // Mock 客诉单查�?- 待申请结案状态（非首响）
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?);
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 文件提交
        doNothing().when(fileRemoteGateway).fileCommit(anyList());
        
        // Mock 数据库更�?- 只保存跟进记录，不更新客诉单状�?
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行
        OrderFollowUpRecordSoOut result = complaintOperateService.addFollowUpRecordsV2(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getRecordResult());
        
        // 验证只保存跟进记�?
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    /**
     * 测试跟进记录V2 - 带里程数据成�?
     */
    @Test
    void testAddFollowUpRecordsV2_WithMileage_Success() {
        // 准备数据
        String complaintNo = "C012";
        OrderAddFollowUpRecordSoInV2 soIn = TestDataBuilder.buildOrderAddFollowUpRecordSoInV2(complaintNo);
        soIn.setMileage("5000.75"); // 设置里程数据
        
        // Mock 客诉单查�?
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        
        // Mock 员工信息
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?);
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(employee));
        
        // Mock 文件提交
        doNothing().when(fileRemoteGateway).fileCommit(anyList());
        
        // Mock 数据库更�?
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行
        OrderFollowUpRecordSoOut result = complaintOperateService.addFollowUpRecordsV2(soIn);
        
        // 验证
        assertNotNull(result);
        assertEquals("SUCCESS", result.getRecordResult());
        
        // 验证跟进记录保存（应包含里程数据�?
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    /**
     * 测试跟进记录V2 - 客诉单不存在抛异�?
     */
    @Test
    void testAddFollowUpRecordsV2_ComplaintNotExists_ThrowException() {
        // 准备数据
        String complaintNo = "C999";
        OrderAddFollowUpRecordSoInV2 soIn = TestDataBuilder.buildOrderAddFollowUpRecordSoInV2(complaintNo);
        
        // Mock 客诉单查�?- 返回�?
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());
        
        // 执行并验�?- 应抛出异�?
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintOperateService.addFollowUpRecordsV2(soIn);
        });
        
        assertTrue(exception.getMessage().contains("不存�?));
    }

    // ============ submitReview 提交复盘测试 ============

    private static SubmitReviewSoIn buildSubmitReviewSoIn(String complaintNo) {
        SubmitReviewSoIn soIn = new SubmitReviewSoIn();
        soIn.setComplaintNo(complaintNo);
        soIn.setReviewMaterial("https://xxx.feishu.cn/docx/xxx");
        soIn.setOperatorMid(1001L);
        return soIn;
    }

    @Test
    void testSubmitReview_Success() {
        String complaintNo = "C020";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        orderInfo.setReviewed(0);
        orderInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(TestDataBuilder.buildEmployeeInfoGoOut(1001L, "操作�?)));

        SubmitReviewSoOut result = complaintOperateService.submitReview(soIn);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        ArgumentCaptor<ComplaintFollowProcessGoIn> followCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintSubmitReviewTransactionService).doSubmitReviewInTransaction(followCaptor.capture(), any());
        assertEquals(soIn.getComplaintNo(), followCaptor.getValue().getComplaintNo());
        verify(complaintFollowProcessRepositoryGateway, never()).saveComplaintFollowProcess(any());
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        verify(redisRemoteGateway).unLock(anyString());
    }

    @Test
    void testSubmitReview_TxFailed_Throws() {
        String complaintNo = "C027";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        orderInfo.setReviewed(0);
        orderInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any())).thenReturn(Lists.newArrayList(TestDataBuilder.buildEmployeeInfoGoOut(1001L, "操作�?)));
        doThrow(new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "提交复盘失败"))
                .when(complaintSubmitReviewTransactionService).doSubmitReviewInTransaction(any(), any());

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("提交复盘失败"));
        verify(redisRemoteGateway).unLock(anyString());
    }

    @Test
    void testSubmitReview_LockFail_Throws() {
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn("C021");
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("正在提交复盘�?));
        verify(complaintOrderRepositoryGateway, never()).findList(any());
    }

    @Test
    void testSubmitReview_ComplaintNotFound_Throws() {
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn("C022");
        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("客诉单不存在"));
    }

    @Test
    void testSubmitReview_NotOnlineCs_Throws() {
        String complaintNo = "C023";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.STORE.getCode());
        orderInfo.setReviewed(0);

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("仅支持线上客�?));
    }

    @Test
    void testSubmitReview_NotServiceComplaint_Throws() {
        String complaintNo = "C024";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        orderInfo.setReviewed(0);

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("仅支持服务投�?));
    }

    @Test
    void testSubmitReview_AlreadyReviewed_Throws() {
        String complaintNo = "C025";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        orderInfo.setReviewed(1);

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("已提交过复盘"));
    }

    @Test
    void testSubmitReview_OrgReassignPending_Throws() {
        String complaintNo = "C026";
        SubmitReviewSoIn soIn = buildSubmitReviewSoIn(complaintNo);
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        orderInfo.setReviewed(0);
        orderInfo.setStatus(ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode());

        when(redisRemoteGateway.lock(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redisRemoteGateway.unLock(anyString())).thenReturn(true);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintOperateService.submitReview(soIn));
        assertTrue(ex.getMessage().contains("当前状态不可提交复�?));
    }
}
