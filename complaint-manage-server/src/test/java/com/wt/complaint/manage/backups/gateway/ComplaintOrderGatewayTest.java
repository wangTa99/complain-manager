package com.wt.complaint.manage.backups.gateway;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * ComplaintOrderGateway集成测试
 * 测试客诉单相关Gateway接口
 * 包含客诉二期新增字段(mediaInvolved, mediaLink, upgradeTime)的查询和更新测试
 *
 * @author zhangzheyang
 * @date 2026/1/19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ComplaintOrderGatewayTest {

    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    /**
     * 测试根据客诉单号查询客诉单信�?
     * 验证新增字段(mediaInvolved, mediaLink, upgradeTime)能正确返�?
     */
    @Test
    public void testFindListByComplaintNo() {
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setComplaintNo("TS248131002388241");

        List<ComplaintOrderInfoGoIn> result = complaintOrderRepositoryGateway.findList(goIn);
        log.info("testFindListByComplaintNo result:{}", GsonUtil.toJson(result));
        
        // 检查新增字�?
        if (result != null && !result.isEmpty()) {
            ComplaintOrderInfoGoIn order = result.get(0);
            log.info("mediaInvolved: {}", order.getMediaInvolved());
            log.info("mediaLink: {}", order.getMediaLink());
            log.info("upgradeTime: {}", order.getUpgradeTime());
        }
    }

    /**
     * 测试根据客诉单号列表查询客诉单信�?
     */
    @Test
    public void testFindListByComplaintNoList() {
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setComplaintNoList(java.util.Arrays.asList("TS248131002388241", "TS248131000231762"));

        List<ComplaintOrderInfoGoIn> result = complaintOrderRepositoryGateway.findList(goIn);
        log.info("testFindListByComplaintNoList result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试根据门店ID查询客诉单信�?
     */
    @Test
    public void testFindListByOrgId() {
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setOrgId("F1039");

        List<ComplaintOrderInfoGoIn> result = complaintOrderRepositoryGateway.findList(goIn);
        log.info("testFindListByOrgId result size:{}", result != null ? result.size() : 0);
    }

    /**
     * 测试更新客诉单信�?- 包含涉媒信息字段
     * 验证新增字段(mediaInvolved, mediaLink)能正确更�?
     */
    @Test
    public void testUpdateComplaintInfoWithMediaInfo() {
        ComplaintOrderInfoGoIn updateGoIn = new ComplaintOrderInfoGoIn();
        updateGoIn.setComplaintNo("TS248131002388241");
        updateGoIn.setMediaInvolved(1);  // 是否涉媒: 1-�?
        updateGoIn.setMediaLink("https://weibo.com/test-link");

        log.info("testUpdateComplaintInfoWithMediaInfo req:{}", GsonUtil.toJson(updateGoIn));
        Boolean result = complaintOrderRepositoryGateway.updateComplaintInfo(updateGoIn);
        log.info("testUpdateComplaintInfoWithMediaInfo result:{}", result);
    }

    /**
     * 测试查询包含涉媒信息的客诉单
     */
    @Test
    public void testFindListWithMediaInfo() {
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setComplaintNo("TS248131002388241");

        List<ComplaintOrderInfoGoIn> result = complaintOrderRepositoryGateway.findList(goIn);
        
        if (result != null && !result.isEmpty()) {
            ComplaintOrderInfoGoIn order = result.get(0);
            log.info("testFindListWithMediaInfo - complaintNo: {}", order.getComplaintNo());
            log.info("testFindListWithMediaInfo - mediaInvolved: {}", order.getMediaInvolved());
            log.info("testFindListWithMediaInfo - mediaLink: {}", order.getMediaLink());
            log.info("testFindListWithMediaInfo - upgradeTime: {}", order.getUpgradeTime());
            log.info("testFindListWithMediaInfo - complaintType: {}", order.getComplaintType());
        }
    }

    /**
     * 测试根据门店ID和状态查询客诉单列表
     */
    @Test
    public void testFindListByOrgIdAndStatus() {
        OrderListGoIn goIn = new OrderListGoIn();
        goIn.setOrgId("F1039");
        goIn.setComplaintStatusList(java.util.Arrays.asList(3, 4, 5));  // 待首响、待申请结案、待结案评估

        List<ComplaintOrderInfoGoIn> result = complaintOrderRepositoryGateway.findList(goIn);
        log.info("testFindListByOrgIdAndStatus result size:{}", result != null ? result.size() : 0);
    }
}
