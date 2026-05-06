package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;
import com.wt.complaint.manage.api.model.req.consult.StatisticsItemReq;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultStatisticsItemResp;
import com.wt.complaint.manage.app.convert.ConsultConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultStatisticsSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultStatisticsSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.alibaba.dubbo.rpc.RpcContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConsultViewProviderImplTest {

    // 静态Mock：RpcContext
    private MockedStatic<RpcContext> rpcContextMockedStatic;

    // 模拟依赖
    @Mock
    private UserConsultViewService userConsultViewService;
    @Mock
    private ConsultConvert consultConvert;

    // 测试目标�?
    @InjectMocks
    private UserConsultViewProviderImpl userConsultViewProvider;

    // 测试常量
    private static final String TEST_MIID = "123456";
    private static final Long TEST_MID = 123456L;
    private static final String BUSINESS_ERROR_CODE = "B001";
    private static final String BUSINESS_ERROR_MSG = "业务异常";
    private static final String INTERNAL_ERROR_CODE = "SYS001";
    private static final String INTERNAL_ERROR_MSG = "内部异常";

    @BeforeEach
    void setUp() {
        // 初始化RpcContext静态Mock
        rpcContextMockedStatic = mockStatic(RpcContext.class);
        RpcContext rpcContext = mock(RpcContext.class);
        when(RpcContext.getContext()).thenReturn(rpcContext);
    }

    @AfterEach
    void tearDown() {
        // 关闭静态Mock
        if (rpcContextMockedStatic != null) {
            rpcContextMockedStatic.close();
        }
    }

    // ====================== 1. queryStatisticsItems 测试 ======================
    @Test
    void queryStatisticsItemsNormalOnlyMe0() {
        // 1. 准备数据
        StatisticsItemReq req = new StatisticsItemReq();
        req.setOnlyMe(0);
        ConsultStatisticsSoIn soIn = new ConsultStatisticsSoIn();
        ConsultStatisticsSoOut soOut = new ConsultStatisticsSoOut();
        ConsultStatisticsItemResp resp = new ConsultStatisticsItemResp();

        // 2. Mock依赖

        when(userConsultViewService.queryStatisticsItems(any(ConsultStatisticsSoIn.class))).thenReturn(soOut);


        // 3. 执行方法
        Result<ConsultStatisticsItemResp> result = userConsultViewProvider.queryStatisticsItems(req);
        assertTrue(result.getCode() == 0);


    }


    @Test
    void queryStatisticsItemsException() {
        // 1. 准备数据
        StatisticsItemReq req = new StatisticsItemReq();
        ConsultStatisticsSoIn soIn = new ConsultStatisticsSoIn();

        // 2. Mock依赖

        when(userConsultViewService.queryStatisticsItems(any(ConsultStatisticsSoIn.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR));

        // 3. 执行方法
        Result<ConsultStatisticsItemResp> result = userConsultViewProvider.queryStatisticsItems(req);

        // 4. 断言
        assertTrue(result.getCode()!= 0);
    }

    @Test
    void queryStatisticsItemsSystemException() {
        // 1. 准备数据
        StatisticsItemReq req = new StatisticsItemReq();
        ConsultStatisticsSoIn soIn = new ConsultStatisticsSoIn();

        // 2. Mock依赖

        when(userConsultViewService.queryStatisticsItems(any(ConsultStatisticsSoIn.class)))
                .thenThrow(new RuntimeException("系统异常"));

        // 3. 执行方法
        Result<ConsultStatisticsItemResp> result = userConsultViewProvider.queryStatisticsItems(req);

        // 4. 断言
        assertTrue(result.getCode()!= 0);
    }

    // ====================== 2. padList 测试 ======================
    @Test
    void padListNormalOnlyMe0() {
        // 1. 准备数据
        PadConsultListReq req = new PadConsultListReq();
        req.setOnlyMe(0);
        ConsultListSoOut soOut = new ConsultListSoOut();
        ConsultListResp resp = new ConsultListResp();

        // 2. Mock依赖
        when(userConsultViewService.queryPadConsultList(any(PadConsultListReq.class))).thenReturn(soOut);


        // 3. 执行方法
        Result<ConsultListResp> result = userConsultViewProvider.padList(req);

        // 4. 断言
        assertTrue(result.getCode() == 0);
    }


    @Test
    void padListException() {
        // 1. 准备数据
        PadConsultListReq req = new PadConsultListReq();
        when(userConsultViewService.queryPadConsultList(any(PadConsultListReq.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR));

        // 2. 执行方法
        Result<ConsultListResp> result = userConsultViewProvider.padList(req);

        // 3. 断言
        assertTrue(result.getCode() != 0);
    }

    // ====================== 3. webList 测试 ======================
    @Test
    void webListNormal() {
        // 1. 准备数据
        ConsultListReq req = new ConsultListReq();
        ConsultListSoIn soIn = new ConsultListSoIn();
        ConsultListSoOut soOut = new ConsultListSoOut();
        ConsultListResp resp = new ConsultListResp();

        // 2. Mock依赖

        when(userConsultViewService.queryWebConsultList(any(ConsultListSoIn.class))).thenReturn(soOut);


        // 3. 执行方法
        Result<ConsultListResp> result = userConsultViewProvider.webList(req);

        // 4. 断言
        assertTrue(result.getCode() == 0);
    }

    @Test
    void webListException() {
        // 1. 准备数据
        ConsultListReq req = new ConsultListReq();
        ConsultListSoIn soIn = new ConsultListSoIn();

        when(userConsultViewService.queryWebConsultList(any(ConsultListSoIn.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR));

        // 2. 执行方法
        Result<ConsultListResp> result = userConsultViewProvider.webList(req);

        // 3. 断言
        assertTrue(result.getCode() != 0);
    }

    // ====================== 4. getConsultHandlerList 测试 ======================
    @Test
    void getConsultHandlerListNormal() {
        // 1. 准备数据
        ConsultHandlerListReq req = new ConsultHandlerListReq();
        ConsultHandlerListResp resp = new ConsultHandlerListResp();

        // 2. Mock依赖
        when(userConsultViewService.getConsultHandler(any(ConsultHandlerListReq.class))).thenReturn(resp);

        // 3. 执行方法
        Result<ConsultHandlerListResp> result = userConsultViewProvider.getConsultHandlerList(req);

        // 4. 断言
        assertTrue(result.getCode() == 0);
    }

    @Test
    void getConsultHandlerListException() {
        // 1. 准备数据
        ConsultHandlerListReq req = new ConsultHandlerListReq();
        when(userConsultViewService.getConsultHandler(any(ConsultHandlerListReq.class)))
                .thenThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR));

        // 2. 执行方法
        Result<ConsultHandlerListResp> result = userConsultViewProvider.getConsultHandlerList(req);

        // 3. 断言
        assertTrue(result.getCode() != 0);
    }


}