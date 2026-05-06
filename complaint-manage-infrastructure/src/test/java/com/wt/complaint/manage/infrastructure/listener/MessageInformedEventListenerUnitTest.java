package com.wt.complaint.manage.infrastructure.listener;

import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.nr.messagehub.sdk.service.ReceiverDubboService;
import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MessageInformedEventListener 单元测试
 * 不启�?Spring，Mock Dubbo �?Eiam，通过反射注入配置字段
 */
@ExtendWith(MockitoExtension.class)
class MessageInformedEventListenerUnitTest {

    @InjectMocks
    private MessageInformedEventListener messageInformedEventListener;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private ReceiverDubboService receiverDubboService;

    @BeforeEach
    void setUp() throws Exception {
        setField("receiverDubboService", receiverDubboService);
        setField("whiteListStr", "");
        setField("onlyTestOrgIds", "");
        setField("needUpdateRequestIdComplaintNo", "");
        setField("profile", "dev");
    }

    private void setField(String name, Object value) throws Exception {
        Field f = MessageInformedEventListener.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(messageInformedEventListener, value);
    }

    @Test
    void onInformedMessageSend_pushEnumNull_returnsWithoutSend() {
        MessageInformedEvent event = MessageInformedEvent.builder()
                .requestId("req-1")
                .pushEnum(null)
                .complaintNo("C001")
                .orgId("F001")
                .build();

        messageInformedEventListener.onInformedMessageSend(event);

        verify(receiverDubboService, never()).send(any());
    }

    @Test
    void onInformedMessageSend_emptyMidAndEmail_sendReturnsEarlyWithoutDubboCall() {
        MessageInformedEvent eventEmpty = MessageInformedEvent.builder()
                .requestId("req-2")
                .pushEnum(PushEnum.APPLICATION_FOR_CLOSURE_AUDIT)
                .complaintNo("C002")
                .orgId("F001")
                .midSet(new HashSet<>())
                .emailSet(new HashSet<>())
                .appIdEnumName("app")
                .inboxEnumName("inbox")
                .nrBoxPayload(new HashMap<>())
                .miOfficePayload(new HashMap<>())
                .build();

        messageInformedEventListener.onInformedMessageSend(eventEmpty);

        verify(receiverDubboService, never()).send(any());
    }

    @Test
    void onInformedMessageSend_hasMid_callsReceiverSend() {
        Set<Long> midSet = new HashSet<>(Collections.singletonList(1001L));
        MessageInformedEvent event = MessageInformedEvent.builder()
                .requestId("req-1")
                .pushEnum(PushEnum.APPLICATION_FOR_CLOSURE_AUDIT)
                .complaintNo("C001")
                .orgId("F001")
                .midSet(midSet)
                .emailSet(new HashSet<>())
                .appIdEnumName("app")
                .inboxEnumName("inbox")
                .nrBoxPayload(new HashMap<>())
                .miOfficePayload(new HashMap<>())
                .build();

        when(receiverDubboService.send(any())).thenReturn(Result.success(true));

        messageInformedEventListener.onInformedMessageSend(event);

        verify(receiverDubboService).send(any());
    }

    @Test
    void onInformedMessageSend_receiverReturnsFail_doesNotThrow() {
        Set<Long> midSet = new HashSet<>(Collections.singletonList(1001L));
        MessageInformedEvent event = MessageInformedEvent.builder()
                .requestId("req-1")
                .pushEnum(PushEnum.APPLICATION_FOR_CLOSURE_AUDIT)
                .complaintNo("C001")
                .orgId("F001")
                .midSet(midSet)
                .emailSet(new HashSet<>())
                .appIdEnumName("app")
                .inboxEnumName("inbox")
                .nrBoxPayload(new HashMap<>())
                .miOfficePayload(new HashMap<>())
                .build();

        when(receiverDubboService.send(any())).thenReturn(Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "fail"));

        messageInformedEventListener.onInformedMessageSend(event);

        verify(receiverDubboService).send(any());
    }
}
