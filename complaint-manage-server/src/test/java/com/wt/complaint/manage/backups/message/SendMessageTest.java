package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author zhangzheyang
 * @date 2025/1/2
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class SendMessageTest {

    @Resource
    ApplicationEventPublisher eventPublisher;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;

    @Test
    public void sendApplicationForWaiverAuditMessage() {
        String complaintNo = "TS248191003051628";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    /**
     * 涉媒通知测试
     */
    @Test
    public void coolRequestSendMediaInvolvedStoreAuditMessage() throws Exception {
        String complaintNo = "TS256851090877271";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    @Test
    public void coolRequestSendProductRiskUpgradeStoreAuditMessage() throws Exception {
        String complaintNo = "TS256851110035095";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.PRODUCT_RISK_UPGRADE_AUDIT);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    @Test
    public void coolRequestSendUnFinishToTimeOutMessage() {
        String complaintNo = "TS256851092015571";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.UN_FINISHED_TO_TIMEOUT);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    /**
     * 投诉待判责审批任务通知测试
     */
    @Test
    public void coolRequestSendJudgeResponsibilityAuditMessage() throws Exception {
        String complaintNo = "TS256851067245033";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.JUDGE_RESPONSIBILITY_AUDIT);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    /**
     * 服务投诉判责结果为门店有责通知测试
     */
    @Test
    public void coolRequestSendStoreResponsibleAuditMessage() throws Exception {
        String complaintNo = "TS256851067245033";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.STORE_RESPONSIBLE_AUDIT);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    /**
     * 门店报备投诉单结案完成通知测试
     */
    @Test
    public void coolRequestSendStoreReportClosureMessage() throws Exception {
        String complaintNo = "TS256851067245033";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.STORE_REPORT_CLOSURE);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    /**
     * 提交服务投诉复盘完成通知测试
     */
    @Test
    public void coolRequestSendSubmitReviewClosureMessage() throws Exception {
        String complaintNo = "TS256851079776454";
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(PushConstant.SUBMIT_REVIEW_CLOSURE);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

}
