package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.domain.api.enums.AppIdEnum;
import com.wt.complaint.manage.domain.api.enums.InboxEnum;
import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserBaseInfoGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 客诉三期：门店报备结案、门店有责通知、复盘完成飞书通知等消息策�?
 */
@ExtendWith(MockitoExtension.class)
class ComplaintNotifyPhaseThreeMessagesUnitTest {

    @InjectMocks
    private StoreReportClosureMessage storeReportClosureMessage;
    @InjectMocks
    private StoreResponsibleAuditMessage storeResponsibleAuditMessage;
    @InjectMocks
    private SubmitReviewClosureMessage submitReviewClosureMessage;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private CarRemoteGateway carRemoteGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storeReportClosureMessage, "pcMainCarMaintenanceUrl", "http://pc/");
        ReflectionTestUtils.setField(storeResponsibleAuditMessage, "pcMainCarMaintenanceUrl", "http://pc/");
        ReflectionTestUtils.setField(submitReviewClosureMessage, "pcMainCarMaintenanceUrl", "http://pc/");
    }

    private void stubStoreAndCarBasics() {
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(StoreInfoGoOut.builder().orgName("门店X").build());
        when(carRemoteGateway.getVinByVid(anyString())).thenReturn("");
        when(carRemoteGateway.getCarInfoByVid(anyString())).thenReturn(null);
    }

    @Test
    void storeReportClosureMessage_buildsRetailNotifications() {
        stubStoreAndCarBasics();
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("SR-1", 2);
        order.setId(7001L);
        order.setCarNo("京A00001");
        when(eiamRemoteGateway.queryEmployeeByStore(any()))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(4001L, "店长")));
        when(eiamRemoteGateway.getEmployeeList(any()))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(4001L, "店长")));

        MessageInformedEvent event = storeReportClosureMessage.createMessageInformedEvent(order, new HashMap<>());

        assertNotNull(event);
        assertEquals(PushEnum.STORE_REPORT_CLOSURE, event.getPushEnum());
        assertEquals(AppIdEnum.NEW_RETAIL_PAD.getName(), event.getAppIdEnumName());
        assertEquals(InboxEnum.RETAI_COMPLAINT_NOTICE.getName(), event.getInboxEnumName());
        assertNotNull(event.getNrBoxPayload());
        assertFalse(event.getMidSet().isEmpty());
    }

    @Test
    void storeResponsibleAuditMessage_includesOperatorMidWhenValid() {
        stubStoreAndCarBasics();
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("SRP-1", 2);
        order.setId(8002L);
        order.setOperatorMid(5005L);
        order.setCarNo("京B00002");
        when(eiamRemoteGateway.queryEmployeeByStore(any()))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(4002L, "�?)));
        when(eiamRemoteGateway.getEmployeeList(any()))
                .thenReturn(Collections.singletonList(TestDataBuilder.buildEmployeeInfoGoOut(4002L, "�?)));

        MessageInformedEvent event = storeResponsibleAuditMessage.createMessageInformedEvent(order, new HashMap<>());

        assertNotNull(event);
        assertEquals(PushEnum.STORE_RESPONSIBLE_AUDIT, event.getPushEnum());
        assertTrue(event.getMidSet().contains(5005L));
        assertNotNull(event.getMiOfficePayload().get("href"));
    }

    @Test
    void submitReviewClosureMessage_mergesRegionAndSatisfactionEmails() {
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(StoreInfoGoOut.builder().orgName("门店Y").build());
        when(eiamRemoteGateway.getZonePositionUser(any())).thenReturn(Collections.emptyList());
        UserBaseInfoGoOut sat = new UserBaseInfoGoOut();
        sat.setEmail("satisfaction@xiaomi.com");
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), anyInt(), eq(100)))
                .thenReturn(Collections.singletonList(sat));

        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("REV-1", 2);
        order.setId(9003L);

        MessageInformedEvent event = submitReviewClosureMessage.createMessageInformedEvent(order, new HashMap<>());

        assertNotNull(event);
        assertEquals(PushEnum.SUBMIT_REVIEW_CLOSURE, event.getPushEnum());
        assertTrue(event.getEmailSet().contains("satisfaction@xiaomi.com"));
        assertEquals("门店Y", event.getMiOfficePayload().get("orgName"));
    }
}
