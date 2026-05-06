package com.wt.complaint.manage.domain.strategy.consult.message;

import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StoreEmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AbstractConsultMessageInformedStrategy 单元测试
 * 通过具体子类测试抽象类中的公共方�?
 */
@ExtendWith(MockitoExtension.class)
class AbstractConsultMessageInformedStrategyUnitTest {

    @Mock
    private CarRemoteGateway carRemoteGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private ComplaintAuditGateway complaintAuditGateway;

    private TestConsultMessageInformedStrategy strategy;

    private static final String TEST_VID = "V001";
    private static final String TEST_ORG_ID = "F001";

    @BeforeEach
    void setUp() {
        strategy = new TestConsultMessageInformedStrategy();
        ReflectionTestUtils.setField(strategy, "carRemoteGateway", carRemoteGateway);
        ReflectionTestUtils.setField(strategy, "storeRemoteGateway", storeRemoteGateway);
        ReflectionTestUtils.setField(strategy, "eiamRemoteGateway", eiamRemoteGateway);
        ReflectionTestUtils.setField(strategy, "complaintAuditGateway", complaintAuditGateway);
        ReflectionTestUtils.setField(strategy, "pcMainCarMaintenanceUrl", "http://test.xiaomi.com/");
        ReflectionTestUtils.setField(strategy, "pcMainCustomerServiceUrl", "http://cs.xiaomi.com/");
    }

    // ==================== getVinByVid ====================

    @Test
    void getVinByVid_success() {
        when(carRemoteGateway.getVinByVid(TEST_VID)).thenReturn("LMWTEST1234567890");

        String result = strategy.getVinByVid(TEST_VID);

        assertEquals("LMWTEST1234567890", result);
    }

    // ==================== getCarInfoByVid ====================

    @Test
    void getCarInfoByVid_success() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vin("LMWTEST1234567890")
                .vid(TEST_VID)
                .build();
        when(carRemoteGateway.getCarInfoByVid(TEST_VID)).thenReturn(carInfo);

        CarInfoGoOut result = strategy.getCarInfoByVid(TEST_VID);

