package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.car.soc.gw.api.dto.res.MrOrderSimple;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultSelectorResp;
import com.wt.complaint.manage.domain.api.enums.ConsultOrderStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserConsultOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigParser;
import com.wt.complaint.manage.domain.model.ConsultStatusCountInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserConsultViewServiceImpl 单元测试
 * 不启动Spring容器，Mock所有Gateway和外部服�?
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class UserConsultViewServiceImplUnitTest {

    @InjectMocks
    private UserConsultViewServiceImpl userConsultViewService;

    @Mock
    private UserConsultOrderGateway userConsultOrderGateway;
    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private MoneThreadPoolExecutor commonThreadPoolExecutor;
    @Mock
    private UserAuthManager userAuthManager;
    @Mock
    private CarRemoteGateway carRemoteGateway;
    @Mock
    private CarUserRemoteGateway carUserRemoteGateway;
    @Mock
    private ClubRpcGateway clubRpcGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;
    @Mock
    private UpcConfigParser upcConfigParser;
    @Mock
    private UpcConfigLocalCache localCache;
    @Mock
    private FileRemoteGateway fileRemoteGateway;
    @Mock
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;
    @Mock
    private MrOrderGateway mrOrderGateway;

    private static final String TEST_CONSULT_NO = "ZX20260301001";
    private static final Long TEST_MID = 1001L;
    private static final String TEST_ORG_ID = "F001";
    private static final String TEST_VID = "V001";

    @BeforeEach
    void setUp() {
        // 让线程池同步执行
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(commonThreadPoolExecutor).execute(any(Runnable.class));
    }

    // ==================== getConsultSelectorList ====================

    @Test
    void getConsultSelectorList_success() {
        ConsultSelectorResp result = userConsultViewService.getConsultSelectorList();

        assertNotNull(result);
        assertNotNull(result.getConsultStatusEnum());
        assertNotNull(result.getConsultTypeEnum());
        assertNotNull(result.getUrgencyLevelEnum());
        assertNotNull(result.getHandleResultEnum());
        assertTrue(result.getConsultStatusEnum().size() > 0);
    }

    // ==================== queryStatisticsItems ====================

    @Test
    void queryStatisticsItems_success() {
        ConsultStatisticsSoIn soIn = new ConsultStatisticsSoIn();
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setMid(TEST_MID);

        List<ConsultStatusCountInfo> countInfos = new ArrayList<>();
        ConsultStatusCountInfo waitReceive = new ConsultStatusCountInfo();
        waitReceive.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        waitReceive.setCnt(5);
        countInfos.add(waitReceive);

        ConsultStatusCountInfo waitFirstResponse = new ConsultStatusCountInfo();
        waitFirstResponse.setOrderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        waitFirstResponse.setCnt(3);
        countInfos.add(waitFirstResponse);

        ConsultStatusCountInfo waitClose = new ConsultStatusCountInfo();
        waitClose.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        waitClose.setCnt(2);
        countInfos.add(waitClose);

        ConsultStatusCountInfo completed = new ConsultStatusCountInfo();
        completed.setOrderStatus(ConsultOrderStatusEnum.COMPLETED.getCode());
        completed.setCnt(10);
        countInfos.add(completed);

        when(userConsultOrderGateway.countConsultStatistics(anyString(), anyLong())).thenReturn(countInfos);

        ConsultStatisticsSoOut result = userConsultViewService.queryStatisticsItems(soIn);

        assertNotNull(result);
        assertEquals(5, result.getPendingReceiveCount());
        assertEquals(3, result.getPendingFirstResponseCount());
        assertEquals(2, result.getPendingCloseCount());
        assertEquals(10, result.getCompletedCount());
    }

    @Test
    void queryStatisticsItems_empty_returnsNull() {
        ConsultStatisticsSoIn soIn = new ConsultStatisticsSoIn();
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setMid(TEST_MID);
        when(userConsultOrderGateway.countConsultStatistics(anyString(), anyLong())).thenReturn(Collections.emptyList());

        ConsultStatisticsSoOut result = userConsultViewService.queryStatisticsItems(soIn);

        assertNull(result);
    }

    // ==================== queryConsultList / queryWebConsultList ====================

    @Test
    void queryConsultList_success() {
        ConsultListSoIn soIn = new ConsultListSoIn();
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setPageNum(1);
        soIn.setPageSize(10);

        when(userConsultOrderGateway.countWebConsultPage(any(ConsultListGoIn.class))).thenReturn(1L);
        List<UserConsultOrderInfo> orderList = Collections.singletonList(buildUserConsultOrderInfo());
        when(userConsultOrderGateway.pageWebConsultOrders(any(ConsultListGoIn.class))).thenReturn(orderList);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(Collections.emptyMap());
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());

        ConsultListSoOut result = userConsultViewService.queryConsultList(soIn);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertNotNull(result.getDataList());
        assertEquals(1, result.getDataList().size());
    }

    @Test
    void queryWebConsultList_success() {
        ConsultListSoIn soIn = new ConsultListSoIn();
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setPageNum(1);
        soIn.setPageSize(10);

        when(userConsultOrderGateway.countWebConsultPage(any(ConsultListGoIn.class))).thenReturn(1L);
        List<UserConsultOrderInfo> orderList = Collections.singletonList(buildUserConsultOrderInfo());
        when(userConsultOrderGateway.pageWebConsultOrders(any(ConsultListGoIn.class))).thenReturn(orderList);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(Collections.emptyMap());
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());

        ConsultListSoOut result = userConsultViewService.queryWebConsultList(soIn);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
    }

    @Test
    void queryConsultList_withRelationOrders() {
        ConsultListSoIn soIn = new ConsultListSoIn();
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setPageNum(1);
        soIn.setPageSize(10);

        when(userConsultOrderGateway.countWebConsultPage(any(ConsultListGoIn.class))).thenReturn(1L);
        List<UserConsultOrderInfo> orderList = Collections.singletonList(buildUserConsultOrderInfo());
        when(userConsultOrderGateway.pageWebConsultOrders(any(ConsultListGoIn.class))).thenReturn(orderList);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(Collections.emptyMap());
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());

        ComplaintRelationOrderGoOut relation = new ComplaintRelationOrderGoOut();
        relation.setBizNo("ST001");
        relation.setComplaintNo(TEST_CONSULT_NO);
        relation.setBizType(2);
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.singletonList(relation));

        MrOrderSimple mrOrder = new MrOrderSimple();
        mrOrder.setSuperTicketNo("ST001");
        mrOrder.setMrNo("MR001");
        when(mrOrderGateway.getSimpleMrOrderInfo(anyList())).thenReturn(Collections.singletonList(mrOrder));

        ConsultListSoOut result = userConsultViewService.queryConsultList(soIn);

        assertNotNull(result);
        assertEquals("MR001", result.getDataList().get(0).getMrNo());
    }

    // ==================== queryPadConsultList ====================

    @Test
    void queryPadConsultList_success() {
        PadConsultListReq req = new PadConsultListReq();
        req.setOrgId(TEST_ORG_ID);
        req.setPageNum(1);
        req.setPageSize(10);

        when(userConsultOrderGateway.countPadConsultPage(any(ConsultListGoIn.class))).thenReturn(1L);
        List<UserConsultOrderInfo> orderList = Collections.singletonList(buildUserConsultOrderInfo());
        when(userConsultOrderGateway.pagePadConsultOrders(any(ConsultListGoIn.class))).thenReturn(orderList);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(Collections.emptyMap());
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());

        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vin("LMWTEST1234567890")
                .vid(TEST_VID)
                .build();
        when(carRemoteGateway.getCarSimpleInfo(anyList(), any())).thenReturn(Collections.singletonList(carInfo));

        ConsultListSoOut result = userConsultViewService.queryPadConsultList(req);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
    }

    @Test
    void queryPadConsultList_completedOrder_vinMasked() {
        PadConsultListReq req = new PadConsultListReq();
        req.setOrgId(TEST_ORG_ID);
        req.setPageNum(1);
        req.setPageSize(10);

        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.COMPLETED.getCode());
        when(userConsultOrderGateway.countPadConsultPage(any(ConsultListGoIn.class))).thenReturn(1L);
        when(userConsultOrderGateway.pagePadConsultOrders(any(ConsultListGoIn.class))).thenReturn(Collections.singletonList(orderInfo));
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(Collections.emptyMap());
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.emptyList());

        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vin("LMWTEST1234567890")
                .vid(TEST_VID)
                .build();
        when(carRemoteGateway.getCarSimpleInfo(anyList(), any())).thenReturn(Collections.singletonList(carInfo));

        when(carUserRemoteGateway.userAggQuery(any(CarUserAggGoIn.class))).thenReturn(new CarUserAggGoOut());

        ConsultListSoOut result = userConsultViewService.queryPadConsultList(req);

        assertNotNull(result);
        String vin = result.getDataList().get(0).getVin();
        assertTrue(vin.startsWith("*******"));
    }

    // ==================== getConsultHandler ====================

    @Test
    void getConsultHandler_success() {
        ConsultHandlerListReq req = new ConsultHandlerListReq();
        req.setOrgId(TEST_ORG_ID);

        EmployeeInfoGoOut employee = new EmployeeInfoGoOut();
        employee.setMiId(TEST_MID);
        employee.setName("测试处理�?);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(employee));
        when(userConsultOrderGateway.findList(any(ConsultListGoIn.class)))
                .thenReturn(Collections.emptyList());

        ConsultHandlerListResp result = userConsultViewService.getConsultHandler(req);

        assertNotNull(result);
        assertNotNull(result.getHandlerList());
        assertEquals(1, result.getHandlerList().size());
        assertEquals("测试处理�?, result.getHandlerList().get(0).getName());
    }

    // ==================== getComplaintAuth ====================

    @Test
    void getComplaintAuth_missingRole_throwsBusinessException() {
        ComplaintFrameInfoSoIn param = new ComplaintFrameInfoSoIn();
        param.setComplaintNo(TEST_CONSULT_NO);

        try (MockedStatic<RpcContext> rpcContextMock = mockStatic(RpcContext.class)) {
            RpcContext mockContext = mock(RpcContext.class);
            rpcContextMock.when(RpcContext::getContext).thenReturn(mockContext);
            when(mockContext.getAttachment("$curr_role")).thenReturn("");
            when(mockContext.getAttachment("$upc_miID")).thenReturn(String.valueOf(TEST_MID));

            assertThrows(BusinessException.class, () -> userConsultViewService.getComplaintAuth(param));
        }
    }

    @Test
    void getComplaintAuth_missingMiID_throwsBusinessException() {
        ComplaintFrameInfoSoIn param = new ComplaintFrameInfoSoIn();
        param.setComplaintNo(TEST_CONSULT_NO);

        try (MockedStatic<RpcContext> rpcContextMock = mockStatic(RpcContext.class)) {
            RpcContext mockContext = mock(RpcContext.class);
            rpcContextMock.when(RpcContext::getContext).thenReturn(mockContext);
            when(mockContext.getAttachment("$curr_role")).thenReturn("ADMIN");
            when(mockContext.getAttachment("$upc_miID")).thenReturn("");

            assertThrows(BusinessException.class, () -> userConsultViewService.getComplaintAuth(param));
        }
    }

    @Test
    void getComplaintAuth_success() {
        ComplaintFrameInfoSoIn param = new ComplaintFrameInfoSoIn();
        param.setComplaintNo(TEST_CONSULT_NO);

        try (MockedStatic<RpcContext> rpcContextMock = mockStatic(RpcContext.class)) {
            RpcContext mockContext = mock(RpcContext.class);
            rpcContextMock.when(RpcContext::getContext).thenReturn(mockContext);
            when(mockContext.getAttachment("$curr_role")).thenReturn("ADMIN");
            when(mockContext.getAttachment("$upc_miID")).thenReturn(String.valueOf(TEST_MID));

            UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
            when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);
            when(upcConfigParser.getRoleList(any())).thenReturn(new ArrayList<>(Collections.singletonList("ADMIN")));
            when(localCache.getUpcConfigMap()).thenReturn(new HashMap<>());
            when(upcConfigParser.calcButtons(anyList(), any(), any())).thenReturn(Collections.singletonList("PICK_UP"));

            ComplaintFrameInfoSoOut result = userConsultViewService.getComplaintAuth(param);

            assertNotNull(result);
            assertNotNull(result.getUserActionAuth());
            assertTrue(result.getUserActionAuth().getButtons().contains("PICK_UP"));
        }
    }

    @Test
    void getComplaintAuth_orderNotFound_throwsBusinessException() {
        ComplaintFrameInfoSoIn param = new ComplaintFrameInfoSoIn();
        param.setComplaintNo(TEST_CONSULT_NO);

        try (MockedStatic<RpcContext> rpcContextMock = mockStatic(RpcContext.class)) {
            RpcContext mockContext = mock(RpcContext.class);
            rpcContextMock.when(RpcContext::getContext).thenReturn(mockContext);
            when(mockContext.getAttachment("$curr_role")).thenReturn("ADMIN");
            when(mockContext.getAttachment("$upc_miID")).thenReturn(String.valueOf(TEST_MID));
            when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(null);

            assertThrows(BusinessException.class, () -> userConsultViewService.getComplaintAuth(param));
        }
    }

    // ==================== getComplaintProcessRecords ====================

    @Test
    void getComplaintProcessRecords_byConsultNo_success() {
        ComplaintProcessSoIn soIn = new ComplaintProcessSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);

        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);
        when(processRepositoryGateway.getProcessListByNo(anyString())).thenReturn(Collections.emptyList());

        ComplaintProcessListSoOut result = userConsultViewService.getComplaintProcessRecords(soIn);

        assertNotNull(result);
    }

    @Test
    void getComplaintProcessRecords_bySuperTicketNo_success() {
        ComplaintProcessSoIn soIn = new ComplaintProcessSoIn();
        soIn.setConsultSuperTicketNo("ST001");

        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);
        when(processRepositoryGateway.getProcessListByNo(anyString())).thenReturn(Collections.emptyList());

        ComplaintProcessListSoOut result = userConsultViewService.getComplaintProcessRecords(soIn);

        assertNotNull(result);
    }

    @Test
    void getComplaintProcessRecords_padSource_filtersCreateAndInfoUpdate() {
        ComplaintProcessSoIn soIn = new ComplaintProcessSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setSource("PAD_DETAIL");

        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        List<ComplaintFollowProcessGoOut> processList = new ArrayList<>();
        ComplaintFollowProcessGoOut createProcess = ComplaintFollowProcessGoOut.builder()
                .complaintNo(TEST_CONSULT_NO)
                .processInstanceId("123456")
                .processContent("{}")
                .createTime(new Date())
                .processType(ProcessTypeEnum.CREATE_ORDER.getProcessCode()).build();
        processList.add(createProcess);

        ComplaintFollowProcessGoOut pickupProcess = ComplaintFollowProcessGoOut.builder()
                .complaintNo(TEST_CONSULT_NO)
                .processInstanceId("123456")
                .processContent("{}")
                .createTime(new Date())
                .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode()).build();
        processList.add(pickupProcess);

        when(processRepositoryGateway.getProcessListByNo(anyString())).thenReturn(processList);

        ComplaintProcessListSoOut result = userConsultViewService.getComplaintProcessRecords(soIn);

        assertNotNull(result);
    }

    // ==================== 辅助构建方法 ====================

    private UserConsultOrderInfo buildUserConsultOrderInfo() {
        UserConsultOrderInfo info = new UserConsultOrderInfo();
        info.setId(1L);
        info.setConsultNo(TEST_CONSULT_NO);
        info.setConsultType(1);
        info.setVid(TEST_VID);
        info.setOrgId(TEST_ORG_ID);
        info.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        info.setCreateMid(TEST_MID);
        info.setOperatorMid(TEST_MID);
        info.setPriority(4);
        info.setReminderTimes(0);
        info.setCreateTime(new Date());
        info.setProblemDesc("测试问题描述");
        return info;
    }
}
