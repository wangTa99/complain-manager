package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.xiaomi.newretail.bpm.api.model.dto.ProcessCurrentTaskResponseDTO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.utils.ComplaintApplyUtil;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintAuditService;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import org.springframework.context.ApplicationEventPublisher;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.AuditDetailForCustomerServiceSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintPreNextSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.JudgeResponsibilitySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.AuditDetailForCustomerServiceSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintPreNextSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.manager.ComplaintAuditManager;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ComplaintAuditServiceImpl单元测试
 * 测试审批服务核心业务逻辑
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintAuditServiceImplUnitTest {

    @InjectMocks
    private ComplaintAuditServiceImpl complaintAuditService;

    @Mock
    private ComplaintAuditGateway complaintAuditGateway;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Mock
    private CarRemoteGateway carRemoteGateway;

    @Mock
    private ComplaintGateway complaintGateway;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Mock
    private FileRemoteGateway fileRemoteGateway;

    @Mock
    private ComplaintAuditManager complaintAuditManager;

    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Mock
    private BPMRemoteGateway bpmRemoteGateway;

    @Mock
    private ComplaintAuditService auditServiceSelfRef;

    @Mock
    private MessageInformedEventFactory messageInformedEventFactory;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MoneThreadPoolExecutor constructMessageEventExecutorMock;

    @BeforeEach
    void setUp() throws Exception {
        constructMessageEventExecutorMock = mock(MoneThreadPoolExecutor.class);
        lenient().doAnswer(inv -> {
            inv.getArgument(0, Runnable.class).run();
            return null;
        }).when(constructMessageEventExecutorMock).execute(any(Runnable.class));
        Field executorField = ComplaintAuditServiceImpl.class.getDeclaredField("constructMessageEventExecutor");
        executorField.setAccessible(true);
        executorField.set(complaintAuditService, constructMessageEventExecutorMock);
    }

    // ============ searchComplaintAuditList 权限过滤测试 ============

    /**
     * 测试查询审批列表 - 满意度管理人员查看所有审批单
     */
    @Test
    void testSearchComplaintAuditList_SatisfactionManagement_Success() {
        // 准备数据
        Long mid = 1001L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        
        // Mock 员工信息 - 满意度管�?
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // Mock 审批列表查询
        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(1L);
        expectedResult.setDataList(Lists.newArrayList(
                TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode())
        ));
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);
        
        // Mock 其他依赖
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(new ArrayList<>());
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        
        // 验证审批类型包含产品风险-申请结案
        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
    }

    /**
     * 测试查询审批列表 - 区域体验专家按大区过�?
     */
    @Test
    void testSearchComplaintAuditList_RegionalExpert_FilterByZone() {
        // 准备数据
        Long mid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1, 2);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        
        // Mock 员工信息 - 区域体验专家
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // Mock 审批列表查询
        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证
        assertNotNull(result);
        
        // 验证大区过滤
        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertEquals(zoneIds, captor.getValue().getZoneIdList());
    }

    @Test
    void testSearchComplaintAuditList_RegionalExpert_FilterAuditTypes() {
        Long mid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        req.setAuditTypeList(Lists.newArrayList(
                AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode(),
                AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()
        ));

        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);

        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);

        complaintAuditService.searchComplaintAuditList(req);

        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
        assertFalse(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()));
    }

    /**
     * 测试查询审批列表 - 城市体验专家按小区过�?
     */
    @Test
    void testSearchComplaintAuditList_UrbanExpert_FilterByLittleZone() {
        // 准备数据
        Long mid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10, 20);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        
        // Mock 员工信息 - 城市体验专家
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // Mock 审批列表查询
        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证
        assertNotNull(result);
        
        // 验证小区过滤
        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertEquals(littleZoneIds, captor.getValue().getLittleZoneIdList());
    }

    @Test
    void testSearchComplaintAuditList_UrbanExpert_FilterAuditTypes() {
        Long mid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        req.setAuditTypeList(Lists.newArrayList(
                AuditTypeEnum.REASSIGNMENT_STORES.getCode(),
                AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()
        ));

        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);

        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);

        complaintAuditService.searchComplaintAuditList(req);

        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.REASSIGNMENT_STORES.getCode()));
        assertFalse(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()));
    }

    /**
     * 测试查询审批列表 - 区域运营管理查门店免责，设置 zoneIdList �?auditTypeList 并调用网�?
     */
    @Test
    void testSearchComplaintAuditList_RegionalOps_Waiver_SetsZoneAndAuditTypeAndCallsGateway() {
        Long mid = 1007L;
        List<Integer> zoneIds = Arrays.asList(1, 2);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(zoneIds));

        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);

        complaintAuditService.searchComplaintAuditList(req);

        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertEquals(zoneIds, captor.getValue().getZoneIdList());
        assertEquals(Collections.singletonList(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()), captor.getValue().getAuditTypeList());
    }

    /**
     * 测试查询审批列表 - 城市服务经理查门店免责，设置 littleZoneIdList �?auditTypeList 并调用网�?
     */
    @Test
    void testSearchComplaintAuditList_CityServiceManager_Waiver_SetsLittleZoneAndAuditTypeAndCallsGateway() {
        Long mid = 1008L;
        List<Integer> littleZoneIds = Arrays.asList(10, 20);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_CityServiceManager(littleZoneIds));

        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(0L);
        expectedResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);

        complaintAuditService.searchComplaintAuditList(req);

        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertEquals(littleZoneIds, captor.getValue().getLittleZoneIdList());
        assertEquals(Collections.singletonList(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()), captor.getValue().getAuditTypeList());
    }

    /**
     * 测试查询审批列表 - 产品风险-申请结案权限过滤（区域专家可见）
     */
    @Test
    void testSearchComplaintAuditList_ProductRiskClosure_PermissionFilter() {
        // 准备数据
        Long mid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        // 请求查询产品风险-申请结案类型
        req.setAuditTypeList(Lists.newArrayList(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
        
        // Mock 员工信息 - 区域体验专家（有权限�?
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // Mock 审批列表查询
        ComplaintAuditListSoOut expectedResult = new ComplaintAuditListSoOut();
        expectedResult.setTotal(1L);
        expectedResult.setDataList(Lists.newArrayList(
                TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode())
        ));
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(expectedResult);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(new ArrayList<>());
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证 - 区域专家可以查看产品风险-申请结案
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        
        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
    }

    /**
     * 测试查询审批列表 - 产品风险-申请结案权限过滤（城市专家不可见�?
     */
    @Test
    void testSearchComplaintAuditList_ProductRiskClosure_NoPermission() {
        // 准备数据
        Long mid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        // 只请求查询产品风�?申请结案类型
        req.setAuditTypeList(Lists.newArrayList(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
        
        // Mock 员工信息 - 城市体验专家（无权限�?
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证 - 城市专家没有权限，返回空列表
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getDataList().isEmpty());
        
        // 验证没有调用审批列表查询（因为权限不足提前返回）
        verify(complaintAuditGateway, never()).searchComplaintAuditList(any());
    }

    /**
     * 测试查询审批列表 - 客诉处理岗位无审批列表权限，返回空列�?
     */
    @Test
    void testSearchComplaintAuditList_ComplaintHandling_NoPermission() {
        Long mid = 1004L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_ComplaintHandling();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);

        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getDataList().isEmpty());
        verify(complaintAuditGateway, never()).searchComplaintAuditList(any());
    }

    /**
     * 测试查询审批列表 - 有权限但网关返回空列表时不进行数据填�?
     */
    @Test
    void testSearchComplaintAuditList_EmptyDataList_ReturnWithoutEnrich() {
        Long mid = 1004L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        ComplaintAuditListSoOut gatewayResult = new ComplaintAuditListSoOut();
        gatewayResult.setTotal(0L);
        gatewayResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(gatewayResult);

        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getDataList().isEmpty());
        // 数据为空时不应调�?findList 进行填充
        verify(complaintOrderRepositoryGateway, never()).findList(any());
    }

    /**
     * 测试查询审批列表 - 请求�?VIN 时设�?vid 并传给网�?
     */
    @Test
    void testSearchComplaintAuditList_WithVin_SetsVidInRequest() {
        Long mid = 1004L;
        String vin = "LTEST1234567890";
        String expectedVid = "v001";
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        req.setVin(vin);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());
        when(carRemoteGateway.getVidByVin(vin)).thenReturn(expectedVid);

        ComplaintAuditListSoOut gatewayResult = new ComplaintAuditListSoOut();
        gatewayResult.setTotal(0L);
        gatewayResult.setDataList(new ArrayList<>());
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(gatewayResult);

        complaintAuditService.searchComplaintAuditList(req);

        ArgumentCaptor<ComplaintAuditListSoIn> captor = ArgumentCaptor.forClass(ComplaintAuditListSoIn.class);
        verify(complaintAuditGateway).searchComplaintAuditList(captor.capture());
        assertEquals(expectedVid, captor.getValue().getVid());
    }

    /**
     * 测试查询审批列表 - 有数据时进行填充（风险等级、投诉类型、枚举名称等�?
     */
    @Test
    void testSearchComplaintAuditList_EnrichData_FillsRiskLevelAndEnumNames() {
        Long mid = 1004L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        ComplaintAuditSoOut auditItem = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditItem.setContactNameC(null);
        auditItem.setContactPhoneC(null);
        auditItem.setVid(null);
        ComplaintAuditListSoOut gatewayResult = new ComplaintAuditListSoOut();
        gatewayResult.setTotal(1L);
        gatewayResult.setDataList(Lists.newArrayList(auditItem));
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(gatewayResult);

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C001", 1);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        when(carRemoteGateway.getCarSimpleInfo(any(), any())).thenReturn(new ArrayList<>());

        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getDataList().size());
        ComplaintAuditSoOut enriched = result.getDataList().get(0);
        assertEquals(orderInfo.getRiskLevel(), enriched.getRiskLevel());
        assertEquals(orderInfo.getComplaintType(), enriched.getComplaintType());
        assertNotNull(enriched.getComplaintTypeName());
        assertNotNull(enriched.getAuditTypeName());
        assertNotNull(enriched.getAuditStatusName());
        assertNotNull(enriched.getCurrentApprovalNodeList());
        assertEquals(Collections.singletonList(PositionEnum.URBAN_EXPERIENCE_EXPERT.getName()),
                enriched.getCurrentApprovalNodeList());
    }

    /**
     * 测试查询审批列表 - 填充 currentApprovalNodeList（申请门店免责按 currentNode 映射�?
     */
    @Test
    void testSearchComplaintAuditList_EnrichData_CurrentApprovalNodeList_WaiverByNode() {
        Long mid = 1004L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        ComplaintAuditSoOut auditItem = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditItem.setCurrentNode(2);
        auditItem.setContactNameC(null);
        auditItem.setContactPhoneC(null);
        auditItem.setVid(null);
        ComplaintAuditListSoOut gatewayResult = new ComplaintAuditListSoOut();
        gatewayResult.setTotal(1L);
        gatewayResult.setDataList(Lists.newArrayList(auditItem));
        when(complaintAuditGateway.searchComplaintAuditList(any())).thenReturn(gatewayResult);

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C001", 1);
        when(complaintOrderRepositoryGateway.findList(any())).thenReturn(Lists.newArrayList(orderInfo));
        when(carRemoteGateway.getCarSimpleInfo(any(), any())).thenReturn(new ArrayList<>());

        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);

        assertNotNull(result);
        assertEquals(1, result.getDataList().size());
        assertEquals(Collections.singletonList(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getName()),
                result.getDataList().get(0).getCurrentApprovalNodeList());
    }

    /**
     * 测试查询审批列表 - 无权限返回空列表
     */
    @Test
    void testSearchComplaintAuditList_NoPermission_EmptyResult() {
        // 准备数据
        Long mid = 1005L;
        ComplaintAuditListSoIn req = new ComplaintAuditListSoIn();
        req.setMid(mid);
        req.setPageNum(1);
        req.setPageSize(10);
        
        // Mock 员工信息 - 无任何权�?
        CarEmployeeInfoGoOut employeeInfo = new CarEmployeeInfoGoOut();
        employeeInfo.setChannelPositionInfoList(new ArrayList<>());
        employeeInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setBigZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(employeeInfo);
        
        // 执行
        ComplaintAuditListSoOut result = complaintAuditService.searchComplaintAuditList(req);
        
        // 验证 - 无权限返回空列表
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getDataList().isEmpty());
        
        // 验证没有调用审批列表查询
        verify(complaintAuditGateway, never()).searchComplaintAuditList(any());
    }

    // ============ checkAuditPermission 权限校验测试 ============

    /**
     * 测试审批权限校验 - 结案申请校验当前处理�?
     */
    @Test
    void testCheckAuditPermission_ClosureApplication_ValidHandler() {
        // 准备数据
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");
        
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setCustomerServiceMid(auditMid); // 当前处理人与审批人一�?
        
        // Mock 员工信息（结案申请不检查岗位，只检查是否为当前处理人）
        CarEmployeeInfoGoOut employeeInfo = new CarEmployeeInfoGoOut();
        employeeInfo.setChannelPositionInfoList(new ArrayList<>());
        employeeInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setBigZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        
        // Mock 审批管理�?
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());
        
        // 执行 - 不应抛异�?
        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);
        
        // 验证
        assertTrue(result);
        verify(complaintAuditManager).approveAudit(any(), any(), any());
    }

    /**
     * 测试审批权限校验 - 结案申请非当前处理人抛异�?
     */
    @Test
    void testCheckAuditPermission_ClosureApplication_InvalidHandler() {
        // 准备数据
        Long auditMid = 1001L;
        Long otherMid = 1002L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");
        
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        orderGoOut.setCustomerServiceMid(otherMid); // 当前处理人与审批人不一�?
        
        // Mock
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        
        CarEmployeeInfoGoOut employeeInfo = new CarEmployeeInfoGoOut();
        employeeInfo.setChannelPositionInfoList(new ArrayList<>());
        employeeInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setBigZonePositionsInfoList(new ArrayList<>());
        employeeInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        
        // 执行并验�?- 应抛出无权限异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintAuditService.submitForApproval(req, auditSoOut, false);
        });
        
        assertTrue(exception.getMessage().contains("只有该工单的当前处理�?));
    }

    /**
     * 测试审批权限校验 - 城市专家不能审批免责申请
     */
    @Test
    void testCheckAuditPermission_UrbanExpert_WaiverApplication_NoPermission() {
        // 准备数据
        Long auditMid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");
        
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setLittleZoneId("10"); // 在城市专家负责的小区�?
        
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        
        // Mock 员工信息 - 城市体验专家
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        
        // Mock
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        
        // 执行并验�?- 应抛出无权限异常（城市专家不能审批免责申请）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintAuditService.submitForApproval(req, auditSoOut, false);
        });
        assertTrue(exception.getMessage().contains("无权�?));
    }

    /**
     * 测试审批权限校验 - 区域体验专家审批申请结案(5)有权�?
     */
    @Test
    void testCheckAuditPermission_RegionalExpert_Success() {
        // 准备数据 - 区域专家只能审批 72H无法结案(2) �?申请结案(5)
        Long auditMid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1);
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode());
        auditSoOut.setZoneId("1"); // 在区域专家负责的大区�?
        
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        
        // Mock 员工信息 - 区域体验专家
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        
        // Mock
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());
        
        // 执行 - 不应抛异�?
        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);
        
        // 验证
        assertTrue(result);
        verify(complaintAuditManager).approveAudit(any(), any(), any());
    }

    /**
     * 测试审批权限校验 - 满意度管理有权限
     */
    @Test
    void testCheckAuditPermission_SatisfactionManagement_Success() {
        // 准备数据 - 使用非免责类型，满意度管理人员对非免责类型有全部权限（无currentNode限制�?
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);

        // Mock 员工信息 - 满意度管�?
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);

        // Mock
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());

        // 执行 - 不应抛异�?
        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);
        
        // 验证
        assertTrue(result);
        verify(complaintAuditManager).approveAudit(any(), any(), any());
    }

    /**
     * 测试审批权限校验 - 区域体验专家审批改派门店(1)无权限抛异常
     */
    @Test
    void testCheckAuditPermission_RegionalExpert_ReassignmentStores_NoPermission() {
        Long auditMid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1);
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditSoOut.setZoneId("1");

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.submitForApproval(req, auditSoOut, false));
        assertTrue(exception.getMessage().contains("无权限审批此类型审批�?));
        verify(complaintAuditManager, never()).approveAudit(any(), any(), any());
    }

    /**
     * 测试审批权限校验 - 城市体验专家审批改派门店(1)有权�?
     */
    @Test
    void testCheckAuditPermission_UrbanExpert_ReassignmentStores_Success() {
        Long auditMid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setComplaintNo("C001");

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditSoOut.setLittleZoneId("10");

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(employeeInfo);
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);
        assertTrue(result);
        verify(complaintAuditManager).approveAudit(any(), any(), any());
    }

    // ============ preNextAudit 上一页下一页测�?============

    /**
     * 测试 preNextAudit - 满意度管理人员设�?auditTypeList 并调用网�?
     */
    @Test
    void testPreNextAudit_SatisfactionManagement_SetsAuditTypeListAndCallsGateway() {
        Long mid = 1001L;
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());
        ComplaintPreNextSoOut expectedOut = new ComplaintPreNextSoOut();
        expectedOut.setPreAuditId(1000L);
        expectedOut.setNextAuditId(1002L);
        when(complaintAuditGateway.selectPreAndAfter(any())).thenReturn(expectedOut);

        ComplaintPreNextSoOut result = complaintAuditService.preNextAudit(req);

        assertNotNull(result);
        assertEquals(1000L, result.getPreAuditId());
        assertEquals(1002L, result.getNextAuditId());
        ArgumentCaptor<ComplaintPreNextSoIn> captor = ArgumentCaptor.forClass(ComplaintPreNextSoIn.class);
        verify(complaintAuditGateway).selectPreAndAfter(captor.capture());
        assertNotNull(captor.getValue().getAuditTypeList());
        assertFalse(captor.getValue().getAuditTypeList().isEmpty());
        assertEquals(AuditStatusEnum.PENDING.getCode(), captor.getValue().getAuditStatus());
        assertEquals(Collections.singletonList(3), captor.getValue().getWaiverCurrentNodeList());
    }

    /**
     * 测试 preNextAudit - 区域体验专家设置 zoneIdList �?auditTypeList 并调用网�?
     */
    @Test
    void testPreNextAudit_RegionalExpert_SetsZoneAndAuditTypeAndCallsGateway() {
        Long mid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1, 2);
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds));
        ComplaintPreNextSoOut expectedOut = new ComplaintPreNextSoOut();
        when(complaintAuditGateway.selectPreAndAfter(any())).thenReturn(expectedOut);

        complaintAuditService.preNextAudit(req);

        ArgumentCaptor<ComplaintPreNextSoIn> captor = ArgumentCaptor.forClass(ComplaintPreNextSoIn.class);
        verify(complaintAuditGateway).selectPreAndAfter(captor.capture());
        assertEquals(zoneIds, captor.getValue().getZoneIdList());
        assertNotNull(captor.getValue().getAuditTypeList());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode()));
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode()));
    }

    /**
     * 测试 preNextAudit - 城市体验专家设置 littleZoneIdList �?auditTypeList 并调用网�?
     */
    @Test
    void testPreNextAudit_UrbanExpert_SetsLittleZoneAndAuditTypeAndCallsGateway() {
        Long mid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds));
        ComplaintPreNextSoOut expectedOut = new ComplaintPreNextSoOut();
        when(complaintAuditGateway.selectPreAndAfter(any())).thenReturn(expectedOut);

        complaintAuditService.preNextAudit(req);

        ArgumentCaptor<ComplaintPreNextSoIn> captor = ArgumentCaptor.forClass(ComplaintPreNextSoIn.class);
        verify(complaintAuditGateway).selectPreAndAfter(captor.capture());
        assertEquals(littleZoneIds, captor.getValue().getLittleZoneIdList());
        assertNotNull(captor.getValue().getAuditTypeList());
        assertEquals(1, captor.getValue().getAuditTypeList().size());
        assertTrue(captor.getValue().getAuditTypeList().contains(AuditTypeEnum.REASSIGNMENT_STORES.getCode()));
    }

    /**
     * 测试 preNextAudit - 区域运营管理设置 zoneIdList、auditTypeList=[门店免责]、waiverCurrentNodeList=[2] 并调用网�?
     */
    @Test
    void testPreNextAudit_RegionalOps_SetsZoneAuditTypeWaiverNodeAndCallsGateway() {
        Long mid = 1004L;
        List<Integer> zoneIds = Arrays.asList(1, 2);
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(zoneIds));
        ComplaintPreNextSoOut expectedOut = new ComplaintPreNextSoOut();
        when(complaintAuditGateway.selectPreAndAfter(any())).thenReturn(expectedOut);

        complaintAuditService.preNextAudit(req);

        ArgumentCaptor<ComplaintPreNextSoIn> captor = ArgumentCaptor.forClass(ComplaintPreNextSoIn.class);
        verify(complaintAuditGateway).selectPreAndAfter(captor.capture());
        assertEquals(zoneIds, captor.getValue().getZoneIdList());
        assertEquals(Collections.singletonList(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()), captor.getValue().getAuditTypeList());
        assertEquals(Collections.singletonList(2), captor.getValue().getWaiverCurrentNodeList());
    }

    /**
     * 测试 preNextAudit - 城市服务经理设置 littleZoneIdList、auditTypeList=[门店免责]、waiverCurrentNodeList=[1] 并调用网�?
     */
    @Test
    void testPreNextAudit_CityServiceManager_SetsLittleZoneAuditTypeWaiverNodeAndCallsGateway() {
        Long mid = 1006L;
        List<Integer> littleZoneIds = Arrays.asList(10, 20);
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_CityServiceManager(littleZoneIds));
        ComplaintPreNextSoOut expectedOut = new ComplaintPreNextSoOut();
        when(complaintAuditGateway.selectPreAndAfter(any())).thenReturn(expectedOut);

        complaintAuditService.preNextAudit(req);

        ArgumentCaptor<ComplaintPreNextSoIn> captor = ArgumentCaptor.forClass(ComplaintPreNextSoIn.class);
        verify(complaintAuditGateway).selectPreAndAfter(captor.capture());
        assertEquals(littleZoneIds, captor.getValue().getLittleZoneIdList());
        assertEquals(Collections.singletonList(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()), captor.getValue().getAuditTypeList());
        assertEquals(Collections.singletonList(1), captor.getValue().getWaiverCurrentNodeList());
    }

    /**
     * 测试 preNextAudit - 非上�?岗位返回空且不调用网�?
     */
    @Test
    void testPreNextAudit_NoPermission_ReturnsEmptyAndNoGatewayCall() {
        Long mid = 1005L;
        ComplaintPreNextSoIn req = new ComplaintPreNextSoIn();
        req.setMid(mid);
        req.setId(1001L);

        CarEmployeeInfoGoOut noRole = new CarEmployeeInfoGoOut();
        noRole.setChannelPositionInfoList(new ArrayList<>());
        noRole.setLittleZonePositionsInfoList(new ArrayList<>());
        noRole.setBigZonePositionsInfoList(new ArrayList<>());
        noRole.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(noRole);

        ComplaintPreNextSoOut result = complaintAuditService.preNextAudit(req);

        assertNotNull(result);
        assertNull(result.getPreAuditId());
        assertNull(result.getNextAuditId());
        verify(complaintAuditGateway, never()).selectPreAndAfter(any());
    }

    // ============ getComplaintAuditDetail 详情查看权限校验 ============

    /**
     * 测试 getComplaintAuditDetail - 审批单不存在(selectById 返回 null)时抛异常
     */
    @Test
    void testGetComplaintAuditDetail_SoOutNull_ThrowsBusinessException() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn(1001L, mid, null, null);

        when(complaintAuditGateway.selectById(req.getId())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(exception.getMessage().contains("未找到对应审批信�?));
    }

    /**
     * 测试 getComplaintAuditDetail - 满意度管理查看不允许的审批类�?4申请结案)抛异�?
     */
    @Test
    void testGetComplaintAuditDetail_SatisfactionManagement_DisallowedAuditType_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn(1001L, mid, null, null);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());
        ComplaintAuditSoOut soOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        soOut.setComplaintNo("C001");
        soOut.setAuditStatus(2); // 避免 auditStatus=1 时进入解�?applyContent 分支导致 NPE
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(soOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(exception.getMessage().contains("没有权限"));
    }

    /**
     * 测试 getComplaintAuditDetail - 服务满意度管理查看申请门店免责且 current_node=1（一审）时，按钮置灰不抛异常
     */
    @Test
    void testGetComplaintAuditDetail_SatisfactionManagement_WaiverNode1_GrayButton() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn(1001L, mid, null, null);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());
        ComplaintAuditSoOut soOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        soOut.setComplaintNo("C001");
        soOut.setCurrentNode(1); // 一审节点，满意度管理仅可查看三�?current_node=3)
        soOut.setAuditStatus(2);
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(soOut);

        // 不抛异常，但grayButton被设为true
        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);
        assertNotNull(result);
        assertTrue(result.getGrayButton());
    }

    /**
     * 测试 getComplaintAuditDetail - 区域体验专家查看不允许的审批类型(改派门店1)抛异�?
     */
    @Test
    void testGetComplaintAuditDetail_RegionalExpert_DisallowedAuditType_Throws() {
        Long mid = 1002L;
        List<Integer> zoneIds = Arrays.asList(1);
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn(1001L, mid, null, null);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(zoneIds));
        ComplaintAuditSoOut soOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        soOut.setComplaintNo("C001");
        soOut.setZoneId("1");
        soOut.setAuditStatus(2); // 避免 auditStatus=1 时进入解�?applyContent 分支导致 NPE
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(soOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(exception.getMessage().contains("没有权限"));
    }

    /**
     * 测试 getComplaintAuditDetail - 城市体验专家查看不允许的审批类型(72H无法结案2)抛异�?
     */
    @Test
    void testGetComplaintAuditDetail_UrbanExpert_DisallowedAuditType_Throws() {
        Long mid = 1003L;
        List<Integer> littleZoneIds = Arrays.asList(10);
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn(1001L, mid, null, null);

        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(littleZoneIds));
        ComplaintAuditSoOut soOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        soOut.setComplaintNo("C001");
        soOut.setLittleZoneId("10");
        soOut.setAuditStatus(2); // 避免 auditStatus=1 时进入解�?applyContent 分支导致 NPE
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(soOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(exception.getMessage().contains("没有权限"));
    }


    // ============ getAuditDetailForCustomerService 客服工作台结案审批详�?============

    @Test
    void testGetAuditDetailForCustomerService_Success() {
        String complaintNo = "C040";
        AuditDetailForCustomerServiceSoIn req = new AuditDetailForCustomerServiceSoIn();
        req.setComplaintNo(complaintNo);
        req.setMid(1001L);
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut(complaintNo, AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setCreateMid(1001L);
        auditSoOut.setApplyContent("{\"solutionDesc\":\"解决方案描述\",\"attachmentList\":[]}");
        when(complaintAuditGateway.getRecentAuditByComplaintNo(eq(complaintNo), eq(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode()))).thenReturn(auditSoOut);
        when(complaintAuditGateway.getClosingTagListByComplaintNo(complaintNo)).thenReturn(new ArrayList<>());
        when(eiamRemoteGateway.getNameByMid(any())).thenReturn(Collections.singletonMap(1001L, "申请�?));

        AuditDetailForCustomerServiceSoOut result = complaintAuditService.getAuditDetailForCustomerService(req);

        assertNotNull(result);
        assertEquals(auditSoOut.getId(), result.getId());
        assertEquals("解决方案描述", result.getSolution());
        assertNotNull(result.getClosingTagList());
        assertEquals("申请�?, result.getApplicantName());
    }

    // ============ judgeResponsibility 服务投诉判责 ============

    @Test
    void testJudgeResponsibility_IdNull_Throws() {
        JudgeResponsibilitySoIn req = JudgeResponsibilitySoIn.builder()
                .id(null)
                .responsible(1)
                .responsibleJudgeDesc("有责")
                .auditMid(1001L)
                .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.judgeResponsibility(req));
        assertTrue(ex.getMessage().contains("审批流id不能为空"));
        verify(complaintAuditManager, never()).judgeResponsibility(any(), any(), any());
    }

    // ============ submitForApproval 测试 ============

    @Test
    void testSubmitForApproval_Approved_CallsApproveAudit() {
        Long mid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(mid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        req.setAuditComment("同意");

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);

        assertTrue(result);
        verify(complaintAuditManager).approveAudit(eq(req), eq(auditSoOut), eq(orderGoOut));
        verify(complaintAuditManager, never()).refuseAudit(any(), any(), any());
    }

    @Test
    void testSubmitForApproval_Rejected_CallsRefuseAudit() {
        Long mid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(mid);
        req.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        req.setAuditComment("不同意，理由不充�?);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);

        assertTrue(result);
        verify(complaintAuditManager).refuseAudit(eq(req), eq(auditSoOut), eq(orderGoOut));
        verify(complaintAuditManager, never()).approveAudit(any(), any(), any());
    }

    @Test
    void testSubmitForApproval_Cancelled_CallsCancelAudit() {
        Long mid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(mid);
        req.setAuditStatus(AuditStatusEnum.CANCELLED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);

        assertTrue(result);
        verify(complaintAuditManager).cancelAudit(eq(req), eq(auditSoOut), eq(orderGoOut));
    }

    @Test
    void testSubmitForApproval_AuditNotExist_ThrowsBusinessException() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(999L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        when(complaintAuditGateway.selectById(req.getId())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.checkAuditParams(req));
        assertTrue(exception.getMessage().contains("投诉审核信息为空"));
    }

    @Test
    void testSubmitForApproval_OrderNotExist_ThrowsBusinessException() {
        Long mid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(mid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.submitForApproval(req, auditSoOut, false));
        assertTrue(exception.getMessage().contains("审批流绑定的客诉单号存在异常"));
    }

    @Test
    void testSubmitForApproval_RejectedWithoutComment_ThrowsBusinessException() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        req.setAuditComment("");

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.checkAuditParams(req));
        assertTrue(exception.getMessage().contains("驳回时，必填审批意见"));
    }

    @Test
    void testSubmitForApproval_AlreadyApproved_ThrowsBusinessException() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(100L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.checkAuditParams(req));
        assertTrue(exception.getMessage().contains("已经审批过了"));
    }

    // ============ judgeResponsibility 测试 ============

    @Test
    void testJudgeResponsibility_Success_Responsible() {
        Long mid = 1001L;
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("门店有责");
        req.setAuditMid(mid);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderGoOut.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        Boolean result = complaintAuditService.judgeResponsibility(req);

        assertTrue(result);
        verify(complaintAuditManager).judgeResponsibility(eq(req), eq(auditSoOut), eq(orderGoOut));
    }

    @Test
    void testJudgeResponsibility_Success_NotResponsible() {
        Long mid = 1001L;
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(0);
        req.setResponsibleJudgeDesc("无责");
        req.setAuditMid(mid);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderGoOut.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement());

        Boolean result = complaintAuditService.judgeResponsibility(req);

        assertTrue(result);
        verify(complaintAuditManager).judgeResponsibility(eq(req), eq(auditSoOut), eq(orderGoOut));
    }

    @Test
    void testJudgeResponsibility_NullId_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(null);
        req.setResponsible(1);
        req.setAuditMid(1001L);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("审批流id不能为空"));
    }

    @Test
    void testJudgeResponsibility_InvalidResponsible_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(2);
        req.setAuditMid(1001L);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("是否有责"));
    }

    @Test
    void testJudgeResponsibility_AuditNotExist_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(999L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(1001L);

        when(complaintAuditGateway.selectById(req.getId())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("审批单不存在"));
    }

    @Test
    void testJudgeResponsibility_WrongAuditType_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(1001L);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("当前审批单类型非服务投诉判责"));
    }

    @Test
    void testJudgeResponsibility_AlreadyProcessed_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(1001L);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("不能重复判责"));
    }

    @Test
    void testJudgeResponsibility_NotServiceComplaint_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(1001L);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("仅支持服务投�?));
    }

    @Test
    void testJudgeResponsibility_NotOnlineCS_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(1001L);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(anyLong())).thenReturn(new CarEmployeeInfoGoOut());
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderGoOut.setCreateSource(CreateSourceEnum.STORE.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertFalse(exception.getMessage().contains("仅支持创建来源为线上客服"));
    }

    @Test
    void testJudgeResponsibility_NoPermission_ThrowsBusinessException() {
        Long mid = 1001L;
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setResponsibleJudgeDesc("有责");
        req.setAuditMid(mid);

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintAuditGateway.selectById(req.getId())).thenReturn(auditSoOut);

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        orderGoOut.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);
        // 返回无权限员工信�?
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(TestDataBuilder.buildCarEmployeeInfo_ComplaintHandling());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("仅服务满意度管理岗位"));
    }

    @Test
    void testJudgeResponsibility_CommentTooLong_ThrowsBusinessException() {
        JudgeResponsibilitySoIn req = new JudgeResponsibilitySoIn();
        req.setId(100L);
        req.setResponsible(1);
        req.setAuditMid(1001L);
        // 构造超�?00字符的审批意�?
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 301; i++) {
            longComment.append("�?);
        }
        req.setResponsibleJudgeDesc(longComment.toString());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.judgeResponsibility(req));
        assertTrue(exception.getMessage().contains("审批意见不能超过300个字�?));
    }

    @Test
    void testGetAuditDetailForCustomerService_AuditNotFound_ThrowsBusinessException() {
        AuditDetailForCustomerServiceSoIn req = new AuditDetailForCustomerServiceSoIn();
        req.setComplaintNo("C999");
        req.setMid(1001L);

        when(complaintAuditGateway.getRecentAuditByComplaintNo("C999", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode()))
                .thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                complaintAuditService.getAuditDetailForCustomerService(req));
        assertTrue(exception.getMessage().contains("未查询到结案审批�?));
    }

    /**
     * 构建用于免责审批测试的auditSoOut
     */
    private ComplaintAuditSoOut buildWaiverAuditSoOut(String complaintNo, int currentNode) {
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut(complaintNo, AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(currentNode);
        auditSoOut.setOrgId("ORG001");
        auditSoOut.setCreateMid(2001L);
        return auditSoOut;
    }

    // ============ updateAuditAndWriteFollowRecord 测试 ============

    @Test
    void testUpdateAuditAndPrepareNextApply_NonWaiverApproved_OnlyCallsSubmitForApproval() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(100L);
        soIn.setAuditMid(1001L);
        soIn.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        // 非免责类型，selectById 返回的审批单
        ComplaintAuditSoOut fetchedAuditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        fetchedAuditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        // updateAuditAndWriteFollowRecord 内部先调�?selectById 获取审批单，再传�?submitForApproval
        when(complaintAuditGateway.selectById(100L)).thenReturn(fetchedAuditSoOut);
        when(auditServiceSelfRef.submitForApproval(soIn, fetchedAuditSoOut, true)).thenReturn(true);

        boolean result = complaintAuditService.updateAuditAndWriteFollowRecord(soIn, orderGoOut, fetchedAuditSoOut);

        assertTrue(result);
        verify(complaintAuditGateway).selectById(100L);
        verify(auditServiceSelfRef).submitForApproval(soIn, fetchedAuditSoOut, true);
        verify(auditServiceSelfRef, never()).checkAuditParams(any());
    }

    @Test
    void testUpdateAuditAndPrepareNextApply_WaiverRejected_OnlyCallsSubmitForApproval() {
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(100L);
        soIn.setAuditMid(1001L);
        soIn.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        soIn.setAuditComment("驳回");

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        ComplaintAuditSoOut fetchedAuditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        fetchedAuditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        when(complaintAuditGateway.selectById(100L)).thenReturn(fetchedAuditSoOut);
        when(auditServiceSelfRef.submitForApproval(soIn, fetchedAuditSoOut, true)).thenReturn(true);

        boolean result = complaintAuditService.updateAuditAndWriteFollowRecord(soIn, orderGoOut, fetchedAuditSoOut);

        assertTrue(result);
        verify(complaintAuditGateway).selectById(100L);
        verify(auditServiceSelfRef).submitForApproval(soIn, fetchedAuditSoOut, true);
        verify(auditServiceSelfRef, never()).checkAuditParams(any());
    }

    @Test
    void testUpdateAuditAndPrepareNextApply_WaiverApproved_Node1_DoesNotTriggerAsyncMsg() {
        String complaintNo = "C001";
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(100L);
        soIn.setAuditMid(1001L);
        soIn.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        ComplaintAuditSoOut fetchedAuditSoOut = buildWaiverAuditSoOut(complaintNo, 1);
        fetchedAuditSoOut.setId(100L);

        when(complaintAuditGateway.selectById(100L)).thenReturn(fetchedAuditSoOut);
        when(auditServiceSelfRef.submitForApproval(soIn, fetchedAuditSoOut, true)).thenReturn(true);

        boolean result = complaintAuditService.updateAuditAndWriteFollowRecord(soIn, orderGoOut, fetchedAuditSoOut);

        assertTrue(result);
        verify(complaintAuditGateway).selectById(100L);
        verify(auditServiceSelfRef).submitForApproval(soIn, fetchedAuditSoOut, true);
        verify(auditServiceSelfRef, never()).checkAuditParams(any());
        // 一审通过后不再由本系统异步发待审批消息（改由 BPM 通知，与 updateAuditAndWriteFollowRecord 内注释块一致）
        verify(constructMessageEventExecutorMock, never()).execute(any());
    }

    @Test
    void testUpdateAuditAndPrepareNextApply_WaiverApproved_MaxNode_NoAsyncMsg() {
        String complaintNo = "C001";
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();
        soIn.setId(100L);
        soIn.setAuditMid(1001L);
        soIn.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        ComplaintAuditSoOut fetchedAuditSoOut = buildWaiverAuditSoOut(complaintNo, 3);

        when(complaintAuditGateway.selectById(100L)).thenReturn(fetchedAuditSoOut);
        when(auditServiceSelfRef.submitForApproval(soIn, fetchedAuditSoOut, true)).thenReturn(true);

        boolean result = complaintAuditService.updateAuditAndWriteFollowRecord(soIn, orderGoOut, fetchedAuditSoOut);

        assertTrue(result);
        verify(complaintAuditGateway).selectById(100L);
        verify(auditServiceSelfRef).submitForApproval(soIn, fetchedAuditSoOut, true);
        verify(auditServiceSelfRef, never()).checkAuditParams(any());
        // 三审通过：不触发 asyncSubmitApplySendMsg（仅 currentNode < 3 时发�?
        verify(constructMessageEventExecutorMock, never()).execute(any());
    }

    // ============ asyncSubmitApplySendMsg 测试 ============

    /**
     * 通过反射调用私有方法 asyncSubmitApplySendMsg
     */
    private void invokeAsyncSubmitApplySendMsg(ComplaintOrderGoOut orderGoOut,
                                               SubmitForApprovalSoIn soIn) throws Exception {
        Method method = ComplaintAuditServiceImpl.class.getDeclaredMethod("asyncSubmitApplySendMsg",
                ComplaintOrderGoOut.class, SubmitForApprovalSoIn.class);
        method.setAccessible(true);
        try {
            method.invoke(complaintAuditService, orderGoOut, soIn);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    @Test
    void testAsyncSubmitApplySendMsg_Success_PublishesEvent() throws Exception {
        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        SubmitForApprovalSoIn soIn = new SubmitForApprovalSoIn();

        // 使executor同步执行Runnable
        doAnswer(inv -> {
            inv.getArgument(0, Runnable.class).run();
            return null;
        }).when(constructMessageEventExecutorMock).execute(any());

        // Mock消息策略
        MessageInformedStrategy mockStrategy = mock(MessageInformedStrategy.class);
        when(messageInformedEventFactory.getStrategy(anyString())).thenReturn(mockStrategy);
        MessageInformedEvent mockEvent = mock(MessageInformedEvent.class);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(mockEvent);

        invokeAsyncSubmitApplySendMsg(orderGoOut, soIn);

        // 验证事件被发�?
        verify(eventPublisher).publishEvent(mockEvent);
        verify(messageInformedEventFactory).getStrategy(anyString());
    }

    // ============ submitForApprovalResponsibilityExemption 测试 ============

    /**
     * 构建免责审批通用mock：通过checkAuditParams和checkAuditPermission
     * 使用满意度管理角�?+ currentNode=3 来通过权限校验
     */
    private ComplaintAuditSoOut setupExemptionCommonMocks(SubmitForApprovalSoIn req, String complaintNo) {
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut(complaintNo, AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(3);
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut(complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo(complaintNo)).thenReturn(orderGoOut);

        // 满意度管理角色通过权限校验
        CarEmployeeInfoGoOut employeeInfo = TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(req.getAuditMid())).thenReturn(employeeInfo);

        return auditSoOut;
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_OrderNotFound_Throws() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut));
        assertTrue(ex.getMessage().contains("审批流绑定的客诉单号存在异常"));
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_ProcessListEmpty_Throws() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut));
        assertTrue(ex.getMessage().contains("跟进记录为空"));
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_OldData_Approved_CallsApproveAudit() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");

        // 旧数据：processInstanceId为空
        ComplaintFollowProcessGoOut firstProcess = ComplaintFollowProcessGoOut.builder()
                .id(1L).complaintNo("C001").processInstanceId(null).processContent("{}").build();
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.singletonList(firstProcess));

        Boolean result = complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut);

        assertTrue(result);
        verify(complaintAuditManager).approveAudit(eq(req), any(), any());
        verify(complaintAuditManager, never()).refuseAudit(any(), any(), any());
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_OldData_Rejected_CallsRefuseAudit() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        req.setAuditComment("驳回原因");

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");

        // 旧数据：processInstanceId为空
        ComplaintFollowProcessGoOut firstProcess = ComplaintFollowProcessGoOut.builder()
                .id(1L).complaintNo("C001").processInstanceId("").processContent("{}").build();
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.singletonList(firstProcess));

        Boolean result = complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut);

        assertTrue(result);
        verify(complaintAuditManager).refuseAudit(eq(req), any(), any());
        verify(complaintAuditManager, never()).approveAudit(any(), any(), any());
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_BpmTaskListEmpty_Throws() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(1001L);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");

        // 新数据：有processInstanceId
        ComplaintFollowProcessGoOut firstProcess = ComplaintFollowProcessGoOut.builder()
                .id(1L).complaintNo("C001").processInstanceId("bpm-123").processContent("{}").build();
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.singletonList(firstProcess));

        // BPM返回空任务列�?
        ProcessCurrentTaskResponseDTO taskResponse = new ProcessCurrentTaskResponseDTO();
        taskResponse.setTaskList(Collections.emptyList());
        when(bpmRemoteGateway.processCurrentTaskList(any())).thenReturn(taskResponse);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut));
        assertTrue(ex.getMessage().contains("bpm任务列表为空"));
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_MidInReviewerList_CallsBpmTaskAudit() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");

        ComplaintFollowProcessGoOut firstProcess = ComplaintFollowProcessGoOut.builder()
                .id(1L).complaintNo("C001").processInstanceId("bpm-123").processContent("{}").build();
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.singletonList(firstProcess));

        // BPM任务列表包含当前审批�?
        ProcessCurrentTaskResponseDTO.Reviewer reviewer = new ProcessCurrentTaskResponseDTO.Reviewer();
        reviewer.setMiId(String.valueOf(auditMid));
        ProcessCurrentTaskResponseDTO.TaskInfo taskInfo = new ProcessCurrentTaskResponseDTO.TaskInfo();
        taskInfo.setProcessInstanceId("bpm-123");
        taskInfo.setReviewerList(Collections.singletonList(reviewer));
        ProcessCurrentTaskResponseDTO taskResponse = new ProcessCurrentTaskResponseDTO();
        taskResponse.setTaskList(Collections.singletonList(taskInfo));
        when(bpmRemoteGateway.processCurrentTaskList(any())).thenReturn(taskResponse);

        // Mock邮箱查询和bpm审批
        when(carEmployeeRemoteGateway.queryEmailByMid(auditMid)).thenReturn("test@xiaomi.com");
        when(bpmRemoteGateway.taskAudit(any())).thenReturn(true);

        try (MockedStatic<ComplaintApplyUtil> mockedUtil = mockStatic(ComplaintApplyUtil.class)) {
            mockedUtil.when(() -> ComplaintApplyUtil.buildBpmTaskAuditDTO(any(), any(), any(), any())).thenReturn(null);
            when(bpmRemoteGateway.taskAudit(any())).thenReturn(true);

            Boolean result = complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut);

            assertTrue(result);
            verify(bpmRemoteGateway).taskAudit(any());
            verify(carEmployeeRemoteGateway).queryEmailByMid(auditMid);
        }
    }

    @Test
    void testSubmitForApprovalResponsibilityExemption_MidNotInReviewerList_ThrowsNoBpmPermission() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = setupExemptionCommonMocks(req, "C001");

        ComplaintFollowProcessGoOut firstProcess = ComplaintFollowProcessGoOut.builder()
                .id(1L).complaintNo("C001").processInstanceId("bpm-123").processContent("{}").build();
        when(complaintFollowProcessRepositoryGateway.getProcessList(any())).thenReturn(Collections.singletonList(firstProcess));

        // BPM reviewerList 不含当前审批人：实现侧视为无审批平台权限，不调用 taskAudit
        ProcessCurrentTaskResponseDTO.Reviewer reviewer = new ProcessCurrentTaskResponseDTO.Reviewer();
        reviewer.setMiId("9999");
        ProcessCurrentTaskResponseDTO.TaskInfo taskInfo = new ProcessCurrentTaskResponseDTO.TaskInfo();
        taskInfo.setProcessInstanceId("bpm-123");
        taskInfo.setReviewerList(Collections.singletonList(reviewer));
        ProcessCurrentTaskResponseDTO taskResponse = new ProcessCurrentTaskResponseDTO();
        taskResponse.setTaskList(Collections.singletonList(taskInfo));
        when(bpmRemoteGateway.processCurrentTaskList(any())).thenReturn(taskResponse);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintAuditService.submitForApprovalResponsibilityExemption(req, auditSoOut));
        assertTrue(ex.getMessage().contains("审批平台"));
        verify(bpmRemoteGateway, never()).taskAudit(any());
    }

    // ============ checkAuditPermission else-if 分支测试 (通过submitForApproval) ============

    /**
     * 区域运营管理 + 免责二审 �?通过权限校验
     */
    @Test
    void testCheckAuditPermission_RegionalOpsManager_WaiverNode2_Success() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(2);
        auditSoOut.setZoneId("10");
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(Arrays.asList(10));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(empInfo);
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);

        assertTrue(result);
        verify(complaintAuditManager).approveAudit(eq(req), any(), any());
    }

    /**
     * 区域运营管理 + 免责但不是二�?�?抛异�?
     */
    @Test
    void testCheckAuditPermission_RegionalOpsManager_WaiverNode1_Throws() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setZoneId("10");
        auditSoOut.setLittleZoneId("100");
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(Arrays.asList(10));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.submitForApproval(req, auditSoOut, false));
        assertTrue(ex.getMessage().contains("无权�?));
    }

    /**
     * 城市服务经理 + 免责一�?�?通过权限校验
     */
    @Test
    void testCheckAuditPermission_CityServiceManager_WaiverNode1_Success() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setLittleZoneId("100");
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_CityServiceManager(Arrays.asList(100));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(empInfo);
        doNothing().when(complaintAuditManager).approveAudit(any(), any(), any());

        Boolean result = complaintAuditService.submitForApproval(req, auditSoOut, false);

        assertTrue(result);
        verify(complaintAuditManager).approveAudit(eq(req), any(), any());
    }

    /**
     * 城市服务经理 + 免责但不是一�?�?抛异�?
     */
    @Test
    void testCheckAuditPermission_CityServiceManager_WaiverNode2_Throws() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(2);
        auditSoOut.setLittleZoneId("100");
        auditSoOut.setZoneId("10");
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_CityServiceManager(Arrays.asList(100));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.submitForApproval(req, auditSoOut, false));
        assertTrue(ex.getMessage().contains("无权�?));
    }

    /**
     * 所有角色权限都不通过 �?�?您没有审批权�?
     */
    @Test
    void testCheckAuditPermission_NoMatchingRole_Throws() {
        Long auditMid = 1001L;
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setId(1001L);
        req.setAuditMid(auditMid);
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());

        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut("C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setLittleZoneId("100");
        auditSoOut.setZoneId("10");
        auditSoOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());

        ComplaintOrderGoOut orderGoOut = TestDataBuilder.buildComplaintOrderGoOut("C001", ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        when(complaintGateway.selectByComplaintNo("C001")).thenReturn(orderGoOut);

        // 无任何岗位角色的员工
        CarEmployeeInfoGoOut empInfo = new CarEmployeeInfoGoOut();
        empInfo.setChannelPositionInfoList(new ArrayList<>());
        empInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        empInfo.setBigZonePositionsInfoList(new ArrayList<>());
        empInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(auditMid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.submitForApproval(req, auditSoOut, false));
        assertTrue(ex.getMessage().contains("审批权限"));
    }

    // ============ verifyDetailViewPermission 相关测试 (通过getComplaintAuditDetail) ============

    /**
     * 构建getComplaintAuditDetail通用mock
     */
    private ComplaintAuditSoOut setupAuditDetailMocks(Long mid, String complaintNo, int auditType) {
        ComplaintAuditSoOut auditSoOut = TestDataBuilder.buildComplaintAuditSoOut(complaintNo, auditType);
        // 不设置auditStatus为PENDING(code=1)，因为源码中存在getAuditStatus与REASSIGNMENT_STORES.getCode()比较的逻辑，PENDING的code会碰�?
        auditSoOut.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        when(complaintAuditGateway.selectById(any())).thenReturn(auditSoOut);
        return auditSoOut;
    }

    /**
     * verifySatisfactionManagementPermission: 满意度管�?+ 允许的类�?�?成功
     */
    @Test
    void testVerifyDetailView_SatisfactionManagement_AllowedType_Success() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
        assertFalse(result.getGrayButton());
    }

    /**
     * verifySatisfactionManagementPermission: 满意度管�?+ 免责非三�?�?grayButton=true
     */
    @Test
    void testVerifyDetailView_SatisfactionManagement_WaiverNonNode3_GrayButton() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_SatisfactionManagement();
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
        assertTrue(result.getGrayButton());
    }

    /**
     * tryVerifyRegionalExperienceExpert: 区域体验专家 + 72H类型 + 匹配大区 �?成功
     */
    @Test
    void testVerifyDetailView_RegionalExpert_72H_MatchingZone_Success() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        auditSoOut.setZoneId("10");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(Arrays.asList(10));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
    }

    /**
     * tryVerifyRegionalExperienceExpert: 72H类型 + 不是区域体验专家 �?抛异�?
     */
    @Test
    void testVerifyDetailView_RegionalExpert_72H_NotExpert_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        auditSoOut.setZoneId("10");
        // 无任何岗�?
        CarEmployeeInfoGoOut empInfo = new CarEmployeeInfoGoOut();
        empInfo.setChannelPositionInfoList(new ArrayList<>());
        empInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        empInfo.setBigZonePositionsInfoList(new ArrayList<>());
        empInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyRegionalExperienceExpert: 区域体验专家 + 72H类型 + 大区不匹�?�?抛异�?
     */
    @Test
    void testVerifyDetailView_RegionalExpert_72H_WrongZone_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
        auditSoOut.setZoneId("10");
        // 区域体验专家但负责大�?0，不匹配10
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalExpert(Arrays.asList(20));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyUrbanExperienceExpert: 城市体验专家 + 改派门店 + 匹配小区 �?成功
     */
    @Test
    void testVerifyDetailView_UrbanExpert_Reassignment_MatchingLittleZone_Success() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditSoOut.setLittleZoneId("100");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(Arrays.asList(100));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
    }

    /**
     * tryVerifyUrbanExperienceExpert: 改派门店 + 不是城市体验专家 �?抛异�?
     */
    @Test
    void testVerifyDetailView_UrbanExpert_Reassignment_NotExpert_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditSoOut.setLittleZoneId("100");
        CarEmployeeInfoGoOut empInfo = new CarEmployeeInfoGoOut();
        empInfo.setChannelPositionInfoList(new ArrayList<>());
        empInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        empInfo.setBigZonePositionsInfoList(new ArrayList<>());
        empInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyUrbanExperienceExpert: 城市体验专家 + 改派门店 + 小区不匹�?�?抛异�?
     */
    @Test
    void testVerifyDetailView_UrbanExpert_Reassignment_WrongLittleZone_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.REASSIGNMENT_STORES.getCode());
        auditSoOut.setLittleZoneId("100");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_UrbanExpert(Arrays.asList(200));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyWaiverSecondNodePermission: 区域运营管理 + 免责二审 + 匹配大区 �?成功
     */
    @Test
    void testVerifyDetailView_RegionalOps_WaiverNode2_MatchingZone_Success() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(2);
        auditSoOut.setZoneId("10");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(Arrays.asList(10));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
    }

    /**
     * tryVerifyWaiverSecondNodePermission: 免责二审 + 不是区域运营管理 �?抛异�?
     */
    @Test
    void testVerifyDetailView_WaiverNode2_NotRegionalOps_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(2);
        auditSoOut.setZoneId("10");
        // 无任何角�?
        CarEmployeeInfoGoOut empInfo = new CarEmployeeInfoGoOut();
        empInfo.setChannelPositionInfoList(new ArrayList<>());
        empInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        empInfo.setBigZonePositionsInfoList(new ArrayList<>());
        empInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyWaiverSecondNodePermission: 区域运营管理 + 免责二审 + 大区不匹�?�?抛异�?
     */
    @Test
    void testVerifyDetailView_RegionalOps_WaiverNode2_WrongZone_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(2);
        auditSoOut.setZoneId("10");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(Arrays.asList(20));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }

    /**
     * tryVerifyWaiverFirstNodePermission: 城市服务经理 + 免责一�?+ 匹配小区 �?成功
     */
    @Test
    void testVerifyDetailView_CityServiceManager_WaiverNode1_MatchingLittleZone_Success() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setLittleZoneId("100");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_CityServiceManager(Arrays.asList(100));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
        assertFalse(result.getGrayButton());
    }

    /**
     * tryVerifyWaiverFirstNodePermission: 区域运营管理 + 免责一�?+ 匹配大区 �?grayButton=true
     */
    @Test
    void testVerifyDetailView_RegionalOps_WaiverNode1_MatchingZone_GrayButton() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setZoneId("10");
        auditSoOut.setLittleZoneId("100");
        CarEmployeeInfoGoOut empInfo = TestDataBuilder.buildCarEmployeeInfo_RegionalOpsManager(Arrays.asList(10));
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        ComplaintAuditSoOut result = complaintAuditService.getComplaintAuditDetail(req);

        assertNotNull(result);
        assertTrue(result.getGrayButton());
    }

    /**
     * verifyDetailViewPermission: 无任何匹配角�?�?�?没有权限查看"
     */
    @Test
    void testVerifyDetailView_NoMatchingRole_Throws() {
        Long mid = 1001L;
        ComplaintAuditDetailSoIn req = new ComplaintAuditDetailSoIn();
        req.setId(1001L);
        req.setMid(mid);

        // 免责一审，但无任何匹配角色
        ComplaintAuditSoOut auditSoOut = setupAuditDetailMocks(mid, "C001", AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        auditSoOut.setCurrentNode(1);
        auditSoOut.setLittleZoneId("100");
        auditSoOut.setZoneId("10");
        CarEmployeeInfoGoOut empInfo = new CarEmployeeInfoGoOut();
        empInfo.setChannelPositionInfoList(new ArrayList<>());
        empInfo.setLittleZonePositionsInfoList(new ArrayList<>());
        empInfo.setBigZonePositionsInfoList(new ArrayList<>());
        empInfo.setHeadPositionsInfoList(new ArrayList<>());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2(mid)).thenReturn(empInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> complaintAuditService.getComplaintAuditDetail(req));
        assertTrue(ex.getMessage().contains("没有权限"));
    }
}
