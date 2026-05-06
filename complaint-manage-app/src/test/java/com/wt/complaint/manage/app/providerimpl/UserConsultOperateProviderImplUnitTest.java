package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.consult.ConsultOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultReassignReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.UpdateHandlerReq;
import com.wt.complaint.manage.api.model.resp.operate.ChangeOrgResp;
import com.wt.complaint.manage.api.model.resp.operate.PickUpOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.UpdateHandlerResp;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultOrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultOrgChangeApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultReassignSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultUpdateHandlerSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultOrderPickUpSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultOrgChangeApplySoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultReassignSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultUpdateHandlerSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConsultOperateProviderImplUnitTest {
    @InjectMocks
    private UserConsultOperateProviderImpl userConsultOperateProvider;

    // 模拟依赖组件
    @Mock
    private UserConsultOperateService userConsultOperateService;

    @Mock
    private FileRemoteGateway fileRemoteGateway;

    // ========== 通用测试前置配置 ==========
    @BeforeEach
    void setUp() {
        // 重置Mock状态，避免用例间干�?
        reset(userConsultOperateService, fileRemoteGateway);
        // 清空RpcContext附件（模拟Dubbo上下文）
        RpcContext.getContext().clearAttachments();
    }

    @Test
    void pickUpOrderNormal() {
        // 1. 准备测试数据
        PickUpOrderReq req = new PickUpOrderReq();
        req.setConsultNo("CONSULT_001");
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        ConsultOrderPickUpSoOut soOut = new ConsultOrderPickUpSoOut();
        soOut.setResult("接单成功");

        // 2. Mock静态Redis工具类（加锁成功�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(true);

            // Mock业务服务
            when(userConsultOperateService.pickUpOrder(any(ConsultOrderPickUpSoIn.class))).thenReturn(soOut);

            // 3. 执行测试方法
            Result<PickUpOrderResp> result = userConsultOperateProvider.pickUpOrder(req);

            // 4. 断言结果
            assertTrue(result.getCode() == 0);
            // 验证解锁方法被调�?
            redisMock.verify(() -> RedisUtil.unlock("ZX:pickUpOrder:CONSULT_001"), times(1));
        }
    }

    @Test
    void pickUpOrderException() {
        // 1. 准备测试数据
        PickUpOrderReq req = new PickUpOrderReq();
        req.setConsultNo("CONSULT_001");

        // 2. Mock静态Redis工具类（加锁失败�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(false);

            // 3. 执行测试方法
            Result<PickUpOrderResp> result = userConsultOperateProvider.pickUpOrder(req);
            assertEquals("有其他操作正在进行中，请稍后再试", result.getMessage());
            // 验证解锁方法仍被调用（finally块）
            redisMock.verify(() -> RedisUtil.unlock("ZX:pickUpOrder:CONSULT_001"), times(1));
        }
    }


    @Test
    void reassignNormal() {
        // 1. 准备测试数据
        ConsultReassignReq req = new ConsultReassignReq();
        req.setConsultNo("CONSULT_001");
        req.setReassignOperatorMid(789012L); // 新跟进人ID
        // 设置RpcContext附件（模拟登录人信息�?
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        // 模拟服务返回结果
        ConsultReassignSoOut soOut = new ConsultReassignSoOut();
        soOut.setResult("改派跟进人成�?);

        // 2. Mock静态Redis工具类（加锁成功�? 业务服务
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis加锁成功
            redisMock.when(() -> RedisUtil.tryLock("ZX:reassign:CONSULT_001")).thenReturn(true);
            // Mock业务服务返回成功
            when(userConsultOperateService.reassign(any(ConsultReassignSoIn.class))).thenReturn(soOut);

            // 3. 执行测试方法
            Result<String> result = userConsultOperateProvider.reassign(req);

            // 4. 断言结果
            assertEquals("改派跟进人成�?, result.getData());
            // 验证Redis解锁必被调用（finally块）
            redisMock.verify(() -> RedisUtil.unlock("ZX:reassign:CONSULT_001"), times(1));
            // 验证业务方法被调用，且入参正确（登录人ID已填充）
            verify(userConsultOperateService, times(1)).reassign(argThat(soIn ->
                    soIn.getOperateMid().equals(123456L) && soIn.getReassignOperatorMid() == 789012L)
            );
        }
    }

    @Test
    void reassignException() {
        // 1. 准备测试数据
        ConsultReassignReq req = new ConsultReassignReq();
        req.setConsultNo("CONSULT_001");

        // 2. Mock静态Redis工具类（加锁失败�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis加锁失败
            redisMock.when(() -> RedisUtil.tryLock("ZX:reassign:CONSULT_001")).thenReturn(false);

            // 3. 执行测试方法
            Result<String> result = userConsultOperateProvider.reassign(req);

            // 4. 断言结果


            assertEquals("有其他操作正在进行中，请稍后再试", result.getMessage());
            // 验证解锁仍被调用（finally块）
            redisMock.verify(() -> RedisUtil.unlock("ZX:reassign:CONSULT_001"), times(1));
            // 验证业务方法未被调用（加锁失败直接抛异常�?
            verify(userConsultOperateService, never()).reassign(any(ConsultReassignSoIn.class));
        }
    }

