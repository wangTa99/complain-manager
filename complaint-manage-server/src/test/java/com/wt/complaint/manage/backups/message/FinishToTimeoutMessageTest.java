package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.DeliverFirstResponseToTimeoutMessage;
import com.wt.complaint.manage.domain.strategy.message.FinishToTimeoutMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class FinishToTimeoutMessageTest {
    @Resource
    private FinishToTimeoutMessage finishToTimeoutMessage;

    @Resource
    private DeliverFirstResponseToTimeoutMessage deliverFirstResponseToTimeoutMessage;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    ApplicationEventPublisher eventPublisher;

    @Test
    public void testNewComplaintMessageToDeal() {
        String complaintNo = "TS248191003051628";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);
        MessageInformedEvent messageInformedEvent = finishToTimeoutMessage.createMessageInformedEvent(complaintOrderGoOut, new HashMap<>());
        System.out.println(messageInformedEvent);
        assertNotNull(messageInformedEvent);
        eventPublisher.publishEvent(messageInformedEvent);
    }

    @Test
    public void testNewComplaintMessageToDeal1() {
        String complaintNo = "RC248441000051378";
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        complaintBasicInfo.setDrNo(complaintNo);
        complaintBasicInfo.setOperatorPositionId(85);
        complaintBasicInfo.setOperatorMid(3150458730L);
        complaintBasicInfo.setZoneId(3);
        complaintBasicInfo.setOrgId("F1031");

        Map<String, String> extraParam = new HashMap<>();
        MessageInformedEvent messageInformedEvent = deliverFirstResponseToTimeoutMessage.createMessageInformedEvent(complaintBasicInfo, extraParam);
        System.out.println(messageInformedEvent);
        assertNotNull(messageInformedEvent);
    }

    @Test
    public void testNewComplaintMessageToDeal2() {
        String complaintNo = "RC248441000051378";
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        complaintBasicInfo.setDrNo(complaintNo);
        complaintBasicInfo.setOperatorPositionId(85);
        complaintBasicInfo.setOperatorMid(3150458730L);
        complaintBasicInfo.setZoneId(3);
        complaintBasicInfo.setOrgId("F1031");

        Map<String, String> extraParam = new HashMap<>();
        MessageInformedEvent messageInformedEvent = deliverFirstResponseToTimeoutMessage.createMessageInformedEvent(complaintBasicInfo, extraParam);
        System.out.println(messageInformedEvent);
        assertNotNull(messageInformedEvent);
    }

    @Test
    public void testNewComplaintMessageToDeal3() {
        String complaintNo = "RC248441000051378";
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        complaintBasicInfo.setDrNo(complaintNo);
        complaintBasicInfo.setOperatorPositionId(85);
        complaintBasicInfo.setOperatorMid(3150458730L);
        complaintBasicInfo.setZoneId(3);
        complaintBasicInfo.setOrgId("F1031");

        Map<String, String> extraParam = new HashMap<>();
        MessageInformedEvent messageInformedEvent = deliverFirstResponseToTimeoutMessage.createMessageInformedEvent(complaintBasicInfo, extraParam);
        System.out.println(messageInformedEvent);
        assertNotNull(messageInformedEvent);
    }

}