        assertNotNull(result);
        assertEquals(TEST_VID, result.getVid());
    }

    // ==================== getOrgNameByOrgId ====================

    @Test
    void getOrgNameByOrgId_success() {
        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .orgId(TEST_ORG_ID)
                .build();
        when(storeRemoteGateway.getStoreInfo(TEST_ORG_ID)).thenReturn(storeInfo);

        String result = strategy.getOrgNameByOrgId(TEST_ORG_ID);

        assertEquals("测试门店", result);
    }

    @Test
    void getOrgNameByOrgId_notFound_returnsEmpty() {
        when(storeRemoteGateway.getStoreInfo(TEST_ORG_ID)).thenReturn(null);

        String result = strategy.getOrgNameByOrgId(TEST_ORG_ID);

        assertEquals("", result);
    }

    // ==================== getMidSetByRoleAndOrg ====================

    @Test
    void getMidSetByRoleAndOrg_success() {
        List<String> roleList = Arrays.asList(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
        EmployeeInfoGoOut employee = new EmployeeInfoGoOut();
        employee.setMiId(1001L);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(employee));

        Set<Long> result = strategy.getMidSetByRoleAndOrg(roleList, TEST_ORG_ID);

        assertNotNull(result);
        assertTrue(result.contains(1001L));
    }

    @Test
    void getMidSetByRoleAndOrg_empty() {
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.emptyList());

        Set<Long> result = strategy.getMidSetByRoleAndOrg(Collections.emptyList(), TEST_ORG_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== constructNrBoxPayload ====================

    @Test
    void constructNrBoxPayload_success() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();
        when(carRemoteGateway.getCarInfoByVid(TEST_VID)).thenReturn(buildCarInfoGoOut());
        when(carRemoteGateway.getVinByVid(TEST_VID)).thenReturn("LMWTEST1234567890");

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgName("测试门店")
                .orgId(TEST_ORG_ID)
                .build();
        when(storeRemoteGateway.getStoreInfo(TEST_ORG_ID)).thenReturn(storeInfo);

        Map<String, String> result = strategy.constructNrBoxPayload(
                complaintOrder, "消息内容", AbstractConsultMessageInformedStrategy.NR_BOX_STATUS_PROGRESS_UPDATE);

        assertNotNull(result);
        assertTrue(result.containsKey("content"));
        String content = result.get("content");
        assertNotNull(content);
    }

    // ==================== getEmailList ====================

    @Test
    void getEmailList_success() {
        ZonePositionUserGoIn goIn = ZonePositionUserGoIn.builder()
                .positionId(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode())
                .bigZoneIdList(Collections.singletonList(1))
                .build();

        ZonePositionUserGoOut user = new ZonePositionUserGoOut();
        user.setEmail("user@xiaomi.com");
        when(eiamRemoteGateway.getZonePositionUser(any(ZonePositionUserGoIn.class)))
                .thenReturn(Collections.singletonList(user));

        List<String> result = strategy.getEmailList(goIn);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user@xiaomi.com", result.get(0));
    }

    @Test
    void getEmailList_empty_returnsEmptyList() {
        ZonePositionUserGoIn goIn = ZonePositionUserGoIn.builder()
                .positionId(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode())
                .build();
        when(eiamRemoteGateway.getZonePositionUser(any(ZonePositionUserGoIn.class)))
                .thenReturn(Collections.emptyList());

        List<String> result = strategy.getEmailList(goIn);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getEmailListByRoleAndOrg ====================

    @Test
    void getEmailListByRoleAndOrg_success() {
        EmployeeInfoGoOut employee = new EmployeeInfoGoOut();
        employee.setMiId(1001L);
        employee.setEmail("employee@xiaomi.com");
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(employee));

        Set<String> result = strategy.getEmailListByRoleAndOrg(
                Collections.singletonList(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey()), TEST_ORG_ID);

        assertNotNull(result);
        assertTrue(result.contains("employee@xiaomi.com"));
    }

    // ==================== getEmailListByPositionId ====================

    @Test
    void getEmailListByPositionId_success() {
        UserBaseInfoGoOut user = new UserBaseInfoGoOut();
        user.setEmail("user@xiaomi.com");
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(user))
                .thenReturn(Collections.emptyList());

        List<String> result = strategy.getEmailListByPositionId(PositionEnum.SATISFACTION_MANAGEMENT.getCode());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getEmailListByPositionId_empty_returnsEmptyList() {
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        List<String> result = strategy.getEmailListByPositionId(PositionEnum.SATISFACTION_MANAGEMENT.getCode());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmailListByPositionId_multiplePages() {
        List<UserBaseInfoGoOut> page1 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserBaseInfoGoOut user = new UserBaseInfoGoOut();
            user.setEmail("user" + i + "@xiaomi.com");
            page1.add(user);
        }
        List<UserBaseInfoGoOut> page2 = new ArrayList<>();
        UserBaseInfoGoOut lastUser = new UserBaseInfoGoOut();
        lastUser.setEmail("last@xiaomi.com");
        page2.add(lastUser);

        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), eq(1), eq(100))).thenReturn(page1);
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), eq(2), eq(100))).thenReturn(page2);

        List<String> result = strategy.getEmailListByPositionId(PositionEnum.SATISFACTION_MANAGEMENT.getCode());

        assertNotNull(result);
        assertEquals(101, result.size());
    }

    // ==================== getNrMiPushPayload ====================

    @Test
    void getNrMiPushPayload_success() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();

        Map<String, String> result = strategy.getNrMiPushPayload(
                complaintOrder,
                "测试门店",
                Collections.singletonList("ADMIN"),
                "标题",
                "描述");

        assertNotNull(result);
        assertEquals("标题", result.get("title"));
        assertEquals("描述", result.get("description"));
        assertNotNull(result.get("extra"));
    }

    @Test
    void getNrMiPushPayload_emptyReceiverRoleList() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();

        Map<String, String> result = strategy.getNrMiPushPayload(
                complaintOrder,
                "测试门店",
                Collections.emptyList(),
                "标题",
                "描述");

        assertNotNull(result);
        assertEquals("标题", result.get("title"));
    }

    // ==================== getFinalEmailSetByZoneAndPosition ====================

    @Test
    void getFinalEmailSetByZoneAndPosition_satisfactionManagement() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();
        complaintOrder.setZoneId("1");

        UserBaseInfoGoOut user = new UserBaseInfoGoOut();
        user.setEmail("sm@xiaomi.com");
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(user))
                .thenReturn(Collections.emptyList());

        Set<String> result = strategy.getFinalEmailSetByZoneAndPosition(
                complaintOrder, Collections.singletonList(PositionEnum.SATISFACTION_MANAGEMENT));

        assertNotNull(result);
        assertTrue(result.contains("sm@xiaomi.com"));
    }

    @Test
    void getFinalEmailSetByZoneAndPosition_regionalOperations() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();
        complaintOrder.setZoneId("1");

        ZonePositionUserGoOut user = new ZonePositionUserGoOut();
        user.setEmail("rom@xiaomi.com");
        when(eiamRemoteGateway.getZonePositionUser(any(ZonePositionUserGoIn.class)))
                .thenReturn(Collections.singletonList(user));

        Set<String> result = strategy.getFinalEmailSetByZoneAndPosition(
                complaintOrder, Collections.singletonList(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT));

        assertNotNull(result);
        assertTrue(result.contains("rom@xiaomi.com"));
    }

    @Test
    void getFinalEmailSetByZoneAndPosition_nonNumericZoneId_skipsZonePositions() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();
        complaintOrder.setZoneId("abc");

        Set<String> result = strategy.getFinalEmailSetByZoneAndPosition(
                complaintOrder, Collections.singletonList(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getFinalEmailSetByRoleAndOrg ====================

    @Test
    void getFinalEmailSetByRoleAndOrg_success() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();

        EmployeeInfoGoOut employee = new EmployeeInfoGoOut();
        employee.setMiId(1001L);
        employee.setEmail("store@xiaomi.com");
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(employee));

        Set<String> result = strategy.getFinalEmailSetByRoleAndOrg(
                complaintOrder, Collections.singletonList(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey()));

        assertNotNull(result);
        assertTrue(result.contains("store@xiaomi.com"));
    }

    // ==================== getMiOfficePayload ====================

    @Test
    void getMiOfficePayload_success() {
        ComplaintOrderGoOut complaintOrder = buildComplaintOrderGoOut();

        Map<String, String> result = strategy.getMiOfficePayload(complaintOrder, "测试门店");

        assertNotNull(result);
        assertEquals(complaintOrder.getComplaintNo(), result.get("complaintOrderId"));
        assertEquals("测试门店", result.get("orgName"));
        assertTrue(result.get("href").contains(complaintOrder.getComplaintNo()));
    }

    // ==================== getMidSetByPositionIdListAndOrg ====================

    @Test
    void getMidSetByPositionIdListAndOrg_success() {
        EmployeeInfoGoOut employee = new EmployeeInfoGoOut();
        employee.setMiId(1001L);
        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Collections.singletonList(employee));

        Set<Long> result = strategy.getMidSetByPositionIdListAndOrg(
                Collections.singletonList(CarEmployeeEnum.RECEIVER.getCode()), TEST_ORG_ID);

        assertNotNull(result);
        assertTrue(result.contains(1001L));
    }

    @Test
    void getMidSetByPositionIdListAndOrg_filtersNullAndZeroMids() {
        EmployeeInfoGoOut employee1 = new EmployeeInfoGoOut();
        employee1.setMiId(1001L);
        EmployeeInfoGoOut employee2 = new EmployeeInfoGoOut();
        employee2.setMiId(0L);
        EmployeeInfoGoOut employee3 = new EmployeeInfoGoOut();
        employee3.setMiId(null);

        when(eiamRemoteGateway.queryEmployeeByStore(any(StoreEmployeeListGoIn.class)))
                .thenReturn(Arrays.asList(employee1, employee2, employee3));

        Set<Long> result = strategy.getMidSetByPositionIdListAndOrg(
                Collections.singletonList(CarEmployeeEnum.RECEIVER.getCode()), TEST_ORG_ID);

        assertEquals(1, result.size());
        assertTrue(result.contains(1001L));
    }

    // ==================== 辅助构建方法 ====================

    private ComplaintOrderGoOut buildComplaintOrderGoOut() {
        ComplaintOrderGoOut goOut = new ComplaintOrderGoOut();
        goOut.setComplaintNo("CP20260301001");
        goOut.setVid(TEST_VID);
        goOut.setOrgId(TEST_ORG_ID);
        goOut.setCarNo("京A12345");
        goOut.setZoneId("1");
        goOut.setLittleZoneId("10");
        return goOut;
    }

    private CarInfoGoOut buildCarInfoGoOut() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vin("LMWTEST1234567890")
                .carType("SU7")
                .build();
        return carInfo;
    }

    /**
     * 具体测试子类，用于测试抽象类中的方法
     */
    private static class TestConsultMessageInformedStrategy extends AbstractConsultMessageInformedStrategy {
        @Override
        public MessageInformedEvent createMessageInformedEvent(UserConsultOrderInfo soOut, Map<String, String> extraParam) {
            return new MessageInformedEvent();
        }
    }
}
