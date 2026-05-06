package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * UnFinishedToTimeoutMessage 单元测试（超15天未结案提醒�?
 */
@ExtendWith(MockitoExtension.class)
public class UnFinishedToTimeoutMessageUnitTest {

    @InjectMocks
    private UnFinishedToTimeoutMessage unFinishedToTimeoutMessage;

    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway storeRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway eiamRemoteGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(unFinishedToTimeoutMessage, "pcMainCarMaintenanceUrl", "http://test/");
    }

    @Test
    void createMessageInformedEvent_ReturnsEventWithCorrectFields() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        order.setZoneId("1");
        order.setLittleZoneId("10");
        order.setId(1001L);
        Map<String, String> extraParam = new HashMap<>();

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder().orgName("测试门店").build();
        when(storeRemoteGateway.getStoreInfo(eq("F001"))).thenReturn(storeInfo);

        ZonePositionUserGoOut user = new ZonePositionUserGoOut();
        user.setEmail("expert@test.com");
        when(eiamRemoteGateway.getZonePositionUser(any(ZonePositionUserGoIn.class)))
                .thenReturn(Collections.singletonList(user));

        MessageInformedEvent event = unFinishedToTimeoutMessage.createMessageInformedEvent(order, extraParam);

        assertNotNull(event);
        assertEquals(PushEnum.UN_FINISHED_TO_TIMEOUT, event.getPushEnum());
        assertEquals("F001", event.getOrgId());
        // auth 可能�?null，避�?NPE
        assertTrue(event.getAuth() == null || event.getAuth());
        assertNotNull(event.getRequestId());
        assertNotNull(event.getEmailSet());
        assertNotNull(event.getMiOfficePayload());
        assertTrue(event.getMiOfficePayload().containsKey("complaintOrderId"));
        assertEquals("C001", event.getMiOfficePayload().get("complaintOrderId"));
    }
}
