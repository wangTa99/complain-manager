package com.wt.complaint.manage.backups.provider;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.resp.*;
import com.wt.complaint.manage.api.provider.ComplaintViewProvider;
import com.wt.complaint.manage.app.nrjob.ComplaintListRecordNrJob;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.backups.BaseTest;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintOrderMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ComplaintViewProviderTest extends BaseTest {

    @Autowired
    private ComplaintViewProvider complaintViewProvider;

    @Autowired
    private ComplaintOrderMapper complaintOrderMapper;

    @Autowired
    private ComplaintListRecordNrJob complaintListRecordNrJob;

    @Autowired
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Test
    public void selectComplaintTimeOutList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        List<ComplaintOrderDO> complaintTimeOutList = complaintOrderMapper.selectComplaintTimeoutList();

        System.out.println("==========================");
        System.out.println("==========================");
        System.out.println("==========================");
        System.out.println("==========================");
        System.out.println("==========================");
        System.out.println("==========================");
        System.out.println(JSON.toJSONString(complaintTimeOutList));
        log.info("complaintListSearchGoInList:{}", JSON.toJSONString(complaintTimeOutList));
        if (CollUtil.isEmpty(complaintTimeOutList)) {
            return;
        }
    }

    @Test
    public void countComplaintListTab() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        RpcContext.getContext().setAttachment("$curr_role", "programmer");
        String reqJson =
                "{\"tab\":4,\"pageSize\":10,\"source\":\"PAD_LIST\",\"pageNum\":1,\"orgId\":\"F0910\"," +
                        "\"onlyShowMyCompositeOrder\":true}";
        ComplaintListSearchReq req = GsonUtil.fromJson(reqJson, ComplaintListSearchReq.class);
        var result = complaintViewProvider.countComplaintListTab(req);
        log.info(GsonUtil.toJson(result));
    }

    @Test
    public void testSearchComplaintListForPadList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150446206");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        ComplaintListSearchReq req = new ComplaintListSearchReq();
        req.setSource(SourceEnum.PAD_LIST.getCode());
        req.setOrgId("F1031");
        req.setComplaintNo("TS248131001812480");
        //tab, 1-全部, 2-待接�? 3-处理�? 4-即将超时, 5-待结案评�? 6-仅查�? 7-已结�?
        req.setTab(4);
        req.setPageNum(1);
        req.setPageSize(100);

//        req.setCreateTimeStart( DateUtil.hoursAgo(8));
//        req.setCreateTimeEnd(DateUtil.hoursAgo(12));
//        req.setOnlyShowMyCompositeOrder(false);
        var result = complaintViewProvider.searchComplaintList(req);
        log.info(GsonUtil.toJson(result));
    }

    @Test
    public void testSearchComplaintListForAfterSaleWorkbench() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
//        RpcContext.getContext().setAttachment("$curr_role", "programmer");
        RpcContext.getContext().setAttachment("$upc_roles_list", "car_org_manager");
//        RpcContext.getContext().setAttachment("$upc_userName", "小张");
//        RpcContext.getContext().setAttachment("$upc_account", "3153009340");
        ComplaintListSearchReq req = new ComplaintListSearchReq();
        req.setSource(SourceEnum.AFTER_SALE_WORKBENCH.getCode());
//        req.setComplaintNo("TS248131002388241");
        List<Integer> zoneIdList = new ArrayList<>();
        zoneIdList.add(39);
        zoneIdList.add(50);
        req.setZoneIdList(zoneIdList);
        req.setReminderTimes(2);
//        req.setProblemDesc("小记测试");
//        req.setVin("LKBCC2MS2NK201863x");
//        req.setContactPhone("13245678923");
//        ComplaintListSearchReq req = GsonUtil.fromJson(reqJson, ComplaintListSearchReq.class);
//        List<String> cityList = new ArrayList<>();
//        cityList.add("36");
//        req.setCityList(cityList);
//        List<String> orgIdList = new ArrayList<>();
//        orgIdList.add("36");
//        req.setOrgIdList(orgIdList);
//        List<String> tagList = new ArrayList<>();
//        tagList.add("FINISH_72H_ASSESSMENT_FREE");
//        req.setTagList(tagList);
//        List<Integer> riskLevelList = new ArrayList<>();
//        riskLevelList.add(1);
//        req.setRiskLevelList(riskLevelList);
//        req.setCreateTimeStart("2024-12-23 14:45:00");
//        req.setCreateTimeEnd("2024-12-29 14:45:00");
//        req.setFirstResponseTimeStart("2024-12-23 14:45:00");
//        req.setFirstResponseTimeEnd("2024-12-28 14:45:00");
//        req.setContactPhone("18636334856");
        var result = complaintViewProvider.searchComplaintList(req);
        log.info(GsonUtil.toJson(result));
    }

    @Test
    public void testSearchComplaintListForPadRelateList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150442576");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
