package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.pushmessage.ComplaintMessageInformedStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class SendNewReportMessageToDealMessageTest {

    @Autowired
    private ComplaintMessageInformedEventFactory complaintMessageInformedEventFactory;

    @Autowired
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void sendNewReportMessageToDealMessage() {
        UserComplaintOrderDetailSoOut soOut =
                userComplaintOrderGateway.selectDetailByUcNo("TS255691003023170");
        ComplaintMessageInformedStrategy messageStrategy =
                complaintMessageInformedEventFactory.getStrategy(PushConstant.NEW_REPORT_TO_DEAL);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(soOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }
}
