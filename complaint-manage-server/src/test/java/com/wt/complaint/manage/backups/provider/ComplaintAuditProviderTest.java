package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.req.approve.*;
import com.wt.complaint.manage.api.model.resp.approve.AuditDetailForCustomerServiceResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditDetailResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditListResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintPreNextResp;
import com.wt.complaint.manage.api.provider.ComplaintAuditProvider;
import com.wt.complaint.manage.backups.BaseTest;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ComplaintAuditProviderTest extends BaseTest {


    @Resource
    private ComplaintAuditProvider complaintAuditProvider;


    @Test
    public void searchComplaintAuditList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");

        ComplaintAuditListReq complaintAuditListReq = new ComplaintAuditListReq();


        complaintAuditListReq.setContactPhone("7baa8977901127052cba15fac981114b");

        // 创建一个审批状态列�?
        List<Integer> auditStatusList = Arrays.asList(1, 2, 3);

        complaintAuditListReq.setAuditStatusList(auditStatusList);
        complaintAuditListReq.setAuditTypeList(Arrays.asList(1, 2,3,4));
        complaintAuditListReq.setComplaintNo("TS255091003041199");
        complaintAuditListReq.setOrgIdList(Arrays.asList("F1031", "F1032"));
        complaintAuditListReq.setCarNo("LKBQWPZTFTHE5GH20");
        complaintAuditListReq.setVin("7baa8977901127052cba15fac981114b");
        complaintAuditListReq.setCreateTimeStart("2025-01-13 15:32:48");
        complaintAuditListReq.setCreateTimeEnd("2025-01-13 17:41:58");


        Result<ComplaintAuditListResp> complaintAuditListRespResult = complaintAuditProvider.searchComplaintAuditList(complaintAuditListReq);
        log.info("complaintAuditListRespResult:{}", GsonUtil.toJson(complaintAuditListRespResult));
    }



    @Test
    public void preNextAudit() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");

        ComplaintPreNextReq complaintPreNextReq = new ComplaintPreNextReq();
        complaintPreNextReq.setId(1134L);

        Result<ComplaintPreNextResp> complaintPreNextRespResult = complaintAuditProvider.preNextAudit(complaintPreNextReq);
        log.info("preNextAudit:{}", GsonUtil.toJson(complaintPreNextRespResult));
    }


    @Test
    public void submitForApproval() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");

        SubmitForApprovalReq submitForApprovalReq = new SubmitForApprovalReq();
        submitForApprovalReq.setId(1134L);
        submitForApprovalReq.setAuditType(1);
        submitForApprovalReq.setAuditStatus(1);
        Result<Boolean> result = complaintAuditProvider.submitForApproval(submitForApprovalReq);
        log.info("submitForApproval:{}", GsonUtil.toJson(result));

    }



    @Test
    public void getComplaintAuditDetail() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");


        ComplaintAuditDetailReq complaintAuditDetailReq = new ComplaintAuditDetailReq();
        complaintAuditDetailReq.setId(1134L);

        Result<ComplaintAuditDetailResp> result = complaintAuditProvider.getComplaintAuditDetail(complaintAuditDetailReq);
        log.info("getComplaintAuditDetail:{}", GsonUtil.toJson(result));
    }



    @Test
    public void getAuditDetailForCustomerService() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");

        AuditDetailForCustomerServiceReq auditDetailForCustomerServiceReq = new AuditDetailForCustomerServiceReq();
        auditDetailForCustomerServiceReq.setComplaintNo("TS255781002048991");

        Result<AuditDetailForCustomerServiceResp> result = complaintAuditProvider.getAuditDetailForCustomerService(auditDetailForCustomerServiceReq);

        log.info("getAuditDetailForCustomerService:{}", GsonUtil.toJson(result));
    }

    @Test
    public void judgeResponsibilityTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150442576");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");

        JudgeResponsibilityReq req = new JudgeResponsibilityReq();
        req.setId(1272L);
        req.setResponsible(0);
        req.setResponsibleJudgeDesc("test");

        Result<Boolean> res = complaintAuditProvider.judgeResponsibility(req);
        log.info("judgeResponsibilityTest success res:{}", GsonUtil.toJson(res));
    }

    /**
     * 测试查询审批列表 - 包含产品风险结案类型
     * 客诉二期新增auditType=5(产品风险-申请结案)
     */
    @Test
    public void searchComplaintAuditListWithProductRisk() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");

        ComplaintAuditListReq complaintAuditListReq = new ComplaintAuditListReq();
        // 包含产品风险结案类型(5)
        complaintAuditListReq.setAuditTypeList(Arrays.asList(1, 2, 3, 4, 5));
        complaintAuditListReq.setAuditStatusList(Arrays.asList(1, 2, 3));

        log.info("searchComplaintAuditListWithProductRisk req:{}", GsonUtil.toJson(complaintAuditListReq));
        Result<ComplaintAuditListResp> complaintAuditListRespResult = complaintAuditProvider.searchComplaintAuditList(complaintAuditListReq);
        log.info("searchComplaintAuditListWithProductRisk result:{}", GsonUtil.toJson(complaintAuditListRespResult));
    }

    /**
     * 测试查询审批列表 - 只查询产品风险结案类�?
     */
    @Test
    public void searchComplaintAuditListProductRiskOnly() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");

        ComplaintAuditListReq complaintAuditListReq = new ComplaintAuditListReq();
        // 只查询产品风险结案类�?5)
        complaintAuditListReq.setAuditTypeList(Arrays.asList(5));
        complaintAuditListReq.setAuditStatusList(Arrays.asList(1));  // 待审�?

        log.info("searchComplaintAuditListProductRiskOnly req:{}", GsonUtil.toJson(complaintAuditListReq));
        Result<ComplaintAuditListResp> complaintAuditListRespResult = complaintAuditProvider.searchComplaintAuditList(complaintAuditListReq);
        log.info("searchComplaintAuditListProductRiskOnly result:{}", GsonUtil.toJson(complaintAuditListRespResult));
    }

    /**
     * 测试获取审批详情 - 验证新增字段(userAgreement, vehicleRepaired)
     * 客诉二期新增字段
     */
    @Test
    public void getComplaintAuditDetailWithNewFields() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");

        ComplaintAuditDetailReq complaintAuditDetailReq = new ComplaintAuditDetailReq();
        complaintAuditDetailReq.setId(1134L);  // 使用有效的审批单ID

        Result<ComplaintAuditDetailResp> result = complaintAuditProvider.getComplaintAuditDetail(complaintAuditDetailReq);
        log.info("getComplaintAuditDetailWithNewFields result:{}", GsonUtil.toJson(result));

        // 打印新增字段
        if (result.getData() != null) {
            log.info("userAgreement: {}", result.getData().getUserAgreement());
            log.info("vehicleRepaired: {}", result.getData().getVehicleRepaired());
        }
    }

    /**
     * 免责申请审批提交
     */
    @Test
    public void coolRequestSubmitForApproval() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150463116");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "v-zhengshuiguang@xiaomi.com");

        SubmitForApprovalReq submitForApprovalReq = new SubmitForApprovalReq();
        submitForApprovalReq.setId(1401L);
        submitForApprovalReq.setAuditComment("驳回测试");

        submitForApprovalReq.setAuditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        submitForApprovalReq.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        Result<Boolean> result = complaintAuditProvider.submitForApproval(submitForApprovalReq);
        log.info("coolRequestSubmitForApproval:{}", GsonUtil.toJson(result));

    }

}