//        RpcContext.getContext().setAttachment("$curr_role", "");
//        RpcContext.getContext().setAttachment("$upc_roles_list","");
//        RpcContext.getContext().setAttachment("$upc_userName", "小张");
//        RpcContext.getContext().setAttachment("$upc_account", "3150309340");
        ComplaintListSearchReq req = new ComplaintListSearchReq();
        req.setSource(SourceEnum.PAD_RELATE_LIST.getCode());
        req.setOrgId("F1039");
        req.setVin("LKBCC2MS2NK200795");
        var result = complaintViewProvider.searchComplaintList(req);
        log.info(GsonUtil.toJson(result));
    }

    @Test
    public void testGetComplaintFrameInfo() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150442576");
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("TS256851051188563");
        req.setSource("AFTER_SALE_WORKBENCH");
        Result<ComplaintDetailFrameResp> complaintFrame = complaintViewProvider.getComplaintFrame(req);
        log.info("frame:{}", GsonUtil.toJson(complaintFrame));
        System.err.println(complaintFrame.getData().getUserActionAuth().getActionsList().stream().sorted().collect(Collectors.toList()));
        System.err.println(complaintFrame.getData().getUserActionAuth().getButtons().stream().sorted().collect(Collectors.toList()));

    }

    @Test
    public void coolRequestGetComplaintDetail() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150302692");
        ComplaintDetailReq req = new ComplaintDetailReq();
        req.setComplaintNo("TS256851030333211");
        Result<ComplaintDetailResp> complaintDetail = complaintViewProvider.getComplaintDetail(req);
        log.info("frame:{}", GsonUtil.toJson(complaintDetail));
    }

    @Test
    public void testBatchGetComplaintDetail() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150302692");
        ComplaintDetailBatchReq req = new ComplaintDetailBatchReq();
        req.setComplaintNoList(Arrays.asList("DR256461032691323"));
        Result<ComplaintDetailBatchResp> complaintDetailBatchRespResult =
                complaintViewProvider.batchGetComplaintDetail(req);
        log.info("frame:{}", GsonUtil.toJson(complaintDetailBatchRespResult));
    }

    @Test
    public void testGetFollowUpRecords() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150302692");
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();
        req.setUcNo("TS257921002022826");
        Result<ComplaintFollowUpRecordsResp> followUpRecords = complaintViewProvider.getFollowUpRecords(req);
        log.info("frame:{}", GsonUtil.toJson(followUpRecords));
    }

    @Test
    public void testGetHandlerList() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150302692");
        ComplaintHandlerListReq req = new ComplaintHandlerListReq();
        req.setOrgId("F1039");
        Result<ComplaintHandlerListResp> complaintHandlerList = complaintViewProvider.getComplaintHandlerList(req);
        log.info("frame:{}", GsonUtil.toJson(complaintHandlerList));
    }

    @Test
    public void testEncrypt() {
        log.info(KeyCenterUtil.encrypt("13245678923"));
        log.info(KeyCenterUtil.encrypt("13245678923"));
    }

    @Test
    public void testExportComplaintList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        List<String> roleList = new ArrayList<>();
        roleList.add("car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", roleList);
        ComplaintListSearchReq req = new ComplaintListSearchReq();
        req.setSource(SourceEnum.AFTER_SALE_WORKBENCH.getCode());
        req.setPageSize(1);
        req.setPageNum(10);
        req.setCreateTimeStart("2024-12-23 14:45:00");
        req.setCreateTimeEnd("2025-12-29 14:45:00");
        log.info("req={}", GsonUtil.toJson(req));
        Result<ComplaintListExportRes> export = complaintViewProvider.exportComplaintList(req);
        log.info("export={}", JSONUtil.toJsonStr(export));
    }

    @Test
    public void testExportComplaintListRecord() {
        complaintListRecordNrJob.exportComplaintList();
    }

    @Test
    public void test() {
        CarEmployeeInfoGoOut employeeInfoV2 = carEmployeeRemoteGateway.getEmployeeInfoV2(3150447733L);
        log.info("employeeInfoV2={}", JSONUtil.toJsonStr(employeeInfoV2));
    }
}
