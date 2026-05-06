package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.NewComplaintMessageToDeal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class NewComplaintMessageToDealTest {

    @Resource
    private NewComplaintMessageToDeal newComplaintMessageToDeal;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    ApplicationEventPublisher eventPublisher;

    @Test
    public void testNewComplaintMessageToDeal() {
        String complaintNo = "TS248191003051628";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);
        MessageInformedEvent messageInformedEvent = newComplaintMessageToDeal.createMessageInformedEvent(complaintOrderGoOut, new HashMap<>());
        System.out.println(messageInformedEvent);
        eventPublisher.publishEvent(messageInformedEvent);
    }
}
