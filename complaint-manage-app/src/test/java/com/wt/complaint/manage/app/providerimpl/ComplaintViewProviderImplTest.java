package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.SimpleComplaintDetailReq;
import com.wt.complaint.manage.api.model.resp.SimpleComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.SimpleComplaintDetailV2Resp;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.SimpleComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.SimpleComplaintDetailSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ComplaintViewProviderImpl 单元测试�?
 * 测试 getSimpleComplaintDetailV2 方法
 * 
 * @author 测试团队
 * @since 2026-02-02
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintViewProviderImplTest {

    @InjectMocks
    private ComplaintViewProviderImpl complaintViewProvider;

    @Mock
    private ComplaintViewService complaintViewService;

    private MockedStatic<RpcContext> mockedRpcContext;

    @BeforeEach
    void setUp() {
        // Mock RpcContext 静态方�?
        mockedRpcContext = mockStatic(RpcContext.class);
    }

    @AfterEach
    void tearDown() {
        // 关闭静�?mock
        if (mockedRpcContext != null) {
            mockedRpcContext.close();
        }
    }

    @Test
    void getSimpleComplaintDetailV2_成功获取投诉单详情_使用请求中的mid() {
        // 准备测试数据
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("UC202602020001");
        request.setMid(123456L);

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("999999");

        // Mock Service 返回结果
        SimpleComplaintDetailSoOut soOut = SimpleComplaintDetailSoOut.builder()
                .complaintInfo(new SimpleComplaintDetailSoOut.ComplaintInfoGoOut())
                .carInfo(new SimpleComplaintDetailSoOut.CarInfoSoOut())
                .build();
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenReturn(soOut);

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertEquals(0, result.getCode(), "调用应该成功");
        Assertions.assertNotNull(result.getData(), "返回数据不应为空");

        // 验证方法调用
        ArgumentCaptor<SimpleComplaintDetailSoIn> soInCaptor = ArgumentCaptor.forClass(SimpleComplaintDetailSoIn.class);
        verify(complaintViewService).getSimpleComplaintDetail(soInCaptor.capture());

        SimpleComplaintDetailSoIn capturedSoIn = soInCaptor.getValue();
        Assertions.assertEquals("UC202602020001", capturedSoIn.getComplaintNo(), "投诉单号应该匹配");
        Assertions.assertEquals("123456", capturedSoIn.getMidStr(), "MID应该使用请求中的�?);
    }

    @Test
    void getSimpleComplaintDetailV2_成功获取投诉单详情_使用RpcContext中的mid() {
        // 准备测试数据 - 不设�?mid
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("UC202602020002");

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("888888");

        // Mock Service 返回结果
        SimpleComplaintDetailSoOut soOut = SimpleComplaintDetailSoOut.builder()
                .complaintInfo(new SimpleComplaintDetailSoOut.ComplaintInfoGoOut())
                .carInfo(new SimpleComplaintDetailSoOut.CarInfoSoOut())
                .build();
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenReturn(soOut);

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertEquals(0, result.getCode(), "调用应该成功");

        // 验证方法调用
        ArgumentCaptor<SimpleComplaintDetailSoIn> soInCaptor = ArgumentCaptor.forClass(SimpleComplaintDetailSoIn.class);
        verify(complaintViewService).getSimpleComplaintDetail(soInCaptor.capture());

        SimpleComplaintDetailSoIn capturedSoIn = soInCaptor.getValue();
        Assertions.assertEquals("UC202602020002", capturedSoIn.getComplaintNo(), "投诉单号应该匹配");
        Assertions.assertEquals("888888", capturedSoIn.getMidStr(), "MID应该使用RpcContext中的�?);
    }

    @Test
    void getSimpleComplaintDetailV2_业务异常处理() {
        // 准备测试数据
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("UC202602020003");
        request.setMid(123456L);

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("999999");

        // Mock Service 抛出业务异常
        BusinessException businessException = new BusinessException(
                ErrorCodeEnums.BUS_ERROR.getErrorCode(),
                "投诉单不存在"
        );
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenThrow(businessException);

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertNotEquals(0, result.getCode(), "调用应该失败");
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode().getCode(), result.getCode(), "错误码应该匹�?);
        Assertions.assertEquals("投诉单不存在", result.getMessage(), "错误信息应该匹配");
    }

    @Test
    void getSimpleComplaintDetailV2_系统异常处理() {
        // 准备测试数据
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("UC202602020004");
        request.setMid(123456L);

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("999999");

        // Mock Service 抛出系统异常
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenThrow(new RuntimeException("数据库连接失�?));

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertNotEquals(0, result.getCode(), "调用应该失败");
        Assertions.assertEquals(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode().getCode(), result.getCode(),
                "错误码应该是内部错误");
        Assertions.assertEquals("内部异常", result.getMessage(), "错误信息应该是内部异�?);
    }

    @Test
    void getSimpleComplaintDetailV2_验证完整的车辆和投诉信息() {
        // 准备测试数据
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("UC202602020005");
        request.setMid(123456L);

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("999999");

        // Mock Service 返回结果
        SimpleComplaintDetailSoOut soOut = SimpleComplaintDetailSoOut.builder()
                .complaintInfo(new SimpleComplaintDetailSoOut.ComplaintInfoGoOut())
                .carInfo(new SimpleComplaintDetailSoOut.CarInfoSoOut())
                .build();
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenReturn(soOut);

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertEquals(0, result.getCode(), "调用应该成功");
        Assertions.assertNotNull(result.getData(), "返回数据不应为空");

        // 验证车辆信息
        SimpleComplaintDetailV2Resp.CarInfo resultCarInfo = result.getData().getCarInfo();
        Assertions.assertNotNull(resultCarInfo, "车辆信息不应为空");

        // 验证投诉信息
        SimpleComplaintDetailResp.ComplaintInfo resultComplaintInfo = result.getData().getComplaintInfo();
        Assertions.assertNotNull(resultComplaintInfo, "投诉信息不应为空");
    }

    @Test
    void getSimpleComplaintDetailV2_空投诉单号处�?) {
        // 准备测试数据 - 投诉单号为空
        SimpleComplaintDetailReq request = new SimpleComplaintDetailReq();
        request.setComplaintNo("");
        request.setMid(123456L);

        // Mock RpcContext
        RpcContext rpcContext = mock(RpcContext.class);
        mockedRpcContext.when(RpcContext::getContext).thenReturn(rpcContext);
        when(rpcContext.getAttachment("$upc_miID")).thenReturn("999999");

        // Mock Service 抛出业务异常
        BusinessException businessException = new BusinessException(
                ErrorCodeEnums.BUS_ERROR.getErrorCode(),
                "投诉单号不能为空"
        );
        when(complaintViewService.getSimpleComplaintDetail(any(SimpleComplaintDetailSoIn.class)))
                .thenThrow(businessException);

        // 执行测试
        Result<SimpleComplaintDetailV2Resp> result = complaintViewProvider.getSimpleComplaintDetailV2(request);

        // 验证结果
        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertNotEquals(0, result.getCode(), "调用应该失败");
        Assertions.assertEquals("投诉单号不能为空", result.getMessage(), "错误信息应该匹配");
    }
}