/*    @Test
    void reassignNull() {
        // 1. 准备测试数据（不设置RpcContext附件，模拟无登录人）
        ConsultReassignReq req = new ConsultReassignReq();
        req.setConsultNo("CONSULT_001");

        // 2. Mock Redis加锁成功
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:reassign:CONSULT_001")).thenReturn(true);

            // 模拟业务服务（验证入参的operateMid为Null�?
            doAnswer(invocation -> {
                ConsultReassignSoIn soIn = invocation.getArgument(0);
                assertNull(soIn.getOperateMid()); // 无登录人时operateMid未赋�?
                return new ConsultReassignSoOut();
            }).when(userConsultOperateService).reassign(any(ConsultReassignSoIn.class));

            // 3. 执行测试方法
            Result<String> result = userConsultOperateProvider.reassign(req);

            // 4. 断言结果（业务服务正常调用，仅operateMid为Null�?

            redisMock.verify(() -> RedisUtil.unlock("ZX:reassign:CONSULT_001"), times(1));
        }
    }*/

    @Test
    void reassignFailed() {
        // 1. 准备测试数据
        ConsultReassignReq req = new ConsultReassignReq();
        req.setConsultNo("CONSULT_001");
        RpcContext.getContext().setAttachment("$upc_miID", "123456");
        // 模拟业务异常
        BusinessException bizEx = new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(),"改派失败：跟进人不存�?);

        // 2. Mock Redis加锁成功 + 业务服务抛异�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:reassign:CONSULT_001")).thenReturn(true);
            when(userConsultOperateService.reassign(any(ConsultReassignSoIn.class))).thenThrow(bizEx);

            // 3. 执行测试方法
            Result<String> result = userConsultOperateProvider.reassign(req);

            // 4. 断言结果
            assertEquals("改派失败：跟进人不存�?, result.getMessage());
            // 验证解锁被调�?
            redisMock.verify(() -> RedisUtil.unlock("ZX:reassign:CONSULT_001"), times(1));
        }
    }

    @Test
    void updateHandlerNormal() {
        // 1. 准备测试数据
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setConsultNo("CONSULT_001");
        req.setHandlerMid("987654"); // 新处理人ID
        // 设置RpcContext附件（模拟登录人信息�?
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        // 模拟服务返回结果
        ConsultUpdateHandlerSoOut soOut = new ConsultUpdateHandlerSoOut();
        soOut.setResult("success"); // 处理人更新成�?

        // 2. Mock静态Redis工具类（加锁成功�? 业务服务
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis加锁成功
            redisMock.when(() -> RedisUtil.tryLock("ZX:updateHandler:CONSULT_001")).thenReturn(true);
            // Mock业务服务返回成功
            when(userConsultOperateService.updateHandler(any(ConsultUpdateHandlerSoIn.class))).thenReturn(soOut);

            // 3. 执行测试方法
            Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);

            // 4. 断言结果

            assertEquals("success", result.getData().getResult()); // 验证返回的更新结�?
            // 验证Redis解锁必被调用（finally块）
            redisMock.verify(() -> RedisUtil.unlock("ZX:updateHandler:CONSULT_001"), times(1));

        }
    }

    @Test
    void updateHandlerException() {
        // 1. 准备测试数据
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setConsultNo("CONSULT_001");

        // 2. Mock静态Redis工具类（加锁失败�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis加锁失败
            redisMock.when(() -> RedisUtil.tryLock("ZX:updateHandler:CONSULT_001")).thenReturn(false);

            // 3. 执行测试方法
            Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);

            // 4. 断言结果


            assertEquals("有其他操作正在进行中，请稍后再试", result.getMessage());
            // 验证解锁仍被调用（finally块）
            redisMock.verify(() -> RedisUtil.unlock("ZX:updateHandler:CONSULT_001"), times(1));
            // 验证业务方法未被调用（加锁失败直接抛异常�?
            verify(userConsultOperateService, never()).updateHandler(any(ConsultUpdateHandlerSoIn.class));
        }
    }

