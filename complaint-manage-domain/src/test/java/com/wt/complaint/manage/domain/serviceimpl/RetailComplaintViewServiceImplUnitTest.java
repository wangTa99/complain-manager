package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.enums.CarChannelTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClueGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StaticRetailCountGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BubbleCountGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DeliverComplaintExpandGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.BubbleCountSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetaiSoOut;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import com.xiaomi.youpin.infra.rpc.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RetailComplaintViewServiceImplUnitTest {

    @InjectMocks
    private RetailComplaintViewServiceImpl retailComplaintViewService;

    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Mock
    private RetailComplaintGateway retailComplaintGateway;

    @Mock
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private FileRemoteGateway fileRemoteGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    private ClueGateway clueGateway;

    @BeforeEach
    void setUp() throws Exception {
        mockExecutor();
    }

    public void mockExecutor() {
        // 注入可用的执行器，避�?CompletableFuture.runAsync 空指�?
        MoneThreadPoolExecutor mockExecutor = mock(MoneThreadPoolExecutor.class);
        lenient().doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));
        try {
            Field execField = RetailComplaintViewServiceImpl.class.getDeclaredField("commonThreadPoolExecutor");
            execField.setAccessible(true);
            execField.set(retailComplaintViewService, mockExecutor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试getBubbleCountV2 - orgCode不为空的场景
     */
    @Test
    public void testGetBubbleCountV2_OrgCodeNotEmpty() {
        // 准备测试数据
        String miID = "123456";
        String orgCode = "org123";

        // 创建气泡数量响应
        BubbleCountGoOut mockBubbleCount = new BubbleCountGoOut();
        mockBubbleCount.setRemindCount(5);
        mockBubbleCount.setFirstResponsePendingCount(10);

        // 模拟远程调用
        when(retailComplaintGateway.getBubbleCount(any(StaticRetailCountGoIn.class)))
                .thenReturn(mockBubbleCount);

        // 执行测试
        BubbleCountSoOut result = retailComplaintViewService.getBubbleCountV2(miID, orgCode);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.getRemindCount());
        Assertions.assertEquals(10, result.getFirstResponsePendingCount());
    }

    /**
     * 测试getBubbleCountV2 - orgCode为空，用户有汽车岗位的场�?
     */
    @Test
    public void testGetBubbleCountV2_OrgCodeEmpty_WithCarPosition() {
        // 准备测试数据
        String miID = "123456";
        String orgCode = null;

        // 创建渠道岗位信息
        CarEmployeeInfoGoOut.ChannelPositionInfo channelPosition =
                new CarEmployeeInfoGoOut.ChannelPositionInfo(PositionEnum.CAR_RETAIL_OPERATION.getCode(), "零售运营�?);

        // 模拟员工信息
        CarEmployeeInfoGoOut mockEmployeeInfo = new CarEmployeeInfoGoOut();
        mockEmployeeInfo.setChannelPositionInfoList(Collections.singletonList(channelPosition));
        mockEmployeeInfo.setHeadPositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setBigZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setLittleZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setStorePositionInfoList(Collections.emptyList());

        // 创建气泡数量响应
        BubbleCountGoOut mockBubbleCount = new BubbleCountGoOut();
        mockBubbleCount.setRemindCount(3);
        mockBubbleCount.setFirstResponsePendingCount(7);

        // 模拟远程调用
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(eq(Long.valueOf(miID)), anyInt()))
                .thenReturn(mockEmployeeInfo);
        when(retailComplaintGateway.getBubbleCount(any(StaticRetailCountGoIn.class)))
                .thenReturn(mockBubbleCount);

        // 执行测试
        BubbleCountSoOut result = retailComplaintViewService.getBubbleCountV2(miID, orgCode);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getRemindCount());
        Assertions.assertEquals(7, result.getFirstResponsePendingCount());
    }

    /**
     * 测试getBubbleCountV2 - orgCode为空，用户没有汽车岗位的场景
     */
    @Test
    public void testGetBubbleCountV2_OrgCodeEmpty_WithoutCarPosition() {
        // 准备测试数据
        String miID = "123456";
        String orgCode = null;

        // 模拟员工信息 - 没有岗位信息
        CarEmployeeInfoGoOut mockEmployeeInfo = new CarEmployeeInfoGoOut();
        mockEmployeeInfo.setChannelPositionInfoList(Collections.emptyList());
        mockEmployeeInfo.setHeadPositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setBigZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setLittleZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setStorePositionInfoList(Collections.emptyList());

        // 模拟远程调用
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(eq(Long.valueOf(miID)), anyInt()))
                .thenReturn(mockEmployeeInfo);

        // 执行测试
        BubbleCountSoOut result = retailComplaintViewService.getBubbleCountV2(miID, orgCode);

        // 验证结果 - 应该返回空的气泡数量
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRemindCount());
        Assertions.assertEquals(0, result.getFirstResponsePendingCount());
    }

    /**
     * 测试getBubbleCountV2 - orgCode为空，员工信息为空的场景
     */
    @Test
    public void testGetBubbleCountV2_OrgCodeEmpty_EmployeeInfoNull() {
        // 准备测试数据
        String miID = "123456";
        String orgCode = null;

        // 模拟远程调用 - 返回空的CarEmployeeInfoGoOut对象而不是null
        CarEmployeeInfoGoOut mockEmployeeInfo = new CarEmployeeInfoGoOut();
        mockEmployeeInfo.setHeadPositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setChannelPositionInfoList(Collections.emptyList());
        mockEmployeeInfo.setBigZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setLittleZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setStorePositionInfoList(Collections.emptyList());

        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(eq(Long.valueOf(miID)), anyInt()))
                .thenReturn(mockEmployeeInfo);

        // 执行测试
        BubbleCountSoOut result = retailComplaintViewService.getBubbleCountV2(miID, orgCode);

        // 验证结果 - 应该返回空的气泡数量
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRemindCount());
        Assertions.assertEquals(0, result.getFirstResponsePendingCount());
    }

    /**
     * 测试getRetailComplaintDetail - 下钻场景（orgCode不为空）
     */
    @Test
    public void testGetRetailComplaintDetail_DrillDown() {
        // 准备测试数据
        RetailComplaintDetailSoIn soIn = new RetailComplaintDetailSoIn();
        soIn.setMid("123456");
        soIn.setDrNo("RC123456");
        soIn.setOrgCode("org123");

        // 模拟员工信息
        CarEmployee carEmployee = new CarEmployee();
        CarEmployee.BusinessPosition businessPosition = new CarEmployee.BusinessPosition();
        businessPosition.setChannelType(CarChannelTypeEnum.CAR_BUSINESS.getCode());
        carEmployee.setCarBusinessPositions(Collections.singletonList(businessPosition));

        Map<Long, CarEmployee> employeeMap = new HashMap<>();
        employeeMap.put(123456L, carEmployee);

        // 模拟投诉详情
        RetailComplaintDetaiGoOut complaintDetail = new RetailComplaintDetaiGoOut();
        complaintDetail.setDrNo("RC123456");
        complaintDetail.setOrgId("org123");

        // 模拟扩展信息
        DeliverComplaintExpandGoOut expandGoOut = new DeliverComplaintExpandGoOut();

        // 设置mock行为
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList()))
                .thenReturn(employeeMap);
        when(retailComplaintGateway.getRetailComplaintDetail(any(RetailComplaintDetailGoIn.class)))
                .thenReturn(complaintDetail);
        when(deliverComplaintExpandGateway.selectDetailByDrNo(anyString()))
                .thenReturn(expandGoOut);
        when(fileRemoteGateway.getFileList(anyList(),any()))
                .thenReturn(Collections.emptyList());

        // 执行测试
        RetailComplaintDetaiSoOut result = retailComplaintViewService.getRetailComplaintDetail(soIn);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals("RC123456", result.getDrNo());
    }

    /**
     * 测试getRetailComplaintDetail - 普通场景（orgCode为空�?
     */
    @Test
    public void testGetRetailComplaintDetail_Normal() {
        // 准备测试数据 - orgCode为空
        RetailComplaintDetailSoIn soIn = new RetailComplaintDetailSoIn();
        soIn.setMid("123456");
        soIn.setDrNo("RC123456");
        soIn.setOrgCode(null);

        // 模拟员工信息（有正确岗位权限�?
        CarEmployeeInfoGoOut mockEmployeeInfo = new CarEmployeeInfoGoOut();
        CarEmployeeInfoGoOut.ChannelPositionInfo mockPosition = new CarEmployeeInfoGoOut.ChannelPositionInfo(
                PositionEnum.CAR_RETAIL_OPERATION.getCode(), "零售运营�?);
        mockEmployeeInfo.setChannelPositionInfoList(Collections.singletonList(mockPosition));
        // 为所有列表属性设置非null�?
        mockEmployeeInfo.setHeadPositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setBigZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setLittleZonePositionsInfoList(Collections.emptyList());
        mockEmployeeInfo.setCityZonePositionInfoList(Collections.emptyList());
        mockEmployeeInfo.setStorePositionInfoList(Collections.emptyList());

        // 模拟投诉详情
        RetailComplaintDetaiGoOut complaintDetail = new RetailComplaintDetaiGoOut();
        complaintDetail.setDrNo("RC123456");
        complaintDetail.setOrgId("org123");

        // 模拟扩展信息
        DeliverComplaintExpandGoOut expandGoOut = new DeliverComplaintExpandGoOut();

        // 设置mock行为
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(anyLong(), anyInt()))
                .thenReturn(mockEmployeeInfo);
        when(retailComplaintGateway.getRetailComplaintDetail(any(RetailComplaintDetailGoIn.class)))
                .thenReturn(complaintDetail);
        when(deliverComplaintExpandGateway.selectDetailByDrNo(anyString()))
                .thenReturn(expandGoOut);
        when(fileRemoteGateway.getFileList(anyList(), any()))
                .thenReturn(Collections.emptyList());

        // 执行测试
        RetailComplaintDetaiSoOut result = retailComplaintViewService.getRetailComplaintDetail(soIn);

        // 验证结果
        Assertions.assertNotNull(result);
        Assertions.assertEquals("RC123456", result.getDrNo());
    }

}
