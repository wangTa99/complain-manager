package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.DeliverRemindMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author zhangzheyang
 * @date 2025/7/1
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class DeliverRemindMessageTest {

    @Resource
    private DeliverRemindMessage deliverRemindMessage;

    @Resource
    ApplicationEventPublisher eventPublisher;

    @Test
    public void testSendMsg() throws Exception{
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        MessageInformedEvent messageInformedEvent = deliverRemindMessage.createMessageInformedEvent(complaintBasicInfo,
                null);
        eventPublisher.publishEvent(messageInformedEvent);
        Thread.sleep(15000);
    }


}
