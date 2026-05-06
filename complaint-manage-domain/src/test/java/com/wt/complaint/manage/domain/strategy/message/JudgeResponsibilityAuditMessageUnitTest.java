package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserBaseInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JudgeResponsibilityAuditMessageUnitTest {

    @InjectMocks
    private JudgeResponsibilityAuditMessage judgeResponsibilityAuditMessage;

    @Mock
    private ComplaintAuditGateway complaintAuditGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private CarRemoteGateway carRemoteGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(judgeResponsibilityAuditMessage, "pcMainCarMaintenanceUrl", "http://pc/");
    }

    @Test
    void createMessageInformedEvent_whenNoAudit_returnsNull() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("JR-NO-AUDIT", 2);
        when(complaintAuditGateway.getRecentAuditByComplaintNo(eq("JR-NO-AUDIT"), eq(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode())))
                .thenReturn(null);

        assertNull(judgeResponsibilityAuditMessage.createMessageInformedEvent(order, new HashMap<>()));
    }

    @Test
    void createMessageInformedEvent_whenAuditExists_buildsMiOfficeMessage() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("JR-OK", 2);
        ComplaintAuditSoOut audit = new ComplaintAuditSoOut();
        audit.setId(88001L);
        when(complaintAuditGateway.getRecentAuditByComplaintNo(eq("JR-OK"), eq(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode())))
                .thenReturn(audit);

        UserBaseInfoGoOut user = new UserBaseInfoGoOut();
        user.setEmail("sat@xiaomi.com");
        when(eiamRemoteGateway.listByPositionIdAndState(anyList(), anyInt(), eq(100)))
                .thenReturn(Collections.singletonList(user));
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(StoreInfoGoOut.builder().orgName("测试�?).build());

        MessageInformedEvent event = judgeResponsibilityAuditMessage.createMessageInformedEvent(order, new HashMap<>());

        assertNotNull(event);
        assertEquals(PushEnum.JUDGE_RESPONSIBILITY_AUDIT, event.getPushEnum());
        assertTrue(event.getEmailSet().contains("sat@xiaomi.com"));
        assertTrue(event.getMiOfficePayload().get("href").contains("88001"));
    }
}
