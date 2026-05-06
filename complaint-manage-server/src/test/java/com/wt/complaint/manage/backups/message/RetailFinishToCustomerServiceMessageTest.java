package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.RetailFinishToCustomerServiceMessage;
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
public class RetailFinishToCustomerServiceMessageTest {

    @Resource
    private RetailFinishToCustomerServiceMessage retailFinishToCustomerServiceMessage;

    @Resource
    ApplicationEventPublisher eventPublisher;


    @Test
    public void testSendMsg() throws Exception{
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        complaintBasicInfo.setDrNo("RC256481003048660");
        complaintBasicInfo.setStNo("ST256571002047224");
        complaintBasicInfo.setCustomerServiceMid(3150442576L);
        MessageInformedEvent messageInformedEvent = retailFinishToCustomerServiceMessage.createMessageInformedEvent(complaintBasicInfo,
                null);
        eventPublisher.publishEvent(messageInformedEvent);
        Thread.sleep(15000);
    }
}
