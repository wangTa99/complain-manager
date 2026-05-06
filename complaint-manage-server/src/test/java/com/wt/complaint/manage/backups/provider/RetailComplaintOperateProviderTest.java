package com.wt.complaint.manage.backups.provider;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.retail.*;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.retail.CreateRetailComplaintOrderResp;
import com.wt.complaint.manage.api.provider.BpmCallBackProvider;
import com.wt.complaint.manage.api.provider.RetailComplaintOperateProvider;
import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.complaint.manage.domain.api.enums.CarChannelTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.serviceimpl.RetailComplaintViewServiceImpl;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedRequest;
import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComplaintManageBootstrap.class)
public class RetailComplaintOperateProviderTest {

    @Autowired
    private RetailComplaintOperateProvider retailComplaintOperateProvider;

    @Test
    public void createComplaintOrder() throws JsonProcessingException, InterruptedException {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        String complaintInfo =
                "{\"workType\":27,\"soNo\":\"SO256461033983658\",\"superTicketNo\":\"ST256721003044083\"," +
                        "\"idempotentId\":\"30033\",\"contactName\":\"吴爽\",\"contactTel\":\"16620409640\"," +
                        "\"contactTitle\":1,\"testTag\":0,\"createMid\":3150441833,\"expand\":{\"customerServiceMid\":\"3150441833\",\"complaintInfo\":[{\"groupName\":\"基本信息\",\"groupOrder\":1,\"fields\":[{\"id\":153,\"order\":1,\"required\":1,\"fieldType\":2,\"fieldName\":\"交付门店\",\"fieldCode\":" +
                        "\"orgId\",\"value\":[{\"code\":\"J8505\",\"desc\":\"交付店test0801\"}],\"attachments\":[]},{\"id\":154,\"order\":2,\"required\":1,\"fieldType\":2,\"fieldName\":\"销售订单\",\"fieldCode\":\"tradeOrderId\",\"value\":[{\"code\":\"5256471003023010\",\"desc\":\"5256471003023010\"}],\"attachments\":[]},{\"id\":99,\"order\":3,\"required\":1,\"fieldType\":3,\"fieldName\":\"问题分类\",\"fieldCode\":\"issueType\",\"value\":[{\"code\":\"403\",\"desc\":\"其他\",\"pathId\":\"244/245/400/403\",\"pathName\":\"销�?销售门�?门店信息/其他\"}]},{\"id\":108,\"order\":4,\"required\":1,\"fieldType\":2,\"fieldName\":\"风险等级\",\"fieldCode\":\"riskLevel\",\"value\":[{\"code\":\"3\",\"desc\":\"L3\"}],\"attachments\":[]},{\"id\":109,\"order\":5,\"required\":1,\"fieldType\":4,\"fieldName\":\"问题详情\",\"fieldCode\":\"problemDesc\",\"value\":[{\"code\":\"\",\"desc\":\"1111111111111\"}],\"attachments\":[]},{\"id\":148,\"order\":6,\"required\":1,\"fieldType\":3,\"fieldName\":\"投诉场景\",\"fieldCode\":\"\",\"value\":[{\"code\":\"135\",\"desc\":\"道路救援不满\",\"pathId\":\"133/134/135\",\"pathName\":\"服务/服务产品和权�?道路救援不满\"}],\"attachments\":[]}]}]}}";
        CreateRetailComplaintOrderReq req = JSONUtil.toBean(complaintInfo, CreateRetailComplaintOrderReq.class);
//        String complaintInfo = "{\"workType\":26,\"soNo\":\"SO256461033983658\"," +
//                "\"superTicketNo\":\"ST256721003044083\",\"F\":\"17423\",\"contactName\":\"吴爽\"," +
//                "\"contactTel\":\"16620409640\",\"contactTitle\":1,\"testTag\":0,\"idempotentId\":30032," +
//                "\"createMid\":3150441833," +
//                "\"expand\":{\"customerServiceMid\":\"3150441833\",\"complaintInfo\":[{\"groupName\":\"基本信息\",\"groupOrder\":1,\"fields\":[{\"id\":152,\"order\":1,\"required\":1,\"fieldType\":2,\"fieldName\":\"零售门店\",\"fieldCode\":" +
//                "\"orgId\",\"value\":[{\"code\":\"J8505\",\"desc\":\"北京销售子�?1\"}],\"attachments\":[]},{\"id\":154,\"order\":2,\"required\":2,\"fieldType\":2,\"fieldName\":\"销售订单\",\"fieldCode\":\"tradeOrderId\",\"value\":[{\"code\":\"5256461039320471\",\"desc\":\"5256461039320471\"}],\"attachments\":[]},{\"id\":99,\"order\":3,\"required\":1,\"fieldType\":3,\"fieldName\":\"问题分类\",\"fieldCode\":\"issueType\",\"value\":[{\"code\":\"403\",\"desc\":\"其他\",\"pathId\":\"244/245/400/403\",\"pathName\":\"销�?销售门�?门店信息/其他\"}]},{\"id\":108,\"order\":4,\"required\":1,\"fieldType\":2,\"fieldName\":\"风险等级\",\"fieldCode\":\"riskLevel\",\"value\":[{\"code\":\"2\",\"desc\":\"L2\"}],\"attachments\":[]},{\"id\":109,\"order\":5,\"required\":1,\"fieldType\":4,\"fieldName\":\"问题详情\",\"fieldCode\":\"problemDesc\",\"value\":[{\"code\":\"\",\"desc\":\"钱钱钱钱钱钱钱钱钱钱钱钱钱钱钱钱钱钱钱\"}],\"attachments\":[]},{\"id\":148,\"order\":6,\"required\":1,\"fieldType\":3,\"fieldName\":\"投诉场景\",\"fieldCode\":\"\",\"value\":[{\"code\":\"139\",\"desc\":\"产品功能导致有损失\",\"pathId\":\"136/137/139\",\"pathName\":\"产品/用户对产品不认可/产品功能导致有损失\"}],\"attachments\":[]}]}]}}";
//        CreateRetailComplaintOrderReq req = JSONUtil.toBean(complaintInfo, CreateRetailComplaintOrderReq.class);
        // 创建 ObjectMapper 对象
        Result<CreateRetailComplaintOrderResp> complaintOrder =
                retailComplaintOperateProvider.createComplaintOrder(req);
        log.info("createComplaintOrder:{}", complaintOrder);
    }

