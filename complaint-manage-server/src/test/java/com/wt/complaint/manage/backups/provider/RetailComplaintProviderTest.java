package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.FileReq;
import com.wt.complaint.manage.api.model.req.retail.GetBubbleCountReq;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintDetailReq;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintListSearchReq;
import com.wt.complaint.manage.api.model.req.retail.RetailHasFirstResponseRecordFlagReq;
import com.wt.complaint.manage.api.model.req.retail.StaticRetailCountReq;
import com.wt.complaint.manage.api.model.resp.RetailHasFirstResposeRecordFlagResp;
import com.wt.complaint.manage.api.model.resp.retail.BubbleCountResp;
import com.wt.complaint.manage.api.model.resp.retail.GetSelectBasicDataResp;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintListSearchResp;
import com.wt.complaint.manage.api.model.resp.retail.StaticTabCountResp;
import com.wt.complaint.manage.api.provider.FileProvider;
import com.wt.complaint.manage.api.provider.RetailComplaintViewProvider;
import com.wt.complaint.manage.app.nrjob.RetailComplaintExpandFillNrJob;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author p-wangkai95
 * @version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class RetailComplaintProviderTest {

    @Autowired
    private RetailComplaintViewProvider retailComplaintViewProvider;

    @Autowired
    private RetailComplaintExpandFillNrJob retailComplaintExpandFillNrJob;

    @Test
    public void testGetSelectBasicData() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<GetSelectBasicDataResp> selectBasicData = retailComplaintViewProvider.getSelectBasicData();
        log.info("testGetSelectBasicData result:{}", GsonUtil.toJson(selectBasicData));
    }

    @Test
    public void testGetBubbleCount() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150469507");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<BubbleCountResp> bubbleCount =
                retailComplaintViewProvider.getBubbleCount();
//        Result<BubbleCountResp> bubbleCount =
//                retailComplaintViewProvider.getBubbleCount("F1039");
        log.info("testgetBubbleCount result:{}", GsonUtil.toJson(bubbleCount));
    }

    @Test
    public void testGetBubbleCountV2() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150469507");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<BubbleCountResp> bubbleCount =
                retailComplaintViewProvider.getBubbleCountV2(GetBubbleCountReq.builder().orgCode("F1039").build());
//        Result<BubbleCountResp> bubbleCount =
//                retailComplaintViewProvider.getBubbleCount("F1039");
        log.info("testgetBubbleCount result:{}", GsonUtil.toJson(bubbleCount));
    }

    @Test
    public void testGetStaticTabCount() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<StaticTabCountResp> staticTabCountRespResult =
                retailComplaintViewProvider.staticTabCount(
                        StaticRetailCountReq.builder().type(0).value("69").searchTerm("16620409640").build());
