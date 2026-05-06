package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;
import com.wt.complaint.manage.domain.constant.BPMConst;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static com.wt.complaint.manage.domain.constant.PushConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ComplaintApplyServiceImpl 单元测试（仅 submitApply�?
 * 不启�?Spring，Mock 所�?Gateway 与事务服�?
 */
@ExtendWith(MockitoExtension.class)
class ComplaintApplyServiceImplUnitTest {

    private static final String STORE_NAME = "测试门店";

    @InjectMocks
    private ComplaintApplyServiceImpl complaintApplyService;

    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditRepositoryGateway complaintAuditRepositoryGateway;
    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway complaintAuditGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private FileRemoteGateway fileRemoteGateway;
    @Mock
    private BPMRemoteGateway bpmRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory messageInformedEventFactory;
    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;
    @Mock
    private ComplaintApplyTransactionService complaintApplyTransactionService;
    @Mock
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @Test
    void submitApply_complaintNoBlank_throwsBusinessException() {
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo("")
                .applyOrgId("F001")
                .createMid(1001L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintApplyService.submitApply(soIn));
        assertTrue(ex.getMessage().contains("客诉单号"));
        verify(complaintOrderRepositoryGateway, never()).findList(any());
    }

    @Test
    void submitApply_orderListEmpty_throwsBusinessException() {
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo("C001")
                .applyOrgId("F001")
                .createMid(1001L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class))).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintApplyService.submitApply(soIn));
        assertTrue(ex.getMessage().contains("不存�?));
        verify(complaintApplyTransactionService, never()).doSubmitApplyInTransaction(any(), any());
    }

    @Test
    void submitApply_success_returnsSoOutAndCallsTransaction() {
        String complaintNo = "C001";
        String orgId = "F001";
        Long createMid = 1001L;
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName(STORE_NAME).zoneId(1).littleZoneId(10).cityId("100").build()));
        when(bpmRemoteGateway.processCreate(any())).thenReturn("PI-EXEMPT-TEST-001");
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);

        assertNotNull(soOut);
        assertEquals(0L, soOut.getId());
        ArgumentCaptor<ComplaintApplySoIn> soInCaptor = ArgumentCaptor.forClass(ComplaintApplySoIn.class);
        verify(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), soInCaptor.capture());
        assertEquals(complaintNo, soInCaptor.getValue().getComplaintNo());
        assertEquals(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode(), soInCaptor.getValue().getAuditType());
    }

    @Test
    void submitApply_finishApplyV2_productRisk_setsProductRiskClosureAuditType() {
        String complaintNo = "C002";
        String orgId = "F001";
        Long createMid = 1001L;
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .finishApplyV2(true)
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(complaintFollowProcessRepositoryGateway.getProcessListByNo(complaintNo)).thenReturn(Collections.emptyList());
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName(STORE_NAME).zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        complaintApplyService.submitApply(soIn);

        ArgumentCaptor<ComplaintApplySoIn> soInCaptor = ArgumentCaptor.forClass(ComplaintApplySoIn.class);
        verify(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), soInCaptor.capture());
        assertEquals(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode(), soInCaptor.getValue().getAuditType());
    }

    @Test
    void submitApply_changeOrg_desOrgIdEqualsCurrentOrg_throwsBusinessException() {
        String complaintNo = "C003";
        String orgId = "F001";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(orgId)
                .createMid(1001L)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintApplyService.submitApply(soIn));
        assertTrue(ex.getMessage().contains("改派门店不能与当前门店相�?));
        verify(complaintApplyTransactionService, never()).doSubmitApplyInTransaction(any(), any());
    }

    @Test
    void submitApply_changeOrg_createSourceStore_throwsBusinessException() {
        String complaintNo = "C004";
        String orgId = "F001";
        String desOrgId = "F002";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);
        orderInfo.setCreateSource(1);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(1001L)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintApplyService.submitApply(soIn));
        assertTrue(ex.getMessage().contains("来源于服务门店的客诉单，不能进行改派"));
        verify(complaintApplyTransactionService, never()).doSubmitApplyInTransaction(any(), any());
    }

    // ======================== startResponsibilityExemptionBpmProcess 单元测试 ========================

    /**
     * 免责申请时，验证BPM流程创建参数（key、name、creator、extraMap�?
     */
    @Test
    void submitApply_applicationForWaiver_bpmProcessCreateCalledWithCorrectParams() {
        String complaintNo = "C010";
        String orgId = "F010";
        Long createMid = 2001L;
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);
        orderInfo.setZoneId("5");
        orderInfo.setLittleZoneId("50");

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "免责申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName(STORE_NAME).zoneId(5).littleZoneId(50).cityId("500").build()));
        when(bpmRemoteGateway.processCreate(any())).thenReturn("PI-BPM-001");
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        complaintApplyService.submitApply(soIn);

        // 捕获BPM创建参数
        ArgumentCaptor<RetailComplaintCreateBPMGoIn> bpmCaptor = ArgumentCaptor.forClass(RetailComplaintCreateBPMGoIn.class);
        verify(bpmRemoteGateway).processCreate(bpmCaptor.capture());

        RetailComplaintCreateBPMGoIn bpmGoIn = bpmCaptor.getValue();
        assertEquals(BPMConst.RESPONSIBILITY_EXEMPTION_INSTANCE_KEY, bpmGoIn.getKey());
        assertEquals(BPMConst.RESPONSIBILITY_EXEMPTION_INSTANCE_NAME, bpmGoIn.getName());
        assertEquals(String.valueOf(createMid), bpmGoIn.getCreator());
        assertNull(bpmGoIn.getRequestId());

        // 验证extraMap包含正确的参�?
        Map<String, Object> extra = bpmGoIn.getExtra();
        assertNotNull(extra);
        assertEquals("50", extra.get(ComplaintInfoConstant.BPM_LITTLE_ZONE_ID_KEY));
        assertEquals("5", extra.get(ComplaintInfoConstant.BPM_BIG_ZONE_ID_KEY));
        assertEquals(complaintNo, extra.get(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY));
        assertEquals(orgId, extra.get(ComplaintInfoConstant.BPM_SHOP_ID_KEY));

        // html和content不为空（由ComplaintApplyUtil构建�?
        assertNotNull(bpmGoIn.getHtml());
        assertNotNull(bpmGoIn.getContent());
    }

    /**
     * 免责申请时，BPM返回的processInstanceId被设置到soIn上并传递到事务服务
     */
    @Test
    void submitApply_applicationForWaiver_processInstanceIdPropagatedToSoIn() {
        String complaintNo = "C011";
        String orgId = "F001";
        Long createMid = 2002L;
        String expectedProcessInstanceId = "PI-EXEMPT-PROP-001";

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        when(bpmRemoteGateway.processCreate(any())).thenReturn(expectedProcessInstanceId);
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        complaintApplyService.submitApply(soIn);

        // 验证processInstanceId被传递到事务服务
        ArgumentCaptor<ComplaintApplySoIn> soInCaptor = ArgumentCaptor.forClass(ComplaintApplySoIn.class);
        verify(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), soInCaptor.capture());
        assertEquals(expectedProcessInstanceId, soInCaptor.getValue().getProcessInstanceId());
    }

    /**
     * 非免责申请类型时，不应调用BPM流程创建
     */
    @Test
    void submitApply_applicationForClosure_doesNotCallBpmProcessCreate() {
        String complaintNo = "C012";
        String orgId = "F001";
        Long createMid = 2003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .solutionDesc("已解�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        complaintApplyService.submitApply(soIn);

        // 非免责申请不应调用BPM
        verify(bpmRemoteGateway, never()).processCreate(any());
    }

    // ======================== submitApplySendMsg 单元测试 ========================

    /**
     * 免责申请成功后不再发�?APPLICATION_FOR_WAIVER_AUDIT（已改由 BPM 通知，与生产 submitApplySendMsg �?APPLICATION_FOR_WAIVER 分支注释保持一致）
     */
    @Test
    void submitApply_applicationForWaiver_doesNotSendWaiverAuditMessage() {
        String complaintNo = "C020";
        String orgId = "F001";
        Long createMid = 3001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        when(bpmRemoteGateway.processCreate(any())).thenReturn("PI-001");
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        complaintApplyService.submitApply(soIn);

        verify(messageInformedEventFactory, never()).getStrategy(APPLICATION_FOR_WAIVER_AUDIT);
        verify(eventPublisher, never()).publishEvent(any());
    }

    /**
     * 改派门店申请成功后发送REASSIGNMENT_STORE_AUDIT消息，且extParams包含targetOrgId
     */
    @Test
    void submitApply_reassignmentStores_sendsReassignmentAuditMessageWithTargetOrgId() {
        String complaintNo = "C021";
        String orgId = "F001";
        String desOrgId = "F002";
        Long createMid = 3002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .applyReason("距离太远")
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "改派申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("原门�?).zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = MessageInformedEvent.builder().complaintNo(complaintNo).build();
        when(messageInformedEventFactory.getStrategy(REASSIGNMENT_STORE_AUDIT)).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        complaintApplyService.submitApply(soIn);

        // 验证使用了REASSIGNMENT_STORE_AUDIT策略
        verify(messageInformedEventFactory).getStrategy(REASSIGNMENT_STORE_AUDIT);

        // 验证extParams包含targetOrgId
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> extParamsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockStrategy).createMessageInformedEvent(any(), extParamsCaptor.capture());
        assertEquals(desOrgId, extParamsCaptor.getValue().get("targetOrgId"));

        verify(eventPublisher).publishEvent(mockEvent);
    }

    /**
     * 申请结案成功后发送APPLICATION_FOR_CLOSURE_AUDIT消息
     */
    @Test
    void submitApply_applicationForClosure_sendsClosureAuditMessage() {
        String complaintNo = "C022";
        String orgId = "F001";
        Long createMid = 3003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .solutionDesc("已解�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = MessageInformedEvent.builder().complaintNo(complaintNo).build();
        when(messageInformedEventFactory.getStrategy(APPLICATION_FOR_CLOSURE_AUDIT)).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        complaintApplyService.submitApply(soIn);

        verify(messageInformedEventFactory).getStrategy(APPLICATION_FOR_CLOSURE_AUDIT);
        verify(mockStrategy).createMessageInformedEvent(any(), any());
        verify(eventPublisher).publishEvent(mockEvent);
    }

    /**
     * 72H无法结案申请成功后发送APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT消息
     */
    @Test
    void submitApply_72hCannotBeClosed_sends72hAuditMessage() {
        String complaintNo = "C023";
        String orgId = "F001";
        Long createMid = 3004L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode())
                .applyReason("零件缺货")
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = MessageInformedEvent.builder().complaintNo(complaintNo).build();
        when(messageInformedEventFactory.getStrategy(APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT)).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        complaintApplyService.submitApply(soIn);

        verify(messageInformedEventFactory).getStrategy(APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT);
        verify(eventPublisher).publishEvent(mockEvent);
    }

    /**
     * 产品风险结案申请成功后发送PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT消息
     */
    @Test
    void submitApply_productRiskClosure_sendsProductRiskClosureAuditMessage() {
        String complaintNo = "C024";
        String orgId = "F001";
        Long createMid = 3005L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setOrgId(orgId);

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .finishApplyV2(true)
                .solutionDesc("已修�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        when(complaintOrderRepositoryGateway.findList(any(OrderListGoIn.class)))
                .thenReturn(Collections.singletonList(orderInfo));
        when(complaintFollowProcessRepositoryGateway.getProcessListByNo(complaintNo)).thenReturn(Collections.emptyList());
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?)));
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.singletonList(
                StoreInfoGoOut.builder().orgId(orgId).orgName("门店").zoneId(1).littleZoneId(10).cityId("100").build()));
        doNothing().when(complaintApplyTransactionService).doSubmitApplyInTransaction(any(), any());

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = MessageInformedEvent.builder().complaintNo(complaintNo).build();
        when(messageInformedEventFactory.getStrategy(PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT)).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        complaintApplyService.submitApply(soIn);

        // resolveAuditTypeForFinishApplyV2 �?auditType 改为 PRODUCT_RISK_CLOSURE_APPLICATION
        verify(messageInformedEventFactory).getStrategy(PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT);
        verify(eventPublisher).publishEvent(mockEvent);
    }

    // ======================== persistComplaintAdjudicationApplyRecord 单元测试 ========================

    /**
     * 正常流程：save成功，返回auditType为JUDGE_RESPONSIBILITY的ComplaintAuditGoIn
     */
    @Test
    void persistComplaintAdjudicationApplyRecord_success_returnsAuditGoInWithJudgeResponsibility() {
        String complaintNo = "C100";
        String orgId = "F001";
        ComplaintOrderInfoGoIn inputGoIn = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        inputGoIn.setOrgId(orgId);

        when(complaintAuditRepositoryGateway.save(any(ComplaintAuditGoIn.class))).thenReturn(true);

        ComplaintAuditGoIn result = complaintApplyService.persistComplaintAdjudicationApplyRecord(inputGoIn, STORE_NAME);

        assertNotNull(result);
        assertEquals(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode(), result.getAuditType());
        assertEquals(complaintNo, result.getComplaintNo());
        assertEquals(orgId, result.getOrgId());
        assertEquals(STORE_NAME, result.getOrgName());
        verify(complaintAuditRepositoryGateway).save(any(ComplaintAuditGoIn.class));
    }

    /**
     * save失败时抛出BusinessException
     */
    @Test
    void persistComplaintAdjudicationApplyRecord_saveFails_throwsBusinessException() {
        String complaintNo = "C101";
        ComplaintOrderInfoGoIn inputGoIn = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        inputGoIn.setOrgId("F001");

        when(complaintAuditRepositoryGateway.save(any(ComplaintAuditGoIn.class))).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> complaintApplyService.persistComplaintAdjudicationApplyRecord(inputGoIn, STORE_NAME));
    }

    /**
     * 成功创建判责审批记录后异步发送JUDGE_RESPONSIBILITY_AUDIT消息
     */
    @Test
    void persistComplaintAdjudicationApplyRecord_success_sendsJudgeResponsibilityMessage() {
        String complaintNo = "C102";
        String orgId = "F001";
        ComplaintOrderInfoGoIn inputGoIn = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        inputGoIn.setOrgId(orgId);
        inputGoIn.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());

        when(complaintAuditRepositoryGateway.save(any(ComplaintAuditGoIn.class))).thenReturn(true);

        // 让异步executor同步执行
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = MessageInformedEvent.builder().complaintNo(complaintNo).build();
        when(messageInformedEventFactory.getStrategy(JUDGE_RESPONSIBILITY_AUDIT)).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        complaintApplyService.persistComplaintAdjudicationApplyRecord(inputGoIn, STORE_NAME);

        verify(messageInformedEventFactory).getStrategy(JUDGE_RESPONSIBILITY_AUDIT);
        verify(eventPublisher).publishEvent(mockEvent);
    }

}