    @Test
    public void testAddFollowRecord() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150444650");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add((Attachment.builder().id(3485765L)
                .url("https://staging-cnbj2-fds.api.xiaomi.net/nr-upload-car/81D0CFEA35C0B32C35C28798376A169E_1750645855278.jpeg")
                .fileName("fc34311f-a413-4dee-a28f-11531577c324.jpeg").build()));
        Result<AddFollowRecordResp> addFollowRecordRespResult = retailComplaintOperateProvider.addFollowRecord(
                RetailFollowRecordReq.builder().drNo("RC256461011106057")
                        .followInfo("用户反馈车子还没修好,希望尽快处理")
                        .attachmentList(attachmentList).build());
        log.info("addFollowRecordRespResult:{}", addFollowRecordRespResult);
    }

    @Test
    public void submitChangeApply_test() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
//        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
//        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        RetailOrgChangeApplyReq req =
                RetailOrgChangeApplyReq.builder().drNo("RC256701001026677").applyOrgId("F1031").desOrgId("X5999")
                        .reassignRemark("申请改派测试").build();
        Result<OrgApplyResp> orgApplyRespResult = retailComplaintOperateProvider.submitChangeOrgApply(req);
        log.info("orgApplyRespResult:{}", orgApplyRespResult);
    }

    /**
     * 申请结案
     *
     * 1. 没有单号
     * 2. 没有mid
     * 3. 入参缺失
     * 4. 入参值不合法
     * 5. 错误的状�?
     * 6. 门店不对
     */
    @Test
    public void submitFinishApply_test() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150353240");
//        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
//        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        String json =
                "{\"drNo\":\"RC256721000039783\",\"applyOrgId\":\"X6018\",\"isReconcile\":0,\"canBeRevisited\":1,\"solutionDesc\":\"look what happened\",\"attachmentList\":[{\"id\":3491260,\"fileName\":\"B65AE34FEE62871F1B2CF55E452087D2_88b64d2c-e430-4a6f-b47b-55a8e6251c34.jpg\",\"type\":1},{\"id\":3491261,\"fileName\":\"4A6EE0AF5023A513E41788E3852DCE23_12a1868d-ba0b-44a2-95d5-7719a16a94b1.jpg\",\"type\":1},{\"id\":3491262,\"fileName\":\"Get_Started_With_Smallpdf.pdf\",\"type\":5}]}";
        RetailComplaintFinishApplyReq req = RetailComplaintFinishApplyReq.builder()
                .drNo("RC256461034679923")
                .isReconcile(1)
                .canBeRevisited(0)
                .solutionDesc("ABCD")
                .applyOrgId("X0536")
                .build();
//        req = GsonUtil.fromJson(json,RetailComplaintFinishApplyReq.class);
        System.err.println(retailComplaintOperateProvider.submitFinishApply(req));
    }

    @Resource
    BpmCallBackProvider callBackProvider;

    /**
     * 结案 callBack
     *
     * 1. 没有 bpmId
     * 2. 没有 operator
     * 3. finish 不是 true
     * 4. 无法幂等
     * 5. 错误的状�?
     * 6. 门店不对
     */
    @Test
    public void applyFinishedCallBack_test() {
        OnStatusChangedRequest request = new OnStatusChangedRequest();
        request.setProcessInstanceId("ce4553e5-55a5-11f0-83b8-e27fd3fa1b4e");
        request.setOperator("shenqi3");
        request.setAction(ProcessAction.Accept);
        request.setFinished(true);

        System.err.println(callBackProvider.applyFinishRetailCallback(request));
    }

    @Resource
    RetailComplaintViewServiceImpl retailComplaintViewService;

    @Test
    public void getEmployeeInfoByMid_test() {
        System.err.println(retailComplaintViewService.getEmployeeInfoByMid("3150442211"));
    }

    @Autowired
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Test
    public void getEmployeeInfoV2_test() {
        System.err.println(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(3150442211L, CarChannelTypeEnum.CAR_SALE.getCode()));
    }

    @Autowired
    private EiamRemoteGateway eiamRemoteGateway;

    @Test
    public void getEmployee_test() {
        System.err.println(eiamRemoteGateway.getEmployee(3150442211L));
    }

}
