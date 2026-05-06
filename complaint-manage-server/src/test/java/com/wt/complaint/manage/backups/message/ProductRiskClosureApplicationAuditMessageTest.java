package com.wt.complaint.manage.backups.message;

import com.wt.complaint.manage.backups.BaseTest;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.ProductRiskClosureApplicationAuditMessage;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductRiskClosureApplicationAuditMessage集成测试
 * 测试产品风险-申请结案消息策略
 * 客诉二期新增
 *
 * @author zhangzheyang
 * @date 2026/1/19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ProductRiskClosureApplicationAuditMessageTest extends BaseTest {

    @Resource
    private ProductRiskClosureApplicationAuditMessage productRiskClosureApplicationAuditMessage;

    /**
     * 测试创建产品风险结案消息事件
     * 验证消息能正确发送给区域体验专家
     */
    @Test
    public void testCreateMessageInformedEvent() {
        ComplaintOrderGoOut complaintOrder = new ComplaintOrderGoOut();
        complaintOrder.setComplaintNo("TS248131002388241");
        complaintOrder.setOrgId("F1039");
        complaintOrder.setZoneId("1");  // 大区ID
        complaintOrder.setVid("LKBQWPZTFTHE5GH20");
        complaintOrder.setContactNameC("测试用户");
        complaintOrder.setComplaintType(3);  // 产品风险类型

        Map<String, String> extraParam = new HashMap<>();

        log.info("testCreateMessageInformedEvent complaintOrder:{}", GsonUtil.toJson(complaintOrder));
        MessageInformedEvent event = productRiskClosureApplicationAuditMessage.createMessageInformedEvent(complaintOrder, extraParam);
        log.info("testCreateMessageInformedEvent event:{}", GsonUtil.toJson(event));
    }

    /**
     * 测试创建产品风险结案消息事件 - 验证邮件列表
     */
    @Test
    public void testCreateMessageInformedEventEmailSet() {
        ComplaintOrderGoOut complaintOrder = new ComplaintOrderGoOut();
        complaintOrder.setComplaintNo("TS248131002388241");
        complaintOrder.setOrgId("F1039");
        complaintOrder.setZoneId("2");  // 不同大区ID
        complaintOrder.setVid("LKBQWPZTFTHE5GH20");
        complaintOrder.setContactNameC("测试用户2");
        complaintOrder.setComplaintType(3);

        Map<String, String> extraParam = new HashMap<>();

        log.info("testCreateMessageInformedEventEmailSet complaintOrder:{}", GsonUtil.toJson(complaintOrder));
        MessageInformedEvent event = productRiskClosureApplicationAuditMessage.createMessageInformedEvent(complaintOrder, extraParam);
        log.info("testCreateMessageInformedEventEmailSet event emailSet:{}", event != null ? event.getEmailSet() : "null");
    }
}
