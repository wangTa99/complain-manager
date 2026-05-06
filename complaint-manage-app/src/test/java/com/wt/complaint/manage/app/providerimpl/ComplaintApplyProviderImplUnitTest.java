package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.apply.ExemptionApplyReq;
import com.wt.complaint.manage.api.model.req.apply.Org72HFreeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgFinishApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintApplyService;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ComplaintApplyProviderImpl 单元测试
 * 不启�?Spring 容器，使�?Mock 覆盖申请相关方法
 *
 * @author zhangzheyang
 * @date 2026/01/29
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
public class ComplaintApplyProviderImplUnitTest {

    @InjectMocks
    private ComplaintApplyProviderImpl complaintApplyProvider;

    @Mock
    private ComplaintApplyService complaintApplyService;

    @BeforeEach
    void setUp() {
        RpcContext.removeContext();
    }

    @Test
    void submitChangeOrgApply_success() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        OrgChangeApplyReq req = new OrgChangeApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setDesOrgId("F002");
        req.setReassignRemark("改派说明");

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(100L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submitChangeOrgApply(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }

    @Test
    void submitExemptionApply_success() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        ExemptionApplyReq req = new ExemptionApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setApplyReason("免责原因");

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(101L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submitExemptionApply(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }

    @Test
    void submit72HFreeApply_success() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        Org72HFreeApplyReq req = new Org72HFreeApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setApplyReason("72H无法结案原因");

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(102L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submit72HFreeApply(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }

    @Test
    void submitFinishApply_success() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        OrgFinishApplyReq req = new OrgFinishApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setSolutionDesc("解决方案");

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(103L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submitFinishApply(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }

    @Test
    void submitFinishApplyV2_success() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        OrgFinishApplyReq req = new OrgFinishApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setSolutionDesc("解决方案");
        req.setUserAgreement(1);
        req.setVehicleRepaired(1);

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(104L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submitFinishApplyV2(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }

    @Test
    void submitFinishApplyV2_userAgreementNull_returnsValidateError() {
        OrgFinishApplyReq req = new OrgFinishApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setUserAgreement(null);
        req.setVehicleRepaired(1);

        Result<OrgApplyResp> result = complaintApplyProvider.submitFinishApplyV2(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("是否与用户达成一致不能为�?));
        verify(complaintApplyService, never()).submitApply(any());
    }

    @Test
    void submitFinishApplyV2_vehicleRepairedNull_returnsValidateError() {
        OrgFinishApplyReq req = new OrgFinishApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setUserAgreement(1);
        req.setVehicleRepaired(null);

        Result<OrgApplyResp> result = complaintApplyProvider.submitFinishApplyV2(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("车辆异常是否修复不能为空"));
        verify(complaintApplyService, never()).submitApply(any());
    }

    @Test
    void submitChangeOrgApply_businessException_returnsFail() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        OrgChangeApplyReq req = new OrgChangeApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setDesOrgId("F002");

        when(complaintApplyService.submitApply(any()))
                .thenThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不可为空"));

        Result<OrgApplyResp> result = complaintApplyProvider.submitChangeOrgApply(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("客诉单号不可为空"));
    }

    @Test
    void submitChangeOrgApply_genericException_returnsInternalError() {
        RpcContext.getContext().setAttachment("$upc_miID", "1001");
        OrgChangeApplyReq req = new OrgChangeApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");

        when(complaintApplyService.submitApply(any())).thenThrow(new RuntimeException("unexpected"));

        Result<OrgApplyResp> result = complaintApplyProvider.submitChangeOrgApply(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getName(), result.getMessage());
    }

    @Test
    void submitChangeOrgApply_rpcContextMidEmpty_createMidNull() {
        // 不设�?RpcContext，miID 为空，createMid 应为 null，不�?NPE
        OrgChangeApplyReq req = new OrgChangeApplyReq();
        req.setComplaintNo("C001");
        req.setApplyOrgId("F001");
        req.setDesOrgId("F002");
        req.setReassignRemark("改派");

        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(105L);
        when(complaintApplyService.submitApply(any())).thenReturn(soOut);

        Result<OrgApplyResp> result = complaintApplyProvider.submitChangeOrgApply(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintApplyService).submitApply(any());
    }
}
