package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CronPushTaskServiceImpl 单元测试（超15天未结案 unFinishedToTimeoutCron�?
 */
@ExtendWith(MockitoExtension.class)
public class CronPushTaskServiceImplUnitTest {

    @InjectMocks
    private CronPushTaskServiceImpl cronPushTaskService;

    @Mock
    private ComplaintGateway complaintGateway;
    @Mock
    private RedisRemoteGateway redisRemoteGateway;
    @Mock
    private MessageInformedEventFactory messageInformedEventFactory;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cronPushTaskService, "repeatSendComplaintNoList", null);
    }

    @Test
    void unFinishedToTimeoutCron_EmptyList_ReturnsWithoutPublish() {
        when(complaintGateway.selectUnFinishedToTimeoutList()).thenReturn(Collections.emptyList());

        cronPushTaskService.unFinishedToTimeoutCron();

        verify(eventPublisher, never()).publishEvent(any());
        verify(redisRemoteGateway, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void unFinishedToTimeoutCron_ListWithRedisNotSet_PublishesAndSetsRedis() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        order.setId(1001L);
        List<ComplaintOrderGoOut> list = Collections.singletonList(order);
        when(complaintGateway.selectUnFinishedToTimeoutList()).thenReturn(list);
        when(redisRemoteGateway.get("unfinish_C001")).thenReturn(null);

        MessageInformedStrategy strategy = mock(MessageInformedStrategy.class);
        MessageInformedEvent event = MessageInformedEvent.builder().requestId("req1").build();
        when(strategy.createMessageInformedEvent(any(), any())).thenReturn(event);
        when(messageInformedEventFactory.getStrategy(PushConstant.UN_FINISHED_TO_TIMEOUT)).thenReturn(strategy);

        cronPushTaskService.unFinishedToTimeoutCron();

        verify(redisRemoteGateway).set(eq("unfinish_C001"), eq("1"), eq(180L), eq(TimeUnit.DAYS));
        ArgumentCaptor<MessageInformedEvent> captor = ArgumentCaptor.forClass(MessageInformedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals("req1", captor.getValue().getRequestId());
    }

    @Test
    void unFinishedToTimeoutCron_RedisAlreadySent_SkipsPublish() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        when(complaintGateway.selectUnFinishedToTimeoutList()).thenReturn(Collections.singletonList(order));
        when(redisRemoteGateway.get("unfinish_C001")).thenReturn("1");

        cronPushTaskService.unFinishedToTimeoutCron();

        verify(eventPublisher, never()).publishEvent(any());
        verify(redisRemoteGateway, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void unFinishedToTimeoutCron_RepeatSendListContainsNo_StillPublishes() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        when(complaintGateway.selectUnFinishedToTimeoutList()).thenReturn(Collections.singletonList(order));
        ReflectionTestUtils.setField(cronPushTaskService, "repeatSendComplaintNoList", "[\"C001\"]");

        MessageInformedStrategy strategy = mock(MessageInformedStrategy.class);
        when(strategy.createMessageInformedEvent(any(), any())).thenReturn(MessageInformedEvent.builder().build());
        when(messageInformedEventFactory.getStrategy(PushConstant.UN_FINISHED_TO_TIMEOUT)).thenReturn(strategy);

        cronPushTaskService.unFinishedToTimeoutCron();

        verify(eventPublisher).publishEvent(any(MessageInformedEvent.class));
    }
}
