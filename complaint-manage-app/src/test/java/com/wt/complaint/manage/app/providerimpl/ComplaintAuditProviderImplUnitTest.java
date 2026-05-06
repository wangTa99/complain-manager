package com.wt.complaint.manage.app.providerimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.req.approve.ComplaintAuditDetailReq;
import com.wt.complaint.manage.api.model.req.approve.ComplaintAuditListReq;
import com.wt.complaint.manage.api.model.req.approve.JudgeResponsibilityReq;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditDetailResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditListResp;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintAuditService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.xiaomi.youpin.infra.rpc.Result;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.api.model.req.approve.SubmitForApprovalReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ComplaintAuditProviderImpl单元测试
 * 测试审批提供者层接口实现
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintAuditProviderImplUnitTest {

    @InjectMocks
    private ComplaintAuditProviderImpl complaintAuditProvider;

    @Mock
    private ComplaintAuditService complaintAuditService;

    @Mock
    private CustomeUserContext customeUserContext;

    @BeforeEach
    void setUp() {
        // Mock UserService返回UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(1001L);
        userInfo.setRoleList("");
        when(customeUserContext.fromRpcContextForAftersaleWorkbench()).thenReturn(userInfo);
    }

    /**
     * 测试查询审批列表成功
     */
    @Test
    void testSearchComplaintAuditList_Success() {
        // 准备数据
        ComplaintAuditListReq req = new ComplaintAuditListReq();
        req.setPageNum(1);
        req.setPageSize(10);
        req.setAuditTypeList(Arrays.asList(
                AuditTypeEnum.REASSIGNMENT_STORES.getCode(),
                AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()
        ));
        
        // Mock Service层返�?
        ComplaintAuditListSoOut soOut = new ComplaintAuditListSoOut();
        soOut.setTotal(2L);
        ComplaintAuditSoOut audit1 = new ComplaintAuditSoOut();
        audit1.setId(1L);
        audit1.setComplaintNo("C001");
        audit1.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        audit1.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        
        ComplaintAuditSoOut audit2 = new ComplaintAuditSoOut();
        audit2.setId(2L);
        audit2.setComplaintNo("C002");
        audit2.setAuditType(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode());
        audit2.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        
        soOut.setDataList(Lists.newArrayList(audit1, audit2));
        when(complaintAuditService.searchComplaintAuditList(any(ComplaintAuditListSoIn.class))).thenReturn(soOut);
        
        // 执行
        Result<ComplaintAuditListResp> result = complaintAuditProvider.searchComplaintAuditList(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        ComplaintAuditListResp resp = result.getData();
        assertNotNull(resp);
        assertEquals(2L, resp.getTotal());
        assertEquals(2, resp.getDataList().size());
        assertEquals("C001", resp.getDataList().get(0).getComplaintNo());
        assertEquals("C002", resp.getDataList().get(1).getComplaintNo());
        
        // 验证Service被调�?
        verify(complaintAuditService).searchComplaintAuditList(any(ComplaintAuditListSoIn.class));
    }

    /**
     * 测试查询审批列表 - 带权限过滤查�?
     */
    @Test
    void testSearchComplaintAuditList_WithPermissionFilter() {
        // 准备数据
        ComplaintAuditListReq req = new ComplaintAuditListReq();
        req.setPageNum(1);
        req.setPageSize(10);
        // 只查询产品风�?申请结案类型
        req.setAuditTypeList(Lists.newArrayList(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
        
        // Mock Service层返�?- 空列表（权限不足�?
        ComplaintAuditListSoOut soOut = new ComplaintAuditListSoOut();
        soOut.setTotal(0L);
        soOut.setDataList(new ArrayList<>());
        when(complaintAuditService.searchComplaintAuditList(any(ComplaintAuditListSoIn.class))).thenReturn(soOut);
        
        // 执行
        Result<ComplaintAuditListResp> result = complaintAuditProvider.searchComplaintAuditList(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        ComplaintAuditListResp resp = result.getData();
        assertNotNull(resp);
        assertEquals(0L, resp.getTotal());
        assertTrue(resp.getDataList().isEmpty());
        
        // 验证Service被调�?
        verify(complaintAuditService).searchComplaintAuditList(any(ComplaintAuditListSoIn.class));
    }

    /**
     * 测试获取审批详情成功
     */
    @Test
    void testGetComplaintAuditDetail_Success() {
        // 准备数据
        ComplaintAuditDetailReq req = new ComplaintAuditDetailReq();
        req.setId(1001L);
        
        // Mock Service层返�?
        ComplaintAuditSoOut soOut = new ComplaintAuditSoOut();
        soOut.setId(1001L);
        soOut.setComplaintNo("C001");
        soOut.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        soOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        soOut.setZoneId("1");
        soOut.setLittleZoneId("10");
        soOut.setApplyContent("{\"desOrgId\":\"F001\"}");
        when(complaintAuditService.getComplaintAuditDetail(any(ComplaintAuditDetailSoIn.class))).thenReturn(soOut);
        
        // 执行
        Result<ComplaintAuditDetailResp> result = complaintAuditProvider.getComplaintAuditDetail(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        ComplaintAuditDetailResp resp = result.getData();
        assertNotNull(resp);
        assertEquals(1001L, resp.getId());
        assertEquals("C001", resp.getComplaintNo());
        assertEquals(AuditTypeEnum.REASSIGNMENT_STORES.getCode(), resp.getAuditType());
        
        // 验证Service被调�?
        verify(complaintAuditService).getComplaintAuditDetail(any(ComplaintAuditDetailSoIn.class));
    }

    @Test
    void testSubmitForApproval_NonWaiver_Success() {
        SubmitForApprovalReq req = new SubmitForApprovalReq();
        req.setId(123L);
        req.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        when(complaintAuditService.checkAuditParams(any(SubmitForApprovalSoIn.class))).thenReturn(auditSoOut);
        when(complaintAuditService.submitForApproval(any(SubmitForApprovalSoIn.class), any(ComplaintAuditSoOut.class), eq(Boolean.FALSE))).thenReturn(true);

        Result<Boolean> result = complaintAuditProvider.submitForApproval(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());

        ArgumentCaptor<SubmitForApprovalSoIn> captor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditService).submitForApproval(captor.capture(), any(ComplaintAuditSoOut.class), eq(Boolean.FALSE));
        assertEquals(1001L, captor.getValue().getAuditMid());
        verify(complaintAuditService, never()).submitForApprovalResponsibilityExemption(any(), any());
    }

    @Test
    void testSubmitForApproval_ResponsibilityExemption_Success() {
        SubmitForApprovalReq req = new SubmitForApprovalReq();
        req.setId(456L);
        req.setAuditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setAuditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        when(complaintAuditService.checkAuditParams(any(SubmitForApprovalSoIn.class))).thenReturn(auditSoOut);
        when(complaintAuditService.submitForApprovalResponsibilityExemption(any(SubmitForApprovalSoIn.class), any(ComplaintAuditSoOut.class))).thenReturn(true);

        Result<Boolean> result = complaintAuditProvider.submitForApproval(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());

        ArgumentCaptor<SubmitForApprovalSoIn> captor = ArgumentCaptor.forClass(SubmitForApprovalSoIn.class);
        verify(complaintAuditService).submitForApprovalResponsibilityExemption(captor.capture(), any(ComplaintAuditSoOut.class));
        assertEquals(1001L, captor.getValue().getAuditMid());
        verify(complaintAuditService, never()).submitForApproval(any(), any(), any());
    }

    @Test
    void testSubmitForApproval_BusinessException() {
        SubmitForApprovalReq req = new SubmitForApprovalReq();
        req.setId(789L);
        req.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        req.setAuditStatus(AuditStatusEnum.REJECTED.getCode());

        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        when(complaintAuditService.checkAuditParams(any(SubmitForApprovalSoIn.class))).thenReturn(auditSoOut);
        doThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "参数错误"))
                .when(complaintAuditService).submitForApproval(any(SubmitForApprovalSoIn.class), any(ComplaintAuditSoOut.class), eq(Boolean.FALSE));

        Result<Boolean> result = complaintAuditProvider.submitForApproval(req);

        assertNotNull(result);
        assertEquals(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode().getCode(), result.getCode());
        verify(complaintAuditService).submitForApproval(any(SubmitForApprovalSoIn.class), any(ComplaintAuditSoOut.class), eq(Boolean.FALSE));
        verify(complaintAuditService, never()).submitForApprovalResponsibilityExemption(any(), any());
    }

    @Test
    void testJudgeResponsibility_Success() {
        JudgeResponsibilityReq req = new JudgeResponsibilityReq();
        req.setId(1001L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责说明");

        when(complaintAuditService.judgeResponsibility(any())).thenReturn(true);

        Result<Boolean> result = complaintAuditProvider.judgeResponsibility(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(complaintAuditService).judgeResponsibility(any());
    }

    @Test
    void testJudgeResponsibility_BusinessException() {
        JudgeResponsibilityReq req = new JudgeResponsibilityReq();
        req.setId(1002L);
        req.setResponsible(0);
        req.setResponsibleJudgeDesc("无责说明");

        doThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单状态不允许判责"))
                .when(complaintAuditService).judgeResponsibility(any());

        Result<Boolean> result = complaintAuditProvider.judgeResponsibility(req);

        assertNotNull(result);
        assertEquals(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode().getCode(), result.getCode());
        verify(complaintAuditService).judgeResponsibility(any());
    }

    @Test
    void testJudgeResponsibility_GenericException() {
        JudgeResponsibilityReq req = new JudgeResponsibilityReq();
        req.setId(1003L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");

        doThrow(new RuntimeException("系统异常"))
                .when(complaintAuditService).judgeResponsibility(any());

        Result<Boolean> result = complaintAuditProvider.judgeResponsibility(req);

        assertNotNull(result);
        assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode().getCode(), result.getCode());
        verify(complaintAuditService).judgeResponsibility(any());
    }
}
