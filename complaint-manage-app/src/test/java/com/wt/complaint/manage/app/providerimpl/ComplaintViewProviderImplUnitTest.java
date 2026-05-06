package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.api.model.enums.TestTagEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.resp.*;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NrJobGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.UtilityRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.SimpleComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.strategy.process.FollowProcessStrategy;
import com.wt.complaint.manage.domain.strategy.process.FollowProcessFactory;
import com.xiaomi.nr.job.admin.dto.TriggerJobRequestDTO;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ComplaintViewProviderImpl 单元测试
 *
 * @author system
 * @date 2026/01/29
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintViewProviderImplUnitTest {

    @InjectMocks
    private ComplaintViewProviderImpl complaintViewProvider;

    @Mock
    private ComplaintViewService complaintViewService;

    @Mock
    private CustomeUserContext customeUserContext;

    @Mock
    private NrJobGateway nrJobGateway;

    @Mock
    private FollowProcessFactory userComplaintFollowProcessFactory;

    @Mock
    private UpcConfigLocalCache localCache;

    @Mock
    private UtilityRemoteGateway utilityRemoteGateway;

    @Mock
    private FollowProcessStrategy followProcessStrategy;

    @BeforeEach
    void setUp() {
        // 设置配置属�?
        ReflectionTestUtils.setField(complaintViewProvider, "jobProjectId", 123L);
        ReflectionTestUtils.setField(complaintViewProvider, "appname", "test-app");
    }

    @Test
    void getComplaintFrame_Success() {
        // 准备测试数据
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("C001");

        ComplaintFrameInfoSoOut soOut = new ComplaintFrameInfoSoOut();
        soOut.setComplaintNo("C001");

        // 模拟服务调用
        when(complaintViewService.getComplaintFrameInfo(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintDetailFrameResp> result = complaintViewProvider.getComplaintFrame(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getComplaintFrameInfo(any());
    }

    @Test
    void getComplaintFrame_BusinessException() {
        // 准备测试数据
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("C001");

        // 模拟业务异常
        BusinessException businessException = new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "测试异常");
        when(complaintViewService.getComplaintFrameInfo(any())).thenThrow(businessException);

        // 执行测试
        Result<ComplaintDetailFrameResp> result = complaintViewProvider.getComplaintFrame(req);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertEquals(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void getComplaintFrame_Exception() {
        // 准备测试数据
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("C001");

        // 模拟系统异常
        when(complaintViewService.getComplaintFrameInfo(any())).thenThrow(new RuntimeException("系统异常"));

        // 执行测试
        Result<ComplaintDetailFrameResp> result = complaintViewProvider.getComplaintFrame(req);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void getComplaintAuth_Success() {
        // 准备测试数据
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("C001");

        ComplaintFrameInfoSoOut soOut = new ComplaintFrameInfoSoOut();
        soOut.setComplaintNo("C001");

        // 模拟服务调用
        when(complaintViewService.getComplaintAuth(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintDetailFrameResp> result = complaintViewProvider.getComplaintAuth(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getComplaintAuth(any());
    }

    @Test
    void refreshCacheTtl_Success() {
        // 执行测试
        Result<String> result = complaintViewProvider.refreshCacheTtl();

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("缓存时间重置成功", result.getData());
        verify(localCache, times(1)).refreshCacheTtl();
        verify(utilityRemoteGateway, times(1)).refreshCacheTtl();
    }

    @Test
    void getComplaintDetail_Success() {
        // 准备测试数据
        ComplaintDetailReq req = new ComplaintDetailReq();
        req.setComplaintNo("C001");

        ComplaintDetailSoOut soOut = new ComplaintDetailSoOut();
        soOut.setComplaintNo("C001");

        // 模拟服务调用
        when(complaintViewService.getComplaintDetail(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintDetailResp> result = complaintViewProvider.getComplaintDetail(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getComplaintDetail(any());
    }

    @Test
    void batchGetComplaintDetail_Success() {
        // 准备测试数据
        ComplaintDetailBatchReq req = new ComplaintDetailBatchReq();
        req.setComplaintNoList(Arrays.asList("C001", "C002"));

        ComplaintBatchDetailSoOut soOut = new ComplaintBatchDetailSoOut();

        // 模拟服务调用
        when(complaintViewService.batchGetComplaintDetail(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintDetailBatchResp> result = complaintViewProvider.batchGetComplaintDetail(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).batchGetComplaintDetail(any());
    }

    @Test
    void getFollowUpRecords_WithUcNo_Success() {
        // 准备测试数据（TS 为投诉单前缀，对�?COMPLAINT_ORDER�?
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();
        req.setUcNo("TS001");

        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();

        // 模拟策略工厂和策略方法（getStrategy 参数�?Integer�?
        when(userComplaintFollowProcessFactory.getStrategy(anyInt())).thenReturn(followProcessStrategy);
        when(followProcessStrategy.getFollowUpRecords(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintFollowUpRecordsResp> result = complaintViewProvider.getFollowUpRecords(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(userComplaintFollowProcessFactory, times(1)).getStrategy(anyInt());
        verify(followProcessStrategy, times(1)).getFollowUpRecords(any());
    }

    @Test
    void getFollowUpRecords_WithComplaintNo_Success() {
        // 准备测试数据（TS 为投诉单前缀�?
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();
        req.setComplaintNo("TS001");

        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();

        // 模拟策略工厂和策略方法（getStrategy 参数�?Integer�?
        when(userComplaintFollowProcessFactory.getStrategy(anyInt())).thenReturn(followProcessStrategy);
        when(followProcessStrategy.getFollowUpRecords(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintFollowUpRecordsResp> result = complaintViewProvider.getFollowUpRecords(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
    }

    @Test
    void getFollowUpRecords_BothEmpty_Fail() {
        // 准备测试数据
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();

        // 执行测试
        Result<ComplaintFollowUpRecordsResp> result = complaintViewProvider.getFollowUpRecords(req);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void getFollowUpRecords_InvalidOrderType_Fail() {
        // 准备测试数据
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();
        req.setUcNo("INVALID001");

        // 执行测试
        Result<ComplaintFollowUpRecordsResp> result = complaintViewProvider.getFollowUpRecords(req);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode().getCode(), result.getCode());
    }

    @Test
    void getSimpleComplaintDetail_Success() {
        // 准备测试数据
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("C001");
        request.setMid(123L);

        SimpleComplaintDetailSoOut goOut = new SimpleComplaintDetailSoOut();
        SimpleComplaintDetailSoOut.ComplaintInfoGoOut complaintInfo = new SimpleComplaintDetailSoOut.ComplaintInfoGoOut();
        complaintInfo.setComplaintNo("C001");
        goOut.setComplaintInfo(complaintInfo);

        // 模拟服务调用
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class))).thenReturn(goOut);

        // 执行测试
        Result<SimpleComplaintDetailResp> result = complaintViewProvider.getSimpleComplaintDetail(request);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class));
    }

    @Test
    void searchComplaintList_AfterSaleWorkbench_Success() {
        // 准备测试数据
        ComplaintListSearchReq request = new ComplaintListSearchReq();
        request.setSource(SourceEnum.AFTER_SALE_WORKBENCH.getCode());

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(123L);
        userInfo.setRoleList("[\"programmer\"]");
        userInfo.setCurrRole("role1");

        ComplaintListSearchSoOut goOut = new ComplaintListSearchSoOut();

        // 模拟服务调用
        when(customeUserContext.fromRpcContextForAftersaleWorkbench()).thenReturn(userInfo);
        when(complaintViewService.searchComplaintList(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);

        // 执行测试
        Result<ComplaintListSearchResp> result = complaintViewProvider.searchComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).searchComplaintList(any(ComplaintListSearchGoIn.class));
    }

    @Test
    void searchComplaintList_OtherSource_Success() {
        // 准备 RpcContext（searchComplaintList 非售后工作台会走 UserInfo.fromRpcContext()�?
        RpcContext.getContext().setAttachment("$upc_miID", "123");
        RpcContext.getContext().setAttachment("$curr_role", "role1");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"user\"]");
        RpcContext.getContext().setAttachment("$upc_email", "test@xiaomi.com");
        RpcContext.getContext().setAttachment("_trace_id_", "trace123");

        ComplaintListSearchReq request = new ComplaintListSearchReq();
        request.setSource("other");

        ComplaintListSearchSoOut goOut = new ComplaintListSearchSoOut();

        when(complaintViewService.searchComplaintList(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);

        Result<ComplaintListSearchResp> result = complaintViewProvider.searchComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).searchComplaintList(any(ComplaintListSearchGoIn.class));
    }

    @Test
    void countComplaintListTab_Success() {
        RpcContext.getContext().setAttachment("$upc_miID", "123");
        RpcContext.getContext().setAttachment("$curr_role", "role1");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"user\"]");
        RpcContext.getContext().setAttachment("$upc_email", "test@xiaomi.com");
        RpcContext.getContext().setAttachment("_trace_id_", "trace123");

        ComplaintListSearchReq request = new ComplaintListSearchReq();

        CountComplaintListTabSoOut goOut = new CountComplaintListTabSoOut();

        when(complaintViewService.countComplaintListTab(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);

        Result<CountComplaintListTabResp> result = complaintViewProvider.countComplaintListTab(request);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).countComplaintListTab(any(ComplaintListSearchGoIn.class));
    }

    @Test
    void getComplaintHandlerList_Success() {
        // 准备测试数据
        ComplaintHandlerListReq req = new ComplaintHandlerListReq();
        req.setOrgId("org001");

        GetComplaintHandlerSoOut complaintHandler = new GetComplaintHandlerSoOut();

        // 模拟服务调用
        when(complaintViewService.getComplaintHandler(any())).thenReturn(complaintHandler);

        // 执行测试
        Result<ComplaintHandlerListResp> result = complaintViewProvider.getComplaintHandlerList(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getComplaintHandler(any());
    }

    @Test
    void getComplaintEditDetail_EmptyComplaintNo_Fail() {
        // 准备测试数据
        ComplaintDetailReq req = new ComplaintDetailReq();

        // 执行测试
        Result<ComplaintEditDetailResp> result = complaintViewProvider.getComplaintEditDetail(req);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    void getComplaintEditDetail_Success() {
        // 准备测试数据
        ComplaintDetailReq req = new ComplaintDetailReq();
        req.setComplaintNo("C001");

        ComplaintEditDetailSoOut soOut = new ComplaintEditDetailSoOut();

        // 模拟服务调用
        when(complaintViewService.getComplaintEditDetail(any())).thenReturn(soOut);

        // 执行测试
        Result<ComplaintEditDetailResp> result = complaintViewProvider.getComplaintEditDetail(req);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(complaintViewService, times(1)).getComplaintEditDetail(any());
    }

    @Test
    void exportComplaintList_EmptyTimeRange_Fail() {
        // 准备测试数据
        ComplaintListSearchReq request = new ComplaintListSearchReq();

        // 执行测试
        Result<ComplaintListExportRes> result = complaintViewProvider.exportComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    void exportComplaintList_NoData_Fail() {
        RpcContext.getContext().setAttachment("$upc_miID", "123");
        RpcContext.getContext().setAttachment("$curr_role", "role1");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"user\"]");
        RpcContext.getContext().setAttachment("$upc_email", "test@xiaomi.com");
        RpcContext.getContext().setAttachment("_trace_id_", "trace123");

        ComplaintListSearchReq request = new ComplaintListSearchReq();
        request.setCreateTimeStart("2024-01-01");
        request.setCreateTimeEnd("2024-01-02");

        ComplaintListSearchSoOut goOut = new ComplaintListSearchSoOut();

        when(complaintViewService.searchComplaintList(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);

        Result<ComplaintListExportRes> result = complaintViewProvider.exportComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    void exportComplaintList_ExceedMaxLimit_Fail() {
        RpcContext.getContext().setAttachment("$upc_miID", "123");
        RpcContext.getContext().setAttachment("$curr_role", "role1");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"user\"]");
        RpcContext.getContext().setAttachment("$upc_email", "test@xiaomi.com");
        RpcContext.getContext().setAttachment("_trace_id_", "trace123");

        ComplaintListSearchReq request = new ComplaintListSearchReq();
        request.setCreateTimeStart("2024-01-01");
        request.setCreateTimeEnd("2024-01-02");

        ComplaintListSearchSoOut goOut = new ComplaintListSearchSoOut();
        goOut.setTotal(100000); // 超过最大导出限�?

        when(complaintViewService.searchComplaintList(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);

        Result<ComplaintListExportRes> result = complaintViewProvider.exportComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    void exportComplaintList_Success() {
        RpcContext.getContext().setAttachment("$upc_miID", "123");
        RpcContext.getContext().setAttachment("$curr_role", "role1");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"user\"]");
        RpcContext.getContext().setAttachment("$upc_email", "test@xiaomi.com");
        RpcContext.getContext().setAttachment("_trace_id_", "trace123");

        ComplaintListSearchReq request = new ComplaintListSearchReq();
        request.setCreateTimeStart("2024-01-01");
        request.setCreateTimeEnd("2024-01-02");

        ComplaintListSearchSoOut goOut = new ComplaintListSearchSoOut();
        goOut.setTotal(100);
        goOut.setDataList(Collections.singletonList(new ComplaintListSearchInfo())); // 有数据才走导出任�?

        when(complaintViewService.searchComplaintList(any(ComplaintListSearchGoIn.class))).thenReturn(goOut);
        when(nrJobGateway.createExportTask(any(TriggerJobRequestDTO.class))).thenReturn("task123");

        Result<ComplaintListExportRes> result = complaintViewProvider.exportComplaintList(request);

        // 断言结果
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
        verify(nrJobGateway, times(1)).createExportTask(any(TriggerJobRequestDTO.class));
    }
}