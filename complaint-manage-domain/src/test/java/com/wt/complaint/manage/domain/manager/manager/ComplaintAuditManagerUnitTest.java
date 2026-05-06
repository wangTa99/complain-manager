package com.wt.complaint.manage.domain.manager;

import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.JudgeResponsibilitySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ComplaintAuditManager 单元测试
 *
 * @author zhangzheyang
 */
@ExtendWith(MockitoExtension.class)
class ComplaintAuditManagerUnitTest {

    @InjectMocks
    private ComplaintAuditManager complaintAuditManager;

    @Mock
    private ComplaintAuditGateway complaintAuditGateway;
    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;
    @Mock
    private ComplaintTagGateway complaintTagGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private RmqGateway rmqGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Mock
    private com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory messageInformedEventFactory;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private MoneThreadPoolExecutor constructMessageEventExecutor;

    @BeforeEach
    void setUp() {
        lenient().when(eiamRemoteGateway.getNameByMid(any())).thenReturn(Collections.singletonMap(1001L, "测试审核�?));
    }

    @Test
    void approveAudit_unknownAuditType_throws() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", 99);
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut));
        assertTrue(ex.getMessage().contains("审批单类型是未知�?));
        verify(complaintAuditGateway, never()).updateAuditById(any());
    }

    @Test
    void approveAudit_application72H_insertTagAndUpdateAudit() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintTagGateway).insertTag(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void approveAudit_applicationForClosure_updatesStatusAndSendsFinish() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        when(rmqGateway.mrOrderStatusFinishMessage(any())).thenReturn(true);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void approveAudit_productRiskClosure_updatesStatusAndSendsFinish() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        when(rmqGateway.mrOrderStatusFinishMessage(any())).thenReturn(true);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void approveAudit_applicationForWaiver_thirdNode_addsTagAndUpdatesOrder() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setCurrentNode(3);
        soIn.setProcessInstanceId("proc-1");
        soIn.setAuditComment("三审通过意见");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintTagGateway).deleteTag(eq("C001"), eq(TagTypeEnum.STORE_RESPONSIBLE.getCode()));
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintTagGateway).insertTag(any());
        ArgumentCaptor<SubmitForApprovalSoIn> soInCaptor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditGateway).updateAuditById(soInCaptor.capture());
        // 三审通过：审批意见保留写入主�?
        assertEquals("三审通过意见", soInCaptor.getValue().getAuditComment());
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_THIRD_PASS.getProcessCode(), processCaptor.getValue().getProcessType());
        RecordInfoGoIn saved = GsonUtil.fromJson(processCaptor.getValue().getProcessContent(), RecordInfoGoIn.class);
        assertEquals("三审通过意见", saved.getAuditReason());
    }

    @Test
    void approveAudit_applicationForWaiver_firstNode_noTagOnlyUpdateAudit() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setCurrentNode(1);
        soIn.setProcessInstanceId("proc-1");
        soIn.setAuditComment("一审通过意见");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintTagGateway, never()).insertTag(any());
        verify(complaintTagGateway, never()).deleteTag(anyString(), anyString());
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        ArgumentCaptor<SubmitForApprovalSoIn> soInCaptor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditGateway).updateAuditById(soInCaptor.capture());
        // 一审通过：不写审批意见到主表，保持审批中并推进到下一节点
        assertNull(soInCaptor.getValue().getAuditComment());
        assertEquals(AuditStatusEnum.PENDING.getCode(), soInCaptor.getValue().getAuditStatus());
        assertEquals(2, soInCaptor.getValue().getCurrentNode().intValue());
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_FIRST_PASS.getProcessCode(), processCaptor.getValue().getProcessType());
        RecordInfoGoIn saved = GsonUtil.fromJson(processCaptor.getValue().getProcessContent(), RecordInfoGoIn.class);
        assertEquals("一审通过意见", saved.getAuditReason());
    }

    @Test
    void approveAudit_applicationForWaiver_secondNode_auditCommentNullAndNodeIncremented() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setCurrentNode(2);
        soIn.setProcessInstanceId("proc-1");
        soIn.setAuditComment("二审通过意见");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintTagGateway, never()).insertTag(any());
        verify(complaintTagGateway, never()).deleteTag(anyString(), anyString());
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        ArgumentCaptor<SubmitForApprovalSoIn> soInCaptor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditGateway).updateAuditById(soInCaptor.capture());
        // 二审通过：不写审批意见到主表，保持审批中并推进到下一节点
        assertNull(soInCaptor.getValue().getAuditComment());
        assertEquals(AuditStatusEnum.PENDING.getCode(), soInCaptor.getValue().getAuditStatus());
        assertEquals(3, soInCaptor.getValue().getCurrentNode().intValue());
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_SECOND_PASS.getProcessCode(), processCaptor.getValue().getProcessType());
        RecordInfoGoIn saved = GsonUtil.fromJson(processCaptor.getValue().getProcessContent(), RecordInfoGoIn.class);
        assertEquals("二审通过意见", saved.getAuditReason());
    }

    @Test
    void approveAudit_applicationForWaiver_legacyNoBpm_usesHistoricalPassCode() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("旧单通过");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintTagGateway).deleteTag(eq("C001"), eq(TagTypeEnum.STORE_RESPONSIBLE.getCode()));
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintTagGateway).insertTag(any());
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_PASS.getProcessCode(), processCaptor.getValue().getProcessType());
    }

    @Test
    void approveAudit_reassignmentStores_success() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setTargetOrgId("F002");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setStatus(ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode());

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgId("F002")
                .orgName("目标门店")
                .zoneId(2)
                .littleZoneId(20)
                .cityId("200")
                .build();
        when(storeRemoteGateway.getStoreInfo("F002")).thenReturn(storeInfo);

        complaintAuditManager.approveAudit(soIn, auditSoOut, orderGoOut);

        verify(storeRemoteGateway).getStoreInfo("F002");
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void refuseAudit_applicationForWaiver_auditCommentPassedToUpdate() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("驳回原因");
        soIn.setCurrentNode(2);
        soIn.setProcessInstanceId("proc-1");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.refuseAudit(soIn, auditSoOut, orderGoOut);

        ArgumentCaptor<SubmitForApprovalSoIn> soInCaptor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditGateway).updateAuditById(soInCaptor.capture());
        // 驳回时一�?二审/三审均写入审批意�?
        assertEquals("驳回原因", soInCaptor.getValue().getAuditComment());
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_SECOND_REJECT.getProcessCode(), processCaptor.getValue().getProcessType());
    }

    @Test
    void refuseAudit_applicationForWaiver_legacyNoBpm_usesHistoricalRejectCode() {
        // legacyNoBpm 场景：审批单�?currentNode（BPM 接入前的历史数据），
        // resolveExemptionRejectProcessType 检测到 node==null 时降级返�?AUDIT_EXEMPTION_REJECT
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("驳回原因");
        // 不设�?currentNode，模拟旧数据�?BPM 节点信息
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.refuseAudit(soIn, auditSoOut, orderGoOut);

        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_REJECT.getProcessCode(), processCaptor.getValue().getProcessType());
    }

    @Test
    void judgeResponsibility_responsible_updatesOrderAndInsertsTag() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(1001L);
        req.setComplaintNo("C001");
        req.setAuditMid(1001L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        complaintAuditManager.judgeResponsibility(req, auditSoOut, orderGoOut);

        verify(complaintAuditGateway).updateAuditById(any(SubmitForApprovalSoIn.class));
        verify(complaintTagGateway).deleteTag(eq("C001"), eq(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode()));
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintTagGateway).insertTag(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void judgeResponsibility_notResponsible_insertsTagNoOrderUpdate() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(1002L);
        req.setComplaintNo("C002");
        req.setAuditMid(1001L);
        req.setResponsible(0);
        req.setResponsibleJudgeDesc("无责");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C002", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C002", 1);

        complaintAuditManager.judgeResponsibility(req, auditSoOut, orderGoOut);

        verify(complaintAuditGateway).updateAuditById(any(SubmitForApprovalSoIn.class));
        verify(complaintTagGateway).deleteTag(eq("C002"), eq(TagTypeEnum.STORE_RESPONSIBLE.getCode()));
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintTagGateway).insertTag(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void cancelAudit_applicationForWaiver_exemptionApplyTimesGreaterThanZero_updatesOrderAndSavesProcess() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("撤销原因");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setExemptionApplyTimes(2);

        complaintAuditManager.cancelAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
        assertEquals(1, orderGoOut.getExemptionApplyTimes().intValue());
    }

    @Test
    void cancelAudit_nonWaiverAuditType_throws() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintAuditManager.cancelAudit(soIn, auditSoOut, orderGoOut));
        assertTrue(ex.getMessage().contains("仅支持免责审�?));
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        verify(complaintAuditGateway, never()).updateAuditById(any());
    }

    @Test
    void cancelAudit_exemptionApplyTimesZero_onlyUpdatesAuditAndSavesProcess() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setExemptionApplyTimes(0);

        complaintAuditManager.cancelAudit(soIn, auditSoOut, orderGoOut);

        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        verify(complaintAuditGateway).updateAuditById(soIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    // ============ cancelAudit 补充测试 ============

    @Test
    void cancelAudit_exemptionTimesOne_decrementsToZero_updatesCorrectly() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("撤销原因");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setExemptionApplyTimes(1);

        complaintAuditManager.cancelAudit(soIn, auditSoOut, orderGoOut);

        // 验证免责申请次数�?递减�?
        assertEquals(0, orderGoOut.getExemptionApplyTimes().intValue());
        ArgumentCaptor<ComplaintOrderInfoGoIn> updateCaptor = ArgumentCaptor.forClass(ComplaintOrderInfoGoIn.class);
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(updateCaptor.capture());
        assertEquals(0, updateCaptor.getValue().getExemptionApplyTimes().intValue());
        assertEquals("C001", updateCaptor.getValue().getComplaintNo());
    }

    @Test
    void cancelAudit_verifiesSavedProcess_hasWithdrawType() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(1001L);
        soIn.setComplaintNo("C001");
        soIn.setAuditMid(1001L);
        soIn.setAuditComment("撤销");
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setExemptionApplyTimes(0);

        complaintAuditManager.cancelAudit(soIn, auditSoOut, orderGoOut);

        // 验证保存的跟进记录为"申请免责-申请撤回"类型
        ArgumentCaptor<ComplaintFollowProcessGoIn> processCaptor = ArgumentCaptor.forClass(ComplaintFollowProcessGoIn.class);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processCaptor.capture());
        assertEquals(ProcessTypeEnum.AUDIT_EXEMPTION_WITHDRAW.getProcessCode(), processCaptor.getValue().getProcessType());
        assertEquals("C001", processCaptor.getValue().getComplaintNo());
    }

    // ============ sendReassignmentMsg 测试 ============

    /**
     * 通过反射调用私有方法sendReassignmentMsg
     */
    private void invokeSendReassignmentMsg(ComplaintOrderGoOut orderGoOut,
                                           ComplaintOrderInfoGoIn updateInfo) throws Exception {
        Method method = ComplaintAuditManager.class.getDeclaredMethod("sendReassignmentMsg",
                ComplaintOrderGoOut.class, ComplaintOrderInfoGoIn.class);
        method.setAccessible(true);
        try {
            method.invoke(complaintAuditManager, orderGoOut, updateInfo);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * 构建改派消息测试用的updateInfo
     */
    private ComplaintOrderInfoGoIn buildUpdateInfoForReassignment() {
        ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
        updateInfo.setOrgId("F002");
        updateInfo.setZoneId("2");
        updateInfo.setLittleZoneId("20");
        updateInfo.setCityId("200");
        return updateInfo;
    }

    /**
     * 设置executor同步执行，并mock消息策略
     */
    private void setupSyncExecutorAndMockStrategy(MessageInformedStrategy mockStrategy, MessageInformedEvent mockEvent) {
        doAnswer(inv -> {
            inv.getArgument(0, Runnable.class).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));
        lenient().when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);
    }

    @Test
    void sendReassignmentMsg_normalOrder_sendsNewComplaintToDeal() throws Exception {
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setOnlyView(0);
        orderGoOut.setMediaInvolved(0);
        ComplaintOrderInfoGoIn updateInfo = buildUpdateInfoForReassignment();

        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = mock(MessageInformedEvent.class);
        setupSyncExecutorAndMockStrategy(mockStrategy, mockEvent);
        when(messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL)).thenReturn(mockStrategy);

        invokeSendReassignmentMsg(orderGoOut, updateInfo);

        // 验证使用NEW_COMPLAINT_TO_DEAL策略发送消�?
        verify(messageInformedEventFactory).getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);
        verify(messageInformedEventFactory, never()).getStrategy(PushConstant.NEW_COMPLAINT_TO_VIEW);
        verify(eventPublisher).publishEvent(mockEvent);
    }

    @Test
    void sendReassignmentMsg_onlyViewOrder_sendsNewComplaintToView() throws Exception {
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setOnlyView(1);
        orderGoOut.setMediaInvolved(0);
        ComplaintOrderInfoGoIn updateInfo = buildUpdateInfoForReassignment();

        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = mock(MessageInformedEvent.class);
        setupSyncExecutorAndMockStrategy(mockStrategy, mockEvent);
        when(messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_VIEW)).thenReturn(mockStrategy);

        invokeSendReassignmentMsg(orderGoOut, updateInfo);

        // 验证使用NEW_COMPLAINT_TO_VIEW策略发送消�?
        verify(messageInformedEventFactory).getStrategy(PushConstant.NEW_COMPLAINT_TO_VIEW);
        verify(messageInformedEventFactory, never()).getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);
        verify(eventPublisher).publishEvent(mockEvent);
    }

    @Test
    void sendReassignmentMsg_mediaInvolvedOrder_sendsBothMessages() throws Exception {
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setOnlyView(0);
        orderGoOut.setMediaInvolved(1);
        ComplaintOrderInfoGoIn updateInfo = buildUpdateInfoForReassignment();

        MessageInformedStrategy dealStrategy = mock(MessageInformedStrategy.class);
        MessageInformedStrategy mediaStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent dealEvent = mock(MessageInformedEvent.class);
        MessageInformedEvent mediaEvent = mock(MessageInformedEvent.class);

        doAnswer(inv -> {
            inv.getArgument(0, Runnable.class).run();
            return null;
        }).when(constructMessageEventExecutor).execute(any(Runnable.class));

        when(messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL)).thenReturn(dealStrategy);
        when(dealStrategy.createMessageInformedEvent(any(), any())).thenReturn(dealEvent);
        when(messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT)).thenReturn(mediaStrategy);
        when(mediaStrategy.createMessageInformedEvent(any(), any())).thenReturn(mediaEvent);

        invokeSendReassignmentMsg(orderGoOut, updateInfo);

        // 验证发送了两条消息：改派通知 + 涉媒通知
        verify(eventPublisher).publishEvent(dealEvent);
        verify(eventPublisher).publishEvent(mediaEvent);
        verify(messageInformedEventFactory).getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);
        verify(messageInformedEventFactory).getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);
    }

    @Test
    void sendReassignmentMsg_notMediaInvolved_sendsOnlyOneMessage() throws Exception {
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setOnlyView(0);
        orderGoOut.setMediaInvolved(0);
        ComplaintOrderInfoGoIn updateInfo = buildUpdateInfoForReassignment();

        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent mockEvent = mock(MessageInformedEvent.class);
        setupSyncExecutorAndMockStrategy(mockStrategy, mockEvent);
        when(messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL)).thenReturn(mockStrategy);

        invokeSendReassignmentMsg(orderGoOut, updateInfo);

        // 验证仅发�?条消息，不发送涉媒通知
        verify(eventPublisher).publishEvent(mockEvent);
        verify(messageInformedEventFactory, never()).getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);
    }
}
