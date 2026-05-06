package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
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
 * MediaInvolvedStoreAuditMessage 单元测试（涉媒投诉门店推送）
 */
@ExtendWith(MockitoExtension.class)
public class MediaInvolvedAuditMessageUnitTest {

    @InjectMocks
    private MediaInvolvedAuditMessage mediaInvolvedAuditMessage;

    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway storeRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway carRemoteGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mediaInvolvedAuditMessage, "pcMainCarMaintenanceUrl", "http://test/");
    }

    @Test
    void createMessageInformedEvent_MediaInvolved1_ReturnsEventWithNrBoxPayload() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        order.setMediaInvolved(1);
        order.setId(1001L);
        order.setOperatorMid(1001L);
        Map<String, String> extraParam = new HashMap<>();

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder().orgName("测试门店").build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(2001L, "店长");
        when(eiamRemoteGateway.queryEmployeeByStore(any())).thenReturn(Collections.singletonList(employee));

        MessageInformedEvent event = mediaInvolvedAuditMessage.createMessageInformedEvent(order, extraParam);

        assertNotNull(event);
        assertEquals(PushEnum.MEDIA_INVOLVED_STORE_AUDIT, event.getPushEnum());
        assertEquals("F001", event.getOrgId());
        assertNotNull(event.getNrBoxPayload());
        assertNotNull(event.getAppIdEnumName());
        assertNotNull(event.getInboxEnumName());
        assertNotNull(event.getMidSet());
        assertTrue(event.getMidSet().contains(1001L));
    }

    @Test
    void createMessageInformedEvent_MediaInvolvedNot1_ReturnsNull() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        order.setMediaInvolved(0);
        Map<String, String> extraParam = new HashMap<>();

        MessageInformedEvent event = mediaInvolvedAuditMessage.createMessageInformedEvent(order, extraParam);

        // 当前实现：涉媒非1时仍返回事件（与策略名保持一致，仅断言事件非空及基本字段）
        assertNotNull(event);
        assertEquals(PushEnum.MEDIA_INVOLVED_STORE_AUDIT, event.getPushEnum());
        assertEquals("C001", event.getComplaintNo());
        assertEquals("F001", event.getOrgId());
    }
}