/*    @Test
    void updateHandlerNull() {
        // 1. 准备测试数据（不设置RpcContext附件，模拟无登录人）
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setConsultNo("CONSULT_001");
        req.setHandlerMid("987654");

        // 2. Mock Redis加锁成功
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:updateHandler:CONSULT_001")).thenReturn(true);

            // 模拟业务服务（验证入参的operateMid为Null�?
            doAnswer(invocation -> {
                ConsultUpdateHandlerSoIn soIn = invocation.getArgument(0);
                assertNull(soIn.getOperateMid()); // 无登录人时operateMid未赋�?
                ConsultUpdateHandlerSoOut soOut = new ConsultUpdateHandlerSoOut();
                soOut.setResult("success");
                return soOut;
            }).when(userConsultOperateService).updateHandler(any(ConsultUpdateHandlerSoIn.class));

            // 3. 执行测试方法
            Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);

            // 4. 断言结果（业务服务正常调用，仅operateMid为Null�?

            assertEquals("success", result.getData().getResult());
            redisMock.verify(() -> RedisUtil.unlock("ZX:updateHandler:CONSULT_001"), times(1));
        }
    }*/

    @Test
    void updateHandlerFailed() {
        // 1. 准备测试数据
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setConsultNo("CONSULT_001");
        RpcContext.getContext().setAttachment("$upc_miID", "123456");
        // 模拟业务异常
        BusinessException bizEx = new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "更新失败：处理人不存�?);

        // 2. Mock Redis加锁成功 + 业务服务抛异�?
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:updateHandler:CONSULT_001")).thenReturn(true);
            when(userConsultOperateService.updateHandler(any(ConsultUpdateHandlerSoIn.class))).thenThrow(bizEx);

            // 3. 执行测试方法
            Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);

            // 4. 断言结果


            assertEquals("更新失败：处理人不存�?, result.getMessage());
            // 验证解锁被调�?
            redisMock.verify(() -> RedisUtil.unlock("ZX:updateHandler:CONSULT_001"), times(1));
        }
    }

    @Test
    void updateHandlerError() {
        // 1. 准备测试数据
        UpdateHandlerReq req = new UpdateHandlerReq();
        req.setConsultNo("CONSULT_001");
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        // 2. Mock Redis加锁成功 + 业务服务抛通用异常
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:updateHandler:CONSULT_001")).thenReturn(true);
            when(userConsultOperateService.updateHandler(any(ConsultUpdateHandlerSoIn.class))).thenThrow(new RuntimeException("Redis连接超时"));

            // 3. 执行测试方法
            Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);

            // 4. 断言结果


            assertEquals("更新处理人失�?, result.getMessage());
            // 验证解锁被调�?
            redisMock.verify(() -> RedisUtil.unlock("ZX:updateHandler:CONSULT_001"), times(1));
        }
    }

    @Test
    void submitChangeOrgApply_success_when_lockAcquired_and_loginInfoExist() {
        // 1. Prepare test data
        ConsultOrgChangeApplyReq req = new ConsultOrgChangeApplyReq();
        req.setConsultNo("CONSULT_001");
        req.setDesOrgId("ORG_10086"); // Target org ID
        // Set RpcContext attachment (simulate login user info)
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        // Mock service return result
        ConsultOrgChangeApplySoOut soOut = new ConsultOrgChangeApplySoOut();
        ChangeOrgResp orgApplyResp = new ChangeOrgResp();
        orgApplyResp.setResult("success");
        soOut.setResult("success");


        // 2. Mock static RedisUtil (lock success) + business service
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis lock success
            redisMock.when(() -> RedisUtil.tryLock("ZX:submitChangeOrgApply:CONSULT_001")).thenReturn(true);
            // Mock business service return success
            when(userConsultOperateService.submitChangeOrgApply(any(ConsultOrgChangeApplySoIn.class))).thenReturn(soOut);

            // 3. Execute test method
            Result<ChangeOrgResp> result = userConsultOperateProvider.submitChangeOrgApply(req);

            // 4. Assert result


            assertEquals("success", result.getData().getResult());
            // Verify Redis unlock is called (finally block)
            redisMock.verify(() -> RedisUtil.unlock("ZX:submitChangeOrgApply:CONSULT_001"), times(1));

        }
    }

    @Test
    void submitChangeOrgApply_fail_when_lockAcquireFailed() {
        // 1. Prepare test data
        ConsultOrgChangeApplyReq req = new ConsultOrgChangeApplyReq();
        req.setConsultNo("CONSULT_001");

        // 2. Mock static RedisUtil (lock failed)
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            // Mock Redis lock failed
            redisMock.when(() -> RedisUtil.tryLock("ZX:submitChangeOrgApply:CONSULT_001")).thenReturn(false);

            // 3. Execute test method
            Result<ChangeOrgResp> result = userConsultOperateProvider.submitChangeOrgApply(req);

            // 4. Assert result


            assertEquals("有其他操作正在进行中，请稍后再试", result.getMessage());
            // Verify unlock is still called (finally block)
            redisMock.verify(() -> RedisUtil.unlock("ZX:submitChangeOrgApply:CONSULT_001"), times(1));
            // Verify business method is not called (lock failed throw exception directly)
            verify(userConsultOperateService, never()).submitChangeOrgApply(any(ConsultOrgChangeApplySoIn.class));
        }
    }


    @Test
    void submitChangeOrgApply_fail_when_systemExceptionThrown() {
        // 1. Prepare test data
        ConsultOrgChangeApplyReq req = new ConsultOrgChangeApplyReq();
        req.setConsultNo("CONSULT_001");
        RpcContext.getContext().setAttachment("$upc_miID", "123456");

        // 2. Mock Redis lock success + business service throw generic exception
        try (MockedStatic<RedisUtil> redisMock = mockStatic(RedisUtil.class)) {
            redisMock.when(() -> RedisUtil.tryLock("ZX:submitChangeOrgApply:CONSULT_001")).thenReturn(true);
            when(userConsultOperateService.submitChangeOrgApply(any(ConsultOrgChangeApplySoIn.class))).thenThrow(new RuntimeException("Database query timeout"));

            // 3. Execute test method
            Result<ChangeOrgResp> result = userConsultOperateProvider.submitChangeOrgApply(req);

            // 4. Assert result


            assertEquals("申请改派门店失败", result.getMessage());
            // Verify unlock is called
            redisMock.verify(() -> RedisUtil.unlock("ZX:submitChangeOrgApply:CONSULT_001"), times(1));
        }
    }
}
