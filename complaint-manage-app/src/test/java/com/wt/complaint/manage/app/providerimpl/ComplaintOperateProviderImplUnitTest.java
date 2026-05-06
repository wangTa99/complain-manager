package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.AddKindPointsDistributionRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReqV2;
import com.wt.complaint.manage.api.model.req.operate.*;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintService;
import com.wt.complaint.manage.domain.api.service.interfaces.UserComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.SubmitReviewSoOut;
import com.wt.complaint.manage.domain.constant.CommonConst;
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

import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ComplaintOperateProviderImpl单元测试
 * 测试操作提供者层接口实现
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintOperateProviderImplUnitTest {

    @InjectMocks
    private ComplaintOperateProviderImpl complaintOperateProvider;

    @Mock
    private ComplaintOperateService complaintOperateService;

    @Mock
    private UserComplaintOperateService userComplaintOperateService;

    @Mock
    private DeliverComplaintService deliverComplaintService;

    @BeforeEach
    void setUp() {
        RpcContext.removeContext();
    }

    @Test
    void testCreateComplaintOrder_Success() {
        CreateComplaintOrderReq req = new CreateComplaintOrderReq();
        req.setVid("V001");
        req.setOrgId("F001");
        ComplaintOrderCreateSoOut soOut = new ComplaintOrderCreateSoOut();
        soOut.setComplaintNo("C001");
        when(complaintOperateService.createComplaintOrder(any(ComplaintOrderCreateSoIn.class))).thenReturn(soOut);
        Result<CreateComplaintOrderResp> result = complaintOperateProvider.createComplaintOrder(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("C001", result.getData().getWorkNo());
        verify(complaintOperateService).createComplaintOrder(any(ComplaintOrderCreateSoIn.class));
    }

    @Test
    void testCreateComplaintOrder_BusinessException_ReturnsFail() {
        CreateComplaintOrderReq req = new CreateComplaintOrderReq();
        req.setVid("V001");
        req.setOrgId("F001");
        when(complaintOperateService.createComplaintOrder(any())).thenThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "门店不存�?));
        Result<CreateComplaintOrderResp> result = complaintOperateProvider.createComplaintOrder(req);
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("门店不存�?));
    }

    @Test
    void testPickUpOrder_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, "OPERATOR");
        PickUpOrderReq req = new PickUpOrderReq();
        req.setComplaintNo("C001");
        OrderPickUpSoOut soOut = new OrderPickUpSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.pickUpOrder(any(OrderPickUpSoIn.class))).thenReturn(soOut);
        Result<PickUpOrderResp> result = complaintOperateProvider.pickUpOrder(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(complaintOperateService).pickUpOrder(any(OrderPickUpSoIn.class));
    }

    @Test
    void testUpdateHandler_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, "DISPATCHER");
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setComplaintNo("C001");
        req.setHandlerMid("2001");
        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.updateHandler(any(OrderUpdateHandlerSoIn.class))).thenReturn(soOut);
        Result<UpdateHandlerResp> result = complaintOperateProvider.updateHandler(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintOperateService).updateHandler(any(OrderUpdateHandlerSoIn.class));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testAddFollowRecord_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, "OPERATOR");
        FollowRecordReq req = new FollowRecordReq();
        req.setComplaintNo("C001");
        req.setFollowInfo("跟进内容");
        OrderFollowUpRecordSoOut soOut = new OrderFollowUpRecordSoOut();
        soOut.setRecordResult("SUCCESS");
        when(complaintOperateService.addFollowUpRecords(any(OrderAddFollowUpRecordSoIn.class))).thenReturn(soOut);
        Result<AddFollowRecordResp> result = complaintOperateProvider.addFollowRecord(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("SUCCESS", result.getData().getResult());
        verify(complaintOperateService).addFollowUpRecords(any(OrderAddFollowUpRecordSoIn.class));
    }

    @Test
    void testAddKindPointsDistributionRecord_Success() {
        AddKindPointsDistributionRecordReq req = new AddKindPointsDistributionRecordReq();
        req.setComplaintNo("C001");
        req.setDistributionId(1L);
        OrderAddDistributionRecordSoOut soOut = new OrderAddDistributionRecordSoOut();
        soOut.setRecordResult("SUCCESS");
        when(complaintOperateService.addDistributionRecords(any(OrderAddDistributionRecordSoIn.class))).thenReturn(soOut);
        Result<AddDistributionRecordResp> result = complaintOperateProvider.addKindPointsDistributionRecord(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintOperateService).addDistributionRecords(any(OrderAddDistributionRecordSoIn.class));
    }

    @Test
    void testRemindOrder_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        RemindOrderReq req = new RemindOrderReq();
        req.setComplaintNo("C001");
        OrderRemindSoOut soOut = new OrderRemindSoOut();
        soOut.setRemindResult("SUCCESS");
        when(complaintOperateService.remindOrder(any(OrderRemindSoIn.class))).thenReturn(soOut);
        Result<RemindOrderResp> result = complaintOperateProvider.remindOrder(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintOperateService).remindOrder(any(OrderRemindSoIn.class));
    }

    @Test
    void testUpdateCustomerService_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        UpdateCustomerServiceReq req = new UpdateCustomerServiceReq();
        req.setCustomerServiceReqList(Collections.singletonList(
                CustomerServiceReq.builder().stNo("C001").customerServiceMid(2001L).build()));
        OrderUpdateCustomerServiceSoOut ucSoOut = new OrderUpdateCustomerServiceSoOut();
        ucSoOut.setUpdateResult(true);
        OrderUpdateCustomerServiceSoOut complaintSoOut = new OrderUpdateCustomerServiceSoOut();
        complaintSoOut.setUpdateResult(false);
        when(userComplaintOperateService.updateCustomer(any(OrderUpdateCustomerServiceSoIn.class))).thenReturn(ucSoOut);
        when(complaintOperateService.updateCustomerService(any(OrderUpdateCustomerServiceSoIn.class))).thenReturn(complaintSoOut);
        when(deliverComplaintService.updateCustomer(any(OrderUpdateCustomerServiceSoIn.class))).thenReturn(false);
        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.updateCustomerService(req);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("success", result.getData().getResult());
        verify(userComplaintOperateService).updateCustomer(any(OrderUpdateCustomerServiceSoIn.class));
        verify(complaintOperateService).updateCustomerService(any(OrderUpdateCustomerServiceSoIn.class));
    }

    @Test
    void testUpgradeComplaintOrder_Success() {
        // 设置RpcContext中的miID
        String miID = "1001";
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, miID);

        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("C001");
        req.setTargetType(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        req.setUpgradeReason("测试升级原因");
        
        // Mock Service层返�?
        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.upgradeComplaintOrder(any(ComplaintOrderUpgradeSoIn.class))).thenReturn(soOut);
        
        // 执行
        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("SUCCESS", result.getData().getResult());
        
        // 验证Service被调�?
        verify(complaintOperateService).upgradeComplaintOrder(any(ComplaintOrderUpgradeSoIn.class));
    }

    /**
     * 测试升级投诉 - 业务异常时返�?fail（v-zhengshuiguang e8e4187�?
     */
    @Test
    void testUpgradeComplaint_BusinessException_ReturnsFail() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("C999");
        req.setTargetType(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        req.setUpgradeReason("测试");

        when(complaintOperateService.upgradeComplaintOrder(any(ComplaintOrderUpgradeSoIn.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前投诉单不是产品风险分类无法升�?));

        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("不是产品风险"));
    }

    /**
     * 测试升级投诉 - operateSource �?CUSTOMER_SERVICE_WORKBENCH
     * 验证 provider 层正确透传 operateSource，使 service 层能进入判责审批任务的分�?
     */
    @Test
    void testUpgradeComplaint_OperateSource_CustomerServiceWorkbench() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");

        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("C001");
        req.setTargetType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        req.setUpgradeReason("客服工作台升级测�?);
        req.setOperateSource("CUSTOMER_SERVICE_WORKBENCH");

        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.upgradeComplaintOrder(any(ComplaintOrderUpgradeSoIn.class))).thenReturn(soOut);

        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("SUCCESS", result.getData().getResult());

        // 验证 operateSource 被正确透传�?service �?
        ArgumentCaptor<ComplaintOrderUpgradeSoIn> captor = ArgumentCaptor.forClass(ComplaintOrderUpgradeSoIn.class);
        verify(complaintOperateService).upgradeComplaintOrder(captor.capture());
        assertEquals("CUSTOMER_SERVICE_WORKBENCH", captor.getValue().getOperateSource());
    }

    /**
     * 测试升级投诉 - operateSource �?PAD_DETAIL
     * 验证 provider 层正确透传 operateSource，使 service 层跳过判责审批任务的分支
     */
    @Test
    void testUpgradeComplaint_OperateSource_PadDetail() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");

        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("C001");
        req.setTargetType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        req.setUpgradeReason("PAD详情页升级测�?);
        req.setOperateSource("PAD_DETAIL");

        OrderUpdateHandlerSoOut soOut = new OrderUpdateHandlerSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.upgradeComplaintOrder(any(ComplaintOrderUpgradeSoIn.class))).thenReturn(soOut);

        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("SUCCESS", result.getData().getResult());

        // 验证 operateSource 被正确透传�?service �?
        ArgumentCaptor<ComplaintOrderUpgradeSoIn> captor = ArgumentCaptor.forClass(ComplaintOrderUpgradeSoIn.class);
        verify(complaintOperateService).upgradeComplaintOrder(captor.capture());
        assertEquals("PAD_DETAIL", captor.getValue().getOperateSource());
    }

    /**
     * 测试编辑客诉单成�?
     */
    @Test
    void testEditComplaint_Success() {
        // 设置RpcContext中的miID
        String miID = "1001";
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, miID);

        EditComplaintReq req = new EditComplaintReq();
        req.setComplaintNo("C002");
        
        // 设置投诉场景
        FieldValue complaint = new FieldValue();
        complaint.setCode("SC002");
        complaint.setDesc("售后体验");
        complaint.setPathId("2/3/4");
        complaint.setPathName("售后/售后体验/售后体验");
        req.setComplaint(complaint);
        
        // 设置风险等级
        req.setRiskLevel("2");
        
        // 设置涉媒信息
        req.setMediaInvolved("1");
        req.setMediaLink("http://test.com");
        
        // Mock Service层返�?
        OrderEditComplaintSoOut soOut = new OrderEditComplaintSoOut();
        soOut.setResult("SUCCESS");
        when(complaintOperateService.editComplaint(any(OrderEditComplaintSoIn.class))).thenReturn(soOut);
        
        // 执行
        Result<EditComplaintResp> result = complaintOperateProvider.editComplaint(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("SUCCESS", result.getData().getResult());
        
        // 验证Service被调�?
        verify(complaintOperateService).editComplaint(any(OrderEditComplaintSoIn.class));
    }

    /**
     * 测试跟进记录V2成功
     */
    @Test
    void testAddFollowUpRecordV2_Success() {
        // 设置RpcContext中的miID和role
        String miID = "1001";
        String role = "OPERATOR";
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, miID);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, role);

        FollowRecordReqV2 req = new FollowRecordReqV2();
        req.setComplaintNo("C003");
        req.setFollowInfo("测试跟进内容");
        req.setAttachmentList(new ArrayList<>());
        req.setMileage("1000.50");
        
        // Mock Service层返�?
        OrderFollowUpRecordSoOut soOut = new OrderFollowUpRecordSoOut();
        soOut.setRecordResult("SUCCESS");
        when(complaintOperateService.addFollowUpRecordsV2(any(OrderAddFollowUpRecordSoInV2.class))).thenReturn(soOut);
        
        // 执行
        Result<AddFollowRecordResp> result = complaintOperateProvider.addFollowRecordV2(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("SUCCESS", result.getData().getResult());
        
        // 验证Service被调�?
        verify(complaintOperateService).addFollowUpRecordsV2(any(OrderAddFollowUpRecordSoInV2.class));
    }

    /**
     * 测试提交复盘成功
     */
    @Test
    void testSubmitReview_Success() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        SubmitReviewReq req = new SubmitReviewReq();
        req.setComplaintNo("C030");
        req.setReviewMaterial("https://xxx.feishu.cn/docx/xxx");
        SubmitReviewSoOut soOut = SubmitReviewSoOut.builder().success(true).build();
        when(complaintOperateService.submitReview(any(SubmitReviewSoIn.class))).thenReturn(soOut);

        Result<SubmitReviewResp> result = complaintOperateProvider.submitReview(req);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().getSuccess());
        verify(complaintOperateService).submitReview(any(SubmitReviewSoIn.class));
    }

    /**
     * 测试提交复盘 - 业务异常时返回失�?
     */
    @Test
    void testSubmitReview_BusinessException_ReturnsFail() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");
        SubmitReviewReq req = new SubmitReviewReq();
        req.setComplaintNo("C031");
        req.setReviewMaterial("https://xxx.feishu.cn/docx/xxx");
        when(complaintOperateService.submitReview(any(SubmitReviewSoIn.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "该客诉单已提交过复盘"));

        Result<SubmitReviewResp> result = complaintOperateProvider.submitReview(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("已提交过复盘"));
    }

    // ======================== submitReview 单元测试 ========================

    /**
     * 测试提交复盘 - 未登录（miID为空）抛出BusinessException
     */
    @Test
    void testSubmitReview_MidEmpty_ReturnsFail() {
        // 不设置RpcContext中的miID，模拟未登录

        SubmitReviewReq req = new SubmitReviewReq();
        req.setComplaintNo("TS256851079776454");
        req.setReviewMaterial("https://mi.feishu.cn/wiki/test123");

        Result<SubmitReviewResp> result = complaintOperateProvider.submitReview(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("登录"));
        verify(complaintOperateService, never()).submitReview(any());
    }

    /**
     * 测试提交复盘 - 系统异常
     */
    @Test
    void testSubmitReview_RuntimeException_ReturnsFail() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");

        SubmitReviewReq req = new SubmitReviewReq();
        req.setComplaintNo("TS256851079776454");
        req.setReviewMaterial("https://mi.feishu.cn/wiki/test123");

        when(complaintOperateService.submitReview(any(SubmitReviewSoIn.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        Result<SubmitReviewResp> result = complaintOperateProvider.submitReview(req);

        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMessage().contains("内部异常"));
    }


}