//        Result<StaticTabCountResp> staticTabCountRespResult =
//        retailComplaintViewProvider.staticTabCount(
//                StaticRetailCountReq.builder().orgCode("F1039").build());
        log.info("testGetStaticTabCount result:{}", GsonUtil.toJson(staticTabCountRespResult));
    }

    @Test
    public void testStaticTabCountConcurrentNPE() throws Exception {
        // 大幅增加线程数和执行次数
        int threadCount = 200;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicInteger npeCount = new AtomicInteger(0);

        // 使用线程池提高并发度
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        // 并发执行
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 增加执行次数
                    for (int j = 0; j < 500; j++) {
                        // 设置 RPC 上下�?
                        RpcContext.getContext().setAttachment("$upc_miID", "3150446340");
                        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
                        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");

                        try {
                            // 使用不同的参数值，增加数据多样�?
                            Result<StaticTabCountResp> staticTabCountRespResult =
                                    retailComplaintViewProvider.staticTabCount(
                                            StaticRetailCountReq.builder()
                                                    .type(0)
                                                    .value("69")
                                                    .searchTerm(String.valueOf(System.currentTimeMillis()))
                                                    .build());
                        } catch (NullPointerException e) {
                            // 专门捕获 NPE 异常
                            npeCount.incrementAndGet();
                            log.error("NPE detected at line 174: ", e);
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            log.error("Other error: ", e);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(300, TimeUnit.SECONDS);
        executorService.shutdown();
        log.info("Test completed. Total errors: {}, NPE count: {}", errorCount.get(), npeCount.get());

        // 验证是否复现�?NPE 问题
        if (npeCount.get() > 0) {
            log.warn("SUCCESS: NPE issue reproduced at line 174!");
        } else {
            log.info("NPE issue not reproduced. Try running the test multiple times.");
        }
    }

    @Test
    public void testSearchRetailComplaintList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<RetailComplaintListSearchResp> retailComplaintListSearchRespResult =
                retailComplaintViewProvider.searchRetailComplaintList(
                        RetailComplaintListSearchReq.builder().tab(1).type(0).value("69").searchTerm(
                                        "16620409640")
                                .pageNum(1).pageSize(10)
                                .build());
//        Result<RetailComplaintListSearchResp> retailComplaintListSearchRespResult =
//                retailComplaintViewProvider.searchRetailComplaintList(
//                        RetailComplaintListSearchReq.builder().tab(1).orgCode("F1031")
//                                .pageNum(1).pageSize(10)
//                                .build());
        log.info("testSearchRetailComplaintList result:{}", GsonUtil.toJson(retailComplaintListSearchRespResult));
    }

    @Test
    public void testGetRetailComplaintDetail() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150391509");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<RetailComplaintDetailResp> retailComplaintDetailRespResult =
                retailComplaintViewProvider.getRetailComplaintDetail(
                        RetailComplaintDetailReq.builder().drNo("DR256851040387718").orgCode("J0666")
                                .build());
        log.info("testGetRetailComplaintDetail result:{}", GsonUtil.toJson(retailComplaintDetailRespResult));
    }

    @Test
    public void testGetRetailHasFirstResposeRecordFlag() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        Result<RetailHasFirstResposeRecordFlagResp> retailComplaintDetailRespResult =
                retailComplaintViewProvider.getRetailHasFirstResposeRecordFlag(
                        RetailHasFirstResponseRecordFlagReq.builder().drNo("RC256751001072685")
                                .build());
        log.info("testGetRetailHasFirstResposeRecordFlag result:{}", GsonUtil.toJson(retailComplaintDetailRespResult));
    }

    @Resource
    BPMRemoteGateway bpmRemoteGateway;

    @Test
    public void testBpm() {
        bpmRemoteGateway.processCreate(RetailComplaintCreateBPMGoIn.builder()
                .key("complaint_apply_finish_retail")
                .name("结案申请-测试")
                .creator("3150409040")
                .extra(new HashMap<String, Object>() {{
                    put("riskLevel", "L3");
                    put("littleZone_id", "95");
                    put("bigZone_id", "69");
                }})
                .content(
                        "{\"blocks\":[{\"entities\":[{\"key\":\"contactName\",\"showName\":\"联系人姓名\",\"showValue\":\"张女士\",\"propreties\":\"inline\"},{\"key\":\"contactTel\",\"showName\":\"联系人电话\",\"showValue\":\"13088888888\",\"propreties\":\"inline\"},{\"key\":\"drNo\",\"showName\":\"投诉工单ID\",\"showValue\":\"DR123456\",\"propreties\":\"inline\"},{\"key\":\"drType\",\"showName\":\"客诉分类\",\"showValue\":\"产品投诉\",\"propreties\":\"inline\"},{\"key\":\"problemType\",\"showName\":\"问题分类\",\"showValue\":\"销�?销售过度承诺\",\"propreties\":\"inline\"},{\"key\":\"orgName\",\"showName\":\"投诉门店\",\"showValue\":\"杭州大悦城店\",\"propreties\":\"inline\"},{\"key\":\"questionDesc\",\"showName\":\"问题详情\",\"showValue\":\"杭州大悦城店阿迪斯JFK塑料袋放进卢卡斯离开洒家分厘卡JFK了洪都拉斯了解撒公开拉升DHL看几点睡啊离开感觉了客户管理了过的很快拉萨河谷看来都是=\",\"propreties\":\"block\"}]},{\"entities\":[{\"key\":\"isReconcile\",\"showName\":\"是否和解\",\"showValue\":\"否\",\"propreties\":\"inline\"},{\"key\":\"canBeRevisited\",\"showName\":\"是否可回访\",\"showValue\":\"否\",\"propreties\":\"inline\"},{\"key\":\"solutionDesc\",\"showName\":\"解决方案\",\"showValue\":\"杭州大悦城店阿迪斯JFK塑料袋放进卢卡斯离开洒家分厘卡JFK了洪都拉斯了解撒公开拉升DHL看几点睡啊离开感觉了客户管理了过的很快拉萨河谷看来都是=\",\"propreties\":\"block\"},{\"key\":\"attachmentList\",\"showName\":\"附件\",\"showValue\":\"\",\"attachmentList\":[{\"id\":3492285,\"fileName\":\"ZH76853677717264186_1_00_1740040988985_91ca4093-e3a7-447b-bb89-754de0729f98.pdf\",\"url\":\"https://staging-cnbj2-fds.api.xiaomi.net/nr-upload-car-private/E6C6BF62EFEEE5D461300ED1FB5AC79A_d35423b6-0dab-4d00-b57b-69d2c5236482.pdf?GalaxyAccessKeyId=AKD2BP4KL5GWSFRZHC&Expires=1751262716566&Signature=OeLbwJqIFUhMV6J8JNl7LX4QLqs=\",\"type\":1},{\"id\":3492283,\"fileName\":\"Get_Started_With_Smallpdf.pdf\",\"url\":\"\",\"type\":5}],\"propreties\":\"attachment\"}]}]}")
                .build());
    }

    @Resource
    private FileProvider fileProvider;

    @Test
    public void getFileTest() {
        System.out.println(fileProvider.getFileInfo(FileReq.builder()
                .fileIds(Arrays.asList(222L))
                .build()));
    }

    @Test
    public void testRetailComplaintExpandFill() {
        retailComplaintExpandFillNrJob.retailComplaintExpandFill();
    }

}
