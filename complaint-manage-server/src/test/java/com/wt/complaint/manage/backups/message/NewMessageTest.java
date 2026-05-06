package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.DeliverNewComplaintMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import javax.annotation.Resource;

/**
 * 新客诉消息相关测试类
 * @author zhangzheyang
 * @date 2025/6/26
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class NewMessageTest {

    @Resource
    private DeliverNewComplaintMessage deliverNewComplaintMessage;


    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;

    @Test
    public void testDeliverNewComplaintMessage() {
        String drNo = "";
        DeliverComplaintBO complaintBO = deliverComplaintGateway.selectByDrNo(drNo);
        ComplaintBasicInfo basicInfo = new ComplaintBasicInfo();
        basicInfo.setDrNo(complaintBO.getDrNo());
        basicInfo.setZoneId(complaintBO.getZoneId());
        basicInfo.setOperatorPositionId(complaintBO.getOperatorPositionId());
        basicInfo.setOrgId(complaintBO.getOrgId());
        basicInfo.setOperatorMid(complaintBO.getOperatorMid());

        MessageInformedEvent messageInformedEvent =
                deliverNewComplaintMessage.createMessageInformedEvent(basicInfo, new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

}
