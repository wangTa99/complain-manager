package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.api.model.enums.RetailTabEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StaticRetailCountGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StaticTabCountGoOut;
import com.wt.complaint.manage.infrastructure.mapper.RetailComplaintMapper;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * RetailComplaintGatewayImpl 单元测试�?
 *
 * @author p-wangkai95
 */
@ExtendWith(MockitoExtension.class)
public class RetailComplaintGatewayImplUnitTest {

    // 注入被测试对�?
    @InjectMocks
    private RetailComplaintGatewayImpl retailComplaintGatewayImpl;

    // Mock 依赖服务
    @Mock
    private RetailComplaintMapper retailComplaintMapper;

    @BeforeEach
    void setUp() throws Exception {
        // 创建并配�?MoneThreadPoolExecutor 实例
        MoneThreadPoolExecutor mockExecutor = mock(MoneThreadPoolExecutor.class);

        // 模拟 execute 方法，直接在当前线程执行任务
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));

        // 使用反射注入线程�?
        Field executorField = RetailComplaintGatewayImpl.class.getDeclaredField("retailComplaintOrderListExecutor");
        executorField.setAccessible(true);
        executorField.set(retailComplaintGatewayImpl, mockExecutor);
    }

    /**
     * 测试 staticTabCount 方法正常情况
     * 验证�?
     * 1. 各个 tab 的数据被正确查询
     * 2. 返回的结果按 tab 升序排序
     * 3. 数据数量正确
     */
    @Test
    void testStaticTabCountNormal() {
        // 准备测试数据
        int firstResponsePendingCount = 10;
        int inProgressCount = 20;
        int approachingTimeoutCount = 5;
        int finishCompleteCount = 30;

        // Mock 各个 tab 的查询结果，增加null检�?
        when(retailComplaintMapper.staticRetailCount(argThat(goIn ->
                goIn != null && Objects.equals(goIn.getTab(), RetailTabEnum.FIRST_RESPONSE_PENDING.getCode()))))
                .thenReturn(firstResponsePendingCount);

        when(retailComplaintMapper.staticRetailCount(argThat(goIn ->
                goIn != null && Objects.equals(goIn.getTab(), RetailTabEnum.IN_PROGRESS.getCode()))))
                .thenReturn(inProgressCount);

        when(retailComplaintMapper.staticRetailCount(argThat(goIn ->
                goIn != null && Objects.equals(goIn.getTab(), RetailTabEnum.APPROACHING_TIMEOUT.getCode()))))
                .thenReturn(approachingTimeoutCount);

        when(retailComplaintMapper.staticRetailCount(argThat(goIn ->
                goIn != null && Objects.equals(goIn.getTab(), RetailTabEnum.FINISH_COMPLETE.getCode()))))
                .thenReturn(finishCompleteCount);

        // 准备请求参数
        StaticRetailCountGoIn request = StaticRetailCountGoIn.builder()
                .zoneId("1")
                .littleZoneId("2")
                .orgId("3")
                .afterSaleWorkbenchPermissionGroup(new StaticRetailCountGoIn.AfterSaleWorkbenchPermissionGroup())
                .build();

        // 调用被测试方�?
        StaticTabCountGoOut result = retailComplaintGatewayImpl.staticTabCount(request);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getTabDataList());
        assertEquals(4, result.getTabDataList().size());

        // 验证排序（应该按 tab 升序�?-待首�? 2-处理�? 3-即将超时, 4-已结案）
        assertEquals(RetailTabEnum.FIRST_RESPONSE_PENDING.getCode(), result.getTabDataList().get(0).getTab());
        assertEquals(firstResponsePendingCount, result.getTabDataList().get(0).getCount());

        assertEquals(RetailTabEnum.IN_PROGRESS.getCode(), result.getTabDataList().get(1).getTab());
        assertEquals(inProgressCount, result.getTabDataList().get(1).getCount());

        assertEquals(RetailTabEnum.APPROACHING_TIMEOUT.getCode(), result.getTabDataList().get(2).getTab());
        assertEquals(approachingTimeoutCount, result.getTabDataList().get(2).getCount());

        assertEquals(RetailTabEnum.FINISH_COMPLETE.getCode(), result.getTabDataList().get(3).getTab());
        assertEquals(finishCompleteCount, result.getTabDataList().get(3).getCount());

        // 验证 mapper 方法被调用了 4 �?
        verify(retailComplaintMapper, times(4)).staticRetailCount(any(StaticRetailCountGoIn.class));
    }

    /**
     * 测试 staticTabCount 方法空数据情�?
     * 验证当所有查询都返回 0 时的行为
     */
    @Test
    void testStaticTabCountEmptyData() {
        // Mock 所有查询都返回 0
        when(retailComplaintMapper.staticRetailCount(any(StaticRetailCountGoIn.class))).thenReturn(0);

        // 准备请求参数
        StaticRetailCountGoIn request = StaticRetailCountGoIn.builder()
                .orgId("3")
                .build();

        // 调用被测试方�?
        StaticTabCountGoOut result = retailComplaintGatewayImpl.staticTabCount(request);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getTabDataList());
        assertEquals(4, result.getTabDataList().size());

        // 验证所�?count 都是 0
        for (StaticTabCountGoOut.TabData tabData : result.getTabDataList()) {
            assertEquals(0, tabData.getCount());
        }

        // 验证排序正确
        assertEquals(RetailTabEnum.FIRST_RESPONSE_PENDING.getCode(), result.getTabDataList().get(0).getTab());
        assertEquals(RetailTabEnum.IN_PROGRESS.getCode(), result.getTabDataList().get(1).getTab());
        assertEquals(RetailTabEnum.APPROACHING_TIMEOUT.getCode(), result.getTabDataList().get(2).getTab());
        assertEquals(RetailTabEnum.FINISH_COMPLETE.getCode(), result.getTabDataList().get(3).getTab());

        // 验证 mapper 方法被调用了 4 �?
        verify(retailComplaintMapper, times(4)).staticRetailCount(any(StaticRetailCountGoIn.class));
    }


    public ThreadPoolTaskExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveSecods,
                                                           int queueCapacity, String threadNamePrefix, RejectedExecutionHandler handler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveSecods);
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(handler);
        // 异步MDC
        // executor.setTaskDecorator(new MdcDecorator());
        executor.initialize();

        return executor;
    }
}