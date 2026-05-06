package com.wt.complaint.manage.app;

import com.wt.complaint.manage.api.model.req.retail.GetBubbleCountReq;
import com.wt.complaint.manage.api.model.resp.retail.BubbleCountResp;
import com.wt.complaint.manage.app.providerimpl.RetailComplaintViewProviderImpl;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.BubbleCountSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetailComplaintViewProviderImplUnitTest {

    @InjectMocks
    private RetailComplaintViewProviderImpl provider;

    @Mock
    private RetailComplaintViewService retailComplaintViewService;

    @BeforeEach
    void setUp() {
        // 清除RpcContext中的attachments，避免测试间相互影响
        RpcContext.removeContext();
    }

    @Test
    void getBubbleCountV2ShouldReturnSuccessWithOrgCode() {
        // 设置RpcContext中的miID
        String miID = "123456";
        RpcContext.getContext().setAttachment("$upc_miID", miID);

        // 准备请求参数
        String orgCode = "org123";
        GetBubbleCountReq req = GetBubbleCountReq.builder()
                .orgCode(orgCode)
                .build();

        // 模拟服务层返�?
        BubbleCountSoOut mockSoOut = BubbleCountSoOut.builder()
                .firstResponsePendingCount(10)
                .remindCount(5)
                .build();
        when(retailComplaintViewService.getBubbleCountV2(miID, orgCode)).thenReturn(mockSoOut);

        // 执行测试
        Result<BubbleCountResp> result = provider.getBubbleCountV2(req);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(10, result.getData().getFirstResponsePendingCount());
        Assertions.assertEquals(5, result.getData().getRemindCount());
    }

    @Test
    void getBubbleCountV2ShouldReturnSuccessWithoutOrgCode() {
        // 设置RpcContext中的miID
        String miID = "123456";
        RpcContext.getContext().setAttachment("$upc_miID", miID);

        // 准备请求参数（orgCode为空�?
        GetBubbleCountReq req = GetBubbleCountReq.builder()
                .orgCode(null)
                .build();

        // 模拟服务层返�?
        BubbleCountSoOut mockSoOut = BubbleCountSoOut.builder()
                .firstResponsePendingCount(8)
                .remindCount(3)
                .build();
        when(retailComplaintViewService.getBubbleCountV2(miID, null)).thenReturn(mockSoOut);

        // 执行测试
        Result<BubbleCountResp> result = provider.getBubbleCountV2(req);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(8, result.getData().getFirstResponsePendingCount());
        Assertions.assertEquals(3, result.getData().getRemindCount());
    }

    @Test
    void getBubbleCountV2ShouldHandleBusinessException() {
        // 设置RpcContext中的miID
        String miID = "123456";
        RpcContext.getContext().setAttachment("$upc_miID", miID);

        // 准备请求参数
        GetBubbleCountReq req = GetBubbleCountReq.builder()
                .orgCode("org123")
                .build();

        // 模拟服务层抛出BusinessException
        doThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR, "业务异常"))
                .when(retailComplaintViewService).getBubbleCountV2(anyString(), anyString());

        // 执行测试并验证异�?
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            provider.getBubbleCountV2(req);
        });

        // 验证异常信息
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(ErrorCodeEnums.BUS_ERROR.getErrorCode(), exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("业务异常"));
    }

    @Test
    void getBubbleCountV2ShouldHandleRuntimeException() {
        // 设置RpcContext中的miID
        String miID = "123456";
        RpcContext.getContext().setAttachment("$upc_miID", miID);

        // 准备请求参数
        GetBubbleCountReq req = GetBubbleCountReq.builder()
                .orgCode("org123")
                .build();

        // 模拟服务层抛出RuntimeException
        doThrow(new RuntimeException("系统异常"))
                .when(retailComplaintViewService).getBubbleCountV2(anyString(), anyString());

        // 执行测试并验证异�?
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            provider.getBubbleCountV2(req);
        });

        // 验证异常信息
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("系统异常"));
    }
}