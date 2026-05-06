package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.api.model.req.operate.IssueTypeContent;
import com.wt.complaint.manage.domain.api.enums.ConsultOrderStatusEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserConsultOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.strategy.consult.message.ConsultMessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.consult.message.ConsultMessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserConsultOperateServiceImpl 单元测试
 * 不启动Spring容器，Mock所有Gateway和外部服�?
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class UserConsultOperateServiceImplUnitTest {

    @InjectMocks
    private UserConsultOperateServiceImpl userConsultOperateService;

    @Mock
    private UserConsultOrderGateway userConsultOrderGateway;
    @Mock
    private NoGeneratorRemoteGateway noGeneratorRemoteGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private ConsultMessageInformedEventFactory consultMessageInformedEventFactory;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private FileRemoteGateway fileRemoteGateway;
    @Mock
    private MoneThreadPoolExecutor commonThreadPoolExecutor;
    @Mock
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;
    @Mock
    private CarRemoteGateway carRemoteGateway;
    @Mock
    private RmqGateway rmqGateway;

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

    // ==================== createConsultOrder ====================

    @Test
    void createConsultOrder_success() {
        CreateConsultOrderSoIn soIn = buildCreateConsultOrderSoIn();
        when(noGeneratorRemoteGateway.generateConsultNo()).thenReturn(TEST_CONSULT_NO);
        when(carRemoteGateway.getVinByVid(TEST_VID)).thenReturn("LMWTEST1234567890");
        when(userConsultOrderGateway.createUserConsultOrder(any(UcConsultOrderGoIn.class))).thenReturn(1);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        ConsultMessageInformedStrategy mockStrategy = mock(ConsultMessageInformedStrategy.class);
        when(consultMessageInformedEventFactory.getStrategy(anyString())).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(MessageInformedEvent.builder().build());

        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        CreateConsultOrderSoOut result = userConsultOperateService.createConsultOrder(soIn);

        assertNotNull(result);
        assertEquals(TEST_CONSULT_NO, result.getConsultNo());
        verify(userConsultOrderGateway).createUserConsultOrder(any(UcConsultOrderGoIn.class));
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    @Test
    void createConsultOrder_withMrSuperTicketNo_savesRelation() {
        CreateConsultOrderSoIn soIn = buildCreateConsultOrderSoIn();
        soIn.getExpandSoIn().setMrSuperTicketNo("ST001");
        when(noGeneratorRemoteGateway.generateConsultNo()).thenReturn(TEST_CONSULT_NO);
        when(carRemoteGateway.getVinByVid(TEST_VID)).thenReturn("LMWTEST1234567890");
        when(userConsultOrderGateway.createUserConsultOrder(any(UcConsultOrderGoIn.class))).thenReturn(1);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(complaintRelationOrderRepositoryGateway.save(any())).thenReturn(true);

        ConsultMessageInformedStrategy mockStrategy = mock(ConsultMessageInformedStrategy.class);
        when(consultMessageInformedEventFactory.getStrategy(anyString())).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(MessageInformedEvent.builder().build());
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        CreateConsultOrderSoOut result = userConsultOperateService.createConsultOrder(soIn);

        assertNotNull(result);
        verify(complaintRelationOrderRepositoryGateway).save(any(ComplaintRelationOrderGoIn.class));
    }

    @Test
    void createConsultOrder_gatewayFails_throwsBusinessException() {
        CreateConsultOrderSoIn soIn = buildCreateConsultOrderSoIn();
        when(noGeneratorRemoteGateway.generateConsultNo()).thenReturn(TEST_CONSULT_NO);
        when(carRemoteGateway.getVinByVid(TEST_VID)).thenReturn("LMWTEST1234567890");
        when(userConsultOrderGateway.createUserConsultOrder(any(UcConsultOrderGoIn.class))).thenReturn(0);

        assertThrows(BusinessException.class, () -> userConsultOperateService.createConsultOrder(soIn));
    }

    @Test
    void createConsultOrder_unexpectedException_throwsBusinessException() {
        CreateConsultOrderSoIn soIn = buildCreateConsultOrderSoIn();
        when(noGeneratorRemoteGateway.generateConsultNo()).thenThrow(new RuntimeException("unexpected"));

        assertThrows(BusinessException.class, () -> userConsultOperateService.createConsultOrder(soIn));
    }

    // ==================== editConsult ====================

    @Test
    void editConsult_orderNotFound_throwsBusinessException() {
        OrderEditConsultSoIn soIn = buildOrderEditConsultSoIn();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.editConsult(soIn));
    }

/*    @Test
    void editConsult_withMrSuperTicketNo_updatesExistingRelation() {
        OrderEditConsultSoIn soIn = buildOrderEditConsultSoIn();
        soIn.getExpandSoIn().setMrSuperTicketNo("ST002");
        UserConsultOrderInfo existingOrder = buildUserConsultOrderInfo();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(existingOrder);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);

        ComplaintRelationOrderGoOut existingRelation = ComplaintRelationOrderGoOut.builder()
                .id(1)
                .bizNo("ST001")
                .complaintNo(TEST_CONSULT_NO)
                .build();
        when(complaintRelationOrderRepositoryGateway.findList(any())).thenReturn(Collections.singletonList(existingRelation));
        when(complaintRelationOrderRepositoryGateway.update(any())).thenReturn(true);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(Collections.emptyMap());
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        OrderEditConsultSoOut result = userConsultOperateService.editConsult(soIn);

        assertNotNull(result);
        verify(complaintRelationOrderRepositoryGateway).update(any(ComplaintRelationOrderGoIn.class));
    }*/

    // ==================== pickUpOrder ====================

    @Test
    void pickUpOrder_success() {
        ConsultOrderPickUpSoIn soIn = new ConsultOrderPickUpSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setPickUpMid(String.valueOf(TEST_MID));

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(TEST_MID, "测试接单�?));
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(employees);
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);

        ConsultOrderPickUpSoOut result = userConsultOperateService.pickUpOrder(soIn);

        assertNotNull(result);
        assertEquals("success", result.getResult());
        verify(userConsultOrderGateway).updateOrderSelective(any(UcConsultOrderUpdateGoIn.class));
    }

    @Test
    void pickUpOrder_orderNotFound_throwsBusinessException() {
        ConsultOrderPickUpSoIn soIn = new ConsultOrderPickUpSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setPickUpMid(String.valueOf(TEST_MID));
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.pickUpOrder(soIn));
    }

    @Test
    void pickUpOrder_wrongStatus_throwsBusinessException() {
        ConsultOrderPickUpSoIn soIn = new ConsultOrderPickUpSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setPickUpMid(String.valueOf(TEST_MID));

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        assertThrows(BusinessException.class, () -> userConsultOperateService.pickUpOrder(soIn));
    }

    @Test
    void pickUpOrder_noPermission_throwsBusinessException() {
        ConsultOrderPickUpSoIn soIn = new ConsultOrderPickUpSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setPickUpMid(String.valueOf(TEST_MID));

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> userConsultOperateService.pickUpOrder(soIn));
    }

    // ==================== addFollowUpRecords ====================

    @Test
    void addFollowUpRecords_success_waitFirstResponse_updatesStatus() {
        OrderAddFollowUpRecordSoIn soIn = buildOrderAddFollowUpRecordSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);

        OrderFollowUpRecordSoOut result = userConsultOperateService.addFollowUpRecords(soIn);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getRecordResult());
        verify(userConsultOrderGateway).updateOrderSelective(any(UcConsultOrderUpdateGoIn.class));
    }

    @Test
    void addFollowUpRecords_success_waitClose_noStatusUpdate() {
        OrderAddFollowUpRecordSoIn soIn = buildOrderAddFollowUpRecordSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        OrderFollowUpRecordSoOut result = userConsultOperateService.addFollowUpRecords(soIn);

        assertNotNull(result);
        verify(userConsultOrderGateway, never()).updateOrderSelective(any(UcConsultOrderUpdateGoIn.class));
    }

    @Test
    void addFollowUpRecords_orderNotFound_throwsBusinessException() {
        OrderAddFollowUpRecordSoIn soIn = buildOrderAddFollowUpRecordSoIn();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.addFollowUpRecords(soIn));
    }

    @Test
    void addFollowUpRecords_updateFails_throwsBusinessException() {
        OrderAddFollowUpRecordSoIn soIn = buildOrderAddFollowUpRecordSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(Long.valueOf(soIn.getFollowUpMid()), "测试跟进�?));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(0);

        assertThrows(BusinessException.class, () -> userConsultOperateService.addFollowUpRecords(soIn));
    }

    // ==================== reassign ====================

    @Test
    void reassign_success() {
        ConsultReassignSoIn soIn = buildConsultReassignSoIn();

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        Map<Long, CarEmployee> carEmployeeMap = new HashMap<>();
        CarEmployee operator = new CarEmployee(soIn.getOperateMid(), "操作�?, CarEmployeeEnum.MANAGER.getCode(), "13800000001", "op@xiaomi.com");
        CarEmployee receiver = new CarEmployee(soIn.getReassignOperatorMid(), "接收�?, CarEmployeeEnum.RECEIVER.getCode(), "13800000002", "recv@xiaomi.com");
        carEmployeeMap.put(soIn.getOperateMid(), operator);
        carEmployeeMap.put(soIn.getReassignOperatorMid(), receiver);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(carEmployeeMap);

        List<EmployeeInfoGoOut> managerEmployees = Collections.singletonList(buildEmployeeInfoGoOut(soIn.getOperateMid(), "操作�?));
        List<EmployeeInfoGoOut> receiverEmployees = Collections.singletonList(buildEmployeeInfoGoOut(soIn.getReassignOperatorMid(), "接收�?));
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(managerEmployees)
                .thenReturn(receiverEmployees);

        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);
        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        ConsultMessageInformedStrategy mockStrategy = mock(ConsultMessageInformedStrategy.class);
        when(consultMessageInformedEventFactory.getStrategy(anyString())).thenReturn(mockStrategy);
        when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(MessageInformedEvent.builder().build());

        ConsultReassignSoOut result = userConsultOperateService.reassign(soIn);

        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void reassign_orderNotFound_throwsBusinessException() {
        ConsultReassignSoIn soIn = buildConsultReassignSoIn();
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.reassign(soIn));
    }

    @Test
    void reassign_wrongStatus_throwsBusinessException() {
        ConsultReassignSoIn soIn = buildConsultReassignSoIn();
        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        assertThrows(BusinessException.class, () -> userConsultOperateService.reassign(soIn));
    }

    @Test
    void reassign_noDispatchPermission_throwsBusinessException() {
        ConsultReassignSoIn soIn = buildConsultReassignSoIn();
        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        Map<Long, CarEmployee> carEmployeeMap = new HashMap<>();
        CarEmployee operator = new CarEmployee(soIn.getOperateMid(), "操作�?, CarEmployeeEnum.MANAGER.getCode(), "13800000001", "op@xiaomi.com");
        carEmployeeMap.put(soIn.getOperateMid(), operator);
        when(carEmployeeRemoteGateway.queryCarEmployee(anyList())).thenReturn(carEmployeeMap);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> userConsultOperateService.reassign(soIn));
    }

    // ==================== submitChangeOrgApply ====================

    @Test
    void submitChangeOrgApply_success() {
        ConsultOrgChangeApplySoIn soIn = new ConsultOrgChangeApplySoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setApplyOrgId(TEST_ORG_ID);
        soIn.setDesOrgId("F002");
        soIn.setReassignRemark("改派门店");
        soIn.setOperateMid(TEST_MID);

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);
        List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(TEST_MID, "测试�?));
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(employees);
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店").build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);

        ConsultOrgChangeApplySoOut result = userConsultOperateService.submitChangeOrgApply(soIn);

        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void submitChangeOrgApply_orderNotFound_throwsBusinessException() {
        ConsultOrgChangeApplySoIn soIn = new ConsultOrgChangeApplySoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.submitChangeOrgApply(soIn));
    }

    @Test
    void submitChangeOrgApply_wrongStatus_throwsBusinessException() {
        ConsultOrgChangeApplySoIn soIn = new ConsultOrgChangeApplySoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        assertThrows(BusinessException.class, () -> userConsultOperateService.submitChangeOrgApply(soIn));
    }

    @Test
    void submitChangeOrgApply_noPermission_throwsBusinessException() {
        ConsultOrgChangeApplySoIn soIn = new ConsultOrgChangeApplySoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        soIn.setApplyOrgId(TEST_ORG_ID);
        soIn.setDesOrgId("F002");

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> userConsultOperateService.submitChangeOrgApply(soIn));
    }

    // ==================== updateHandler ====================

    @Test
    void updateHandler_success() {
        ConsultUpdateHandlerSoIn soIn = new ConsultUpdateHandlerSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        soIn.setOperatorMid(2001L);

        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        List<EmployeeInfoGoOut> dispatcherEmployees = Collections.singletonList(buildEmployeeInfoGoOut(TEST_MID, "派单�?));
        List<EmployeeInfoGoOut> receiverEmployees = Collections.singletonList(buildEmployeeInfoGoOut(2001L, "接单�?));
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(dispatcherEmployees)
                .thenReturn(receiverEmployees);
        when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);

        List<EmployeeInfoGoOut> allEmployees = Arrays.asList(
                buildEmployeeInfoGoOut(TEST_MID, "派单�?),
                buildEmployeeInfoGoOut(2001L, "接单�?));
        when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(allEmployees);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        ConsultUpdateHandlerSoOut result = userConsultOperateService.updateHandler(soIn);

        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void updateHandler_orderNotFound_throwsBusinessException() {
        ConsultUpdateHandlerSoIn soIn = new ConsultUpdateHandlerSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        soIn.setOperatorMid(2001L);
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.updateHandler(soIn));
    }

    @Test
    void updateHandler_wrongStatus_throwsBusinessException() {
        ConsultUpdateHandlerSoIn soIn = new ConsultUpdateHandlerSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        soIn.setOperatorMid(2001L);
        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);

        assertThrows(BusinessException.class, () -> userConsultOperateService.updateHandler(soIn));
    }

    @Test
    void updateHandler_noDispatchPermission_throwsBusinessException() {
        ConsultUpdateHandlerSoIn soIn = new ConsultUpdateHandlerSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOperateMid(TEST_MID);
        soIn.setOperatorMid(2001L);
        UserConsultOrderMainGoOut mainGoOut = buildMainGoOutWithStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultMainData(any(UcConsultOrderGoIn.class))).thenReturn(mainGoOut);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class))).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> userConsultOperateService.updateHandler(soIn));
    }

    // ==================== finish ====================

    @Test
    void finish_orderNotFound_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
    }

    @Test
    void finish_wrongStatus_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode());
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
    }

/*    @Test
    void finish_success_asStoreManager() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(2001L); // 跟进人不是当前操作人
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            // judgeHandlerAction: 通过门店权限校验（主�?店长�?
            EmployeeInfoGoOut employee = buildEmployeeInfoGoOut(TEST_MID, "店长");
            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenReturn(Collections.singletonList(employee));

            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);
            when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
            when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
            when(rmqGateway.mrOrderStatusFinishMessage(any(FinishOrderStatusMqMessageGoIn.class))).thenReturn(true);

            ConsultFinishSoOut result = userConsultOperateService.finish(soIn);

            assertNotNull(result);
            assertEquals("success", result.getResult());
            verify(userConsultOrderGateway).updateOrderSelective(any(UcConsultOrderUpdateGoIn.class));
            verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
            verify(rmqGateway).mrOrderStatusFinishMessage(any(FinishOrderStatusMqMessageGoIn.class));
        }
    }*/

/*    @Test
    void finish_success_asOrderFollower() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(TEST_MID); // 跟进人就是当前操作人
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            // judgeHandlerAction: 不是主管/店长，但是跟进人
            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenReturn(Collections.emptyList());

            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);
            when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
            when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
            when(rmqGateway.mrOrderStatusFinishMessage(any(FinishOrderStatusMqMessageGoIn.class))).thenReturn(true);

            ConsultFinishSoOut result = userConsultOperateService.finish(soIn);

            assertNotNull(result);
            assertEquals("success", result.getResult());
        }
    }*/

    @Test
    void finish_noPermission_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(2001L); // 跟进人不是当前操作人
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            // judgeHandlerAction: 不是主管/店长
            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenReturn(Collections.emptyList());

            BusinessException ex = assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
            assertTrue(ex.getMessage().contains("仅有主管跟店�?));
        }
    }

    @Test
    void finish_updateFails_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(TEST_MID);
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenReturn(Collections.emptyList());
            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(0);

            BusinessException ex = assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
            assertTrue(ex.getMessage().contains("结案失败"));
        }
    }

/*    @Test
    void finish_mqSendFails_stillReturnsSuccess() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(TEST_MID);
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenReturn(Collections.emptyList());
            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);
            when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
            when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
            // MQ发送失�?
            when(rmqGateway.mrOrderStatusFinishMessage(any(FinishOrderStatusMqMessageGoIn.class))).thenReturn(false);

            ConsultFinishSoOut result = userConsultOperateService.finish(soIn);

            assertNotNull(result);
            assertEquals("success", result.getResult());
        }
    }*/

    @Test
    void finish_unexpectedException_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());
        orderInfo.setOperatorMid(TEST_MID);
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setMiID(TEST_MID);
        try (MockedStatic<UserInfo> userInfoMock = mockStatic(UserInfo.class)) {
            userInfoMock.when(UserInfo::fromRpcContext).thenReturn(userInfo);

            // judgeHandlerAction内部抛出RuntimeException
            when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                    .thenThrow(new RuntimeException("unexpected error"));

            BusinessException ex = assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
            assertTrue(ex.getMessage().contains("结案异常"));
        }
    }

    @Test
    void finish_waitReceiveStatus_throwsBusinessException() {
        ConsultFinishSoIn soIn = buildConsultFinishSoIn();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> userConsultOperateService.finish(soIn));
        assertTrue(ex.getMessage().contains("咨询单状态不是待结案"));
    }

    // ==================== remindOrder ====================

    @Test
    void remindOrder_success() {
        RetailRemindOrderSoIn soIn = RetailRemindOrderSoIn.builder()
                .drNo(TEST_CONSULT_NO)
                .reminderMid(String.valueOf(TEST_MID))
                .orderRemindInfo("催单内容")
                .build();

        try (MockedStatic<RedisUtil> redisUtilMock = mockStatic(RedisUtil.class)) {
            redisUtilMock.when(() -> RedisUtil.generateRemindKey(anyString())).thenReturn("key");
            redisUtilMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(true);
            redisUtilMock.when(() -> RedisUtil.unlock(anyString())).then(invocation -> null);

            UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
            when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

            List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(TEST_MID, "催单�?));
            when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(1);
            when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

            ConsultMessageInformedStrategy mockStrategy = mock(ConsultMessageInformedStrategy.class);
            when(consultMessageInformedEventFactory.getStrategy(anyString())).thenReturn(mockStrategy);
            when(mockStrategy.createMessageInformedEvent(any(), any())).thenReturn(MessageInformedEvent.builder().build());

            RemindOrderSoOut result = userConsultOperateService.remindOrder(soIn);

            assertNotNull(result);
            assertEquals("SUCCESS", result.getResult());
        }
    }

    @Test
    void remindOrder_lockFailed_throwsBusinessException() {
        RetailRemindOrderSoIn soIn = RetailRemindOrderSoIn.builder()
                .drNo(TEST_CONSULT_NO)
                .reminderMid(String.valueOf(TEST_MID))
                .build();

        try (MockedStatic<RedisUtil> redisUtilMock = mockStatic(RedisUtil.class)) {
            redisUtilMock.when(() -> RedisUtil.generateRemindKey(anyString())).thenReturn("key");
            redisUtilMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(false);

            assertThrows(BusinessException.class, () -> userConsultOperateService.remindOrder(soIn));
        }
    }

    @Test
    void remindOrder_orderNotFound_throwsBusinessException() {
        RetailRemindOrderSoIn soIn = RetailRemindOrderSoIn.builder()
                .drNo(TEST_CONSULT_NO)
                .reminderMid(String.valueOf(TEST_MID))
                .build();

        try (MockedStatic<RedisUtil> redisUtilMock = mockStatic(RedisUtil.class)) {
            redisUtilMock.when(() -> RedisUtil.generateRemindKey(anyString())).thenReturn("key");
            redisUtilMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(true);
            redisUtilMock.when(() -> RedisUtil.unlock(anyString())).then(invocation -> null);

            when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(null);

            assertThrows(BusinessException.class, () -> userConsultOperateService.remindOrder(soIn));
        }
    }

    @Test
    void remindOrder_updateFails_throwsBusinessException() {
        RetailRemindOrderSoIn soIn = RetailRemindOrderSoIn.builder()
                .drNo(TEST_CONSULT_NO)
                .reminderMid(String.valueOf(TEST_MID))
                .orderRemindInfo("催单内容")
                .build();

        try (MockedStatic<RedisUtil> redisUtilMock = mockStatic(RedisUtil.class)) {
            redisUtilMock.when(() -> RedisUtil.generateRemindKey(anyString())).thenReturn("key");
            redisUtilMock.when(() -> RedisUtil.tryLock(anyString())).thenReturn(true);
            redisUtilMock.when(() -> RedisUtil.unlock(anyString())).then(invocation -> null);

            UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
            when(userConsultOrderGateway.searchUserConsultOrderInfo(any(UcConsultOrderGoIn.class))).thenReturn(orderInfo);

            List<EmployeeInfoGoOut> employees = Collections.singletonList(buildEmployeeInfoGoOut(TEST_MID, "催单�?));
            when(eiamRemoteGateway.getEmployeeList(any(EmployeeListGoIn.class))).thenReturn(employees);
            when(userConsultOrderGateway.updateOrderSelective(any(UcConsultOrderUpdateGoIn.class))).thenReturn(0);
            when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(false);

            assertThrows(BusinessException.class, () -> userConsultOperateService.remindOrder(soIn));
        }
    }

    // ==================== 辅助构建方法 ====================

    private CreateConsultOrderSoIn buildCreateConsultOrderSoIn() {
        CreateConsultOrderSoIn soIn = new CreateConsultOrderSoIn();
        soIn.setVid(TEST_VID);
        soIn.setSoNo("SO001");
        soIn.setSuperTicketNo("ST001");
        soIn.setContactName("测试客户");
        soIn.setContactTel("13800000000");
        soIn.setTestTag(0);
        soIn.setCreateMid(TEST_MID);
        soIn.setOperatorMid(TEST_MID);
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setOperatorPositionId(0);
        soIn.setIdempotentId("idem001");

        ConsultCreateExpandSoIn expandSoIn = ConsultCreateExpandSoIn.builder()
                .priority(4)
                .enquireType(1)
                .remark("测试问题描述")
                .orgId(TEST_ORG_ID)
                .problemCategory("售后")
                .build();
        soIn.setExpandSoIn(expandSoIn);

        IssueTypeContent issueType = new IssueTypeContent();
        issueType.setId(1);
        issueType.setName("售后问题");

        return soIn;
    }

    private OrderEditConsultSoIn buildOrderEditConsultSoIn() {
        OrderEditConsultSoIn soIn = new OrderEditConsultSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setCreateMid(TEST_MID);
        soIn.setOperatorMid(TEST_MID);
        soIn.setOperatorPositionId(0);

        ConsultCreateExpandSoIn expandSoIn = ConsultCreateExpandSoIn.builder()
                .priority(4)
                .enquireType(1)
                .remark("更新问题描述")
                .orgId(TEST_ORG_ID)
                .problemCategory("售后")
                .build();
        soIn.setExpandSoIn(expandSoIn);

        IssueTypeContent issueType = new IssueTypeContent();
        issueType.setId(1);
        issueType.setName("售后问题");

        return soIn;
    }

    private OrderAddFollowUpRecordSoIn buildOrderAddFollowUpRecordSoIn() {
        return OrderAddFollowUpRecordSoIn.builder()
                .consultNo(TEST_CONSULT_NO)
                .followUpMid(String.valueOf(TEST_MID))
                .followUpName("测试跟进�?)
                .followInfo("跟进内容")
                .build();
    }

    private ConsultReassignSoIn buildConsultReassignSoIn() {
        ConsultReassignSoIn soIn = new ConsultReassignSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setOrgId(TEST_ORG_ID);
        soIn.setOperateMid(TEST_MID);
        soIn.setReassignOperatorMid(2001L);
        soIn.setReassignOperatorPositionId(0);
        soIn.setReassignDesc("改派原因");
        return soIn;
    }

    private ConsultFinishSoIn buildConsultFinishSoIn() {
        ConsultFinishSoIn soIn = new ConsultFinishSoIn();
        soIn.setConsultNo(TEST_CONSULT_NO);
        soIn.setFinishDesc("结案描述");
        soIn.setHandleType(1);
        soIn.setOperateMid(TEST_MID);
        soIn.setApplyOrgId(TEST_ORG_ID);
        return soIn;
    }

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

    private UserConsultOrderMainGoOut buildMainGoOutWithStatus(int status) {
        UserConsultOrderMainGoOut mainGoOut = new UserConsultOrderMainGoOut();
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(status);
        mainGoOut.setUserConsultOrderInfoList(Collections.singletonList(orderInfo));
        return mainGoOut;
    }

    private EmployeeInfoGoOut buildEmployeeInfoGoOut(Long mid, String name) {
        EmployeeInfoGoOut goOut = new EmployeeInfoGoOut();
        goOut.setMiId(mid);
        goOut.setName(name);
        goOut.setEmail(name + "@xiaomi.com");
        return goOut;
    }
}
