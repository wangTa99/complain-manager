package com.wt.complaint.manage.backups.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wt.complaint.manage.api.provider.DeliverComplaintProvider;
import com.wt.complaint.manage.api.model.req.deliver.*;
import com.wt.complaint.manage.api.model.resp.deliver.*;
import com.wt.complaint.manage.app.nrjob.DeliverComplaintListNrJob;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author huxiankang
 * @date 2025/6/13
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class DeliverComplaintProviderTest {

    @Autowired
    private DeliverComplaintProvider deliverComplaintProvider;
    @Autowired
    private DeliverComplaintListNrJob deliverComplaintListNrJob;


    @Test
    public void queryStatisticsItemsTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        StatisticsItemReq req = new StatisticsItemReq();
        req.setOrgIds("J8193,J0666");
        Result<StatisticsItemResp> result = deliverComplaintProvider.queryStatisticsItems(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void listTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"JFYYZY\"]");
        DeliverComplaintListReq req = new DeliverComplaintListReq();
        req.setPageSize(50);
        req.setPageNum(1);
        Result<DeliverComplaintListResp> result = deliverComplaintProvider.list(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void exportTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_email", "p-huxiankang@xiaomi.com");
        DeliverComplaintListReq req = new DeliverComplaintListReq();
        Result<DeliverComplaintExportResp> result = deliverComplaintProvider.export(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void deliverComplaintListExportHandlerTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_email", "p-huxiankang@xiaomi.com");
        deliverComplaintListNrJob.deliverComplaintListExportHandler();
    }

    @Test
    public void detailTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        DeliverComplaintDetailReq req = new DeliverComplaintDetailReq();
        req.setDrNo("DR256851073747943");
        Result<DeliverComplaintDetailResp> result = deliverComplaintProvider.detail(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }


    @Test
    public void followUpTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"JFYYZY\"]");
        DeliverComplaintFollowUpReq req = new DeliverComplaintFollowUpReq();
        Result<String> result = deliverComplaintProvider.followUp(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void startProcessTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"JFYYZY\"]");
        DeliverComplaintStartProcessReq req = new DeliverComplaintStartProcessReq();
        Result<String> result = deliverComplaintProvider.startProcess(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void queryReassignEmployeeTest() {
        QueryReassignEmployeeReq req = new QueryReassignEmployeeReq();
        req.setOrgId("J8395");
        req.setPositionId(466);
        Result<List<QueryReassignEmployeeResp>> result = deliverComplaintProvider.queryReassignEmployee(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void reassignTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        DeliverComplaintReassignReq req = new DeliverComplaintReassignReq();
        Result<String> result = deliverComplaintProvider.reassign(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    @Test
    public void finishTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        DeliverComplaintFinishReq req = new DeliverComplaintFinishReq();
        Result<String> result = deliverComplaintProvider.finish(req);
        log.info("res:{}", new GsonBuilder().setPrettyPrinting().create().toJson(result));
    }

    /**
     *  申请免责
     *
     *  1. 没有mid
     *  2. 角色不对
     *  3. 单号不对
     *  4. 客诉尚未结案
     *  5. 反复申请
     *  6. 没传文件
     *  7. 超过 48 小时申请
     *  8. 成功申请
     */
    @Test
    public void applyExemptionTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"JFYYZY\"]");
        System.err.println(deliverComplaintProvider.applyExemption(DeliverComplaintApplyExemptionReq.builder()
                                                                                                    .drNo("DR12345")
                                                                                                    .exemptionReason("BCD")
                                                                                                    .build()));
    }

    /**
     *  判责
     *  1. 没有mid
     *  2. 角色不对
     *  3. 单号不对
     *  4. 客诉尚未结案
     *  5. 反复判责
     *  6. 没申请判�?
     *  7. 成功判责
     */
    @Test
    public void judgeResponsibleTest() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150458730");
        RpcContext.getContext().setAttachment("$upc_roles_list", "[\"JFYYZY\"]");
        System.err.println(deliverComplaintProvider.judgeResponsible(DeliverComplaintJudgeReq.builder()
                                                                                             .drNo("DR12345")
                                                                                             .responsible(1)
                                                                                             .responsibleJudgeDesc("ABC")
                                                                                             .build()));
    }
}
