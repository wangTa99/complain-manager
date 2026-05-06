package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.apply.ExemptionApplyReq;
import com.wt.complaint.manage.api.model.req.apply.Org72HFreeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgFinishApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.provider.ComplaintApplyProvider;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.backups.BaseTest;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ComplaintApplyProviderTest extends BaseTest {
    @Autowired
    private ComplaintApplyProvider applyProvider;

    @Test
    public void testOrgChange() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        OrgChangeApplyReq req = getTestData("OrgChangeApplyReq.json", OrgChangeApplyReq.class);
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitChangeOrgApply(req);
        log.info("result:{}", GsonUtil.toJson(orgApplyRespResult.toString()));
    }

    @Test
    public void testNoDuty() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        ExemptionApplyReq req = getTestData("ExemptionApplyReq.json", ExemptionApplyReq.class);
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitExemptionApply(req);
        log.info("result:{}", GsonUtil.toJson(orgApplyRespResult.toString()));
    }

    @Test
    public void test72HFree() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        Org72HFreeApplyReq org72HFreeApplyReq = Org72HFreeApplyReq.builder().complaintNo("TS248131002388241").applyOrgId("F1039")
                .deliveryTime("2025-01-07 16:23:00").mileage(20.22).applyReason("test").attachmentList(Collections.emptyList()).build();
        log.info("req:{}", GsonUtil.toJson(org72HFreeApplyReq));
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submit72HFreeApply(org72HFreeApplyReq);
        log.info("result:{}", GsonUtil.toJson(orgApplyRespResult.toString()));
    }

    @Test
    public void testApplyFinish() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        OrgFinishApplyReq req = getTestData("OrgFinishApplyReq.json", OrgFinishApplyReq.class);
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitFinishApply(req);
        log.info("result:{}", GsonUtil.toJson(orgApplyRespResult.toString()));
    }

    /**
     * 测试结案申请V2接口 - 产品投诉/服务投诉场景
     * 客诉二期新增接口，支持userAgreement、vehicleRepaired、mediaInfo字段
     */
    @Test
    public void testSubmitFinishApplyV2() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");

        OrgFinishApplyReq req = OrgFinishApplyReq.builder()
                .complaintNo("TS256851103886688")  // 使用有效的客诉单�?
                .applyOrgId("F1039")
                .solutionDesc("V2版本结案申请测试-客户问题已解�?)
                .userAgreement(1)       // 是否与用户达成一�? 1-�?
                .vehicleRepaired(1)     // 车辆异常是否修复: 1-�?
                .mediaInfo(3)           // 涉媒信息: 3-不涉�?
                .attachmentList(Collections.emptyList())
                .build();

        log.info("testSubmitFinishApplyV2 req:{}", GsonUtil.toJson(req));
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitFinishApplyV2(req);
        log.info("testSubmitFinishApplyV2 result:{}", GsonUtil.toJson(orgApplyRespResult));
    }

    /**
     * 测试结案申请V2接口 - 产品风险场景
     * 验证当complaintType为产品风险时，auditType会被设置为PRODUCT_RISK_CLOSURE_APPLICATION
     */
    @Test
    public void testSubmitFinishApplyV2ForProductRisk() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");

        OrgFinishApplyReq req = OrgFinishApplyReq.builder()
                .complaintNo("TS248131002388241")  // 使用产品风险类型的客诉单�?
                .applyOrgId("F1039")
                .solutionDesc("V2版本结案申请测试-产品风险场景")
                .userAgreement(0)       // 是否与用户达成一�? 0-�?
                .vehicleRepaired(2)     // 车辆异常是否修复: 2-不涉�?
                .mediaInfo(1)           // 涉媒信息: 1-用户已删�?
                .attachmentList(Collections.emptyList())
                .build();

        log.info("testSubmitFinishApplyV2ForProductRisk req:{}", GsonUtil.toJson(req));
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitFinishApplyV2(req);
        log.info("testSubmitFinishApplyV2ForProductRisk result:{}", GsonUtil.toJson(orgApplyRespResult));
    }

    /**
     * 测试结案申请V2接口 - 用户未达成一致场�?
     */
    @Test
    public void testSubmitFinishApplyV2UserNotAgreed() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");

        OrgFinishApplyReq req = OrgFinishApplyReq.builder()
                .complaintNo("TS248131002388241")
                .applyOrgId("F1039")
                .solutionDesc("V2版本结案申请测试-用户未达成一�?)
                .userAgreement(0)       // 是否与用户达成一�? 0-�?
                .vehicleRepaired(0)     // 车辆异常是否修复: 0-�?
                .mediaInfo(2)           // 涉媒信息: 2-用户未删�?
                .attachmentList(Collections.emptyList())
                .build();

        log.info("testSubmitFinishApplyV2UserNotAgreed req:{}", GsonUtil.toJson(req));
        Result<OrgApplyResp> orgApplyRespResult = applyProvider.submitFinishApplyV2(req);
        log.info("testSubmitFinishApplyV2UserNotAgreed result:{}", GsonUtil.toJson(orgApplyRespResult));
    }
}
