package com.wt.complaint.manage.backups.provider;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.wt.complaint.manage.api.model.req.ComplaintFollowUpRecordsReq;
import com.wt.complaint.manage.api.model.req.UserComplaintDetailFrameReq;
import com.wt.complaint.manage.api.model.req.UserComplaintDetailReq;
import com.wt.complaint.manage.api.model.req.UserComplaintListSearchReq;
import com.wt.complaint.manage.api.model.req.view.UcOrderInfoBatchReq;
import com.wt.complaint.manage.api.model.req.view.UcOrderLightInfoBatchReq;
import com.wt.complaint.manage.api.model.resp.ComplaintFollowUpRecordsResp;
import com.wt.complaint.manage.api.model.resp.UserComplaintDetailFrameResp;
import com.wt.complaint.manage.api.model.resp.UserComplaintDetailResp;
import com.wt.complaint.manage.api.model.resp.UserComplaintListSearchResp;
import com.wt.complaint.manage.api.model.resp.view.UcOrderInfoBatchResp;
import com.wt.complaint.manage.api.model.resp.view.UcOrderLightInfoBatchResp;
import com.wt.complaint.manage.api.provider.ComplaintViewProvider;
import com.wt.complaint.manage.api.provider.UserComplaintViewProvider;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class UserComplaintViewProviderTest {

    @Autowired
    private UserComplaintViewProvider userComplaintViewProvider;

    @Autowired
    private ComplaintViewProvider complaintViewProvider;

    @Test
    public void testSelectComplaintTimeOutList() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        List<String> roleList = new ArrayList<>();
        roleList.add("car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", roleList);
        List<Integer> orderStatusList = new ArrayList<>();
        orderStatusList.add(0);
        orderStatusList.add(1);
        orderStatusList.add(2);
        List<Integer> zoneIdList = new ArrayList<>();
        zoneIdList.add(39);
        List<Integer> cityList = new ArrayList<>();
        cityList.add(36);
        List<String> orgIdList = new ArrayList<>();
        orgIdList.add("F1039");
        UserComplaintListSearchReq req = UserComplaintListSearchReq.builder()
                .ucType(2)
                .ucNo("RP256461039568614")
                .carNo("津DK2818")
                .vin("LKBCC2MS2NK200795")
                .contactPhone("17620330896")
                .orderStatusList(orderStatusList)
                .zoneIdList(zoneIdList)
                .cityList(cityList)
                .orgIdList(orgIdList)
                .createTimeStart("2023-01-01 00:00:00")
                .createTimeEnd("2026-01-31 23:59:59")
                .finishTimeStart("2023-01-01 00:00:00")
                .finishTimeEnd("2026-01-31 23:59:59")
                .pageNum(1)
                .pageSize(10)
                .build();
        Result<UserComplaintListSearchResp> resp = userComplaintViewProvider.searchUserComplaintList(req);
        log.info("resp:{}", JSON.toJSONString(resp));
    }

    @Test
    public void testGetUserComplaintFrame() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        List<String> roleList = new ArrayList<>();
        roleList.add("car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", roleList);
        UserComplaintDetailFrameReq userComplaintDetailFrameReq =
                UserComplaintDetailFrameReq.builder().ucNo("RP256461033256849").build();
        Result<UserComplaintDetailFrameResp> userComplaintFrame =
                userComplaintViewProvider.getUserComplaintFrame(userComplaintDetailFrameReq);
        log.info("resp:{}", JSON.toJSONString(userComplaintFrame));
    }

    @Test
    public void testGetUserComplaintDetail() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
        RpcContext.getContext().setAttachment("$upc_email", "p-wangkai95@xiaomi.com");
        List<String> roleList = new ArrayList<>();
        roleList.add("car_org_manager");
        RpcContext.getContext().setAttachment("$upc_roles_list", roleList);
        UserComplaintDetailReq userComplaintDetailReq =
                UserComplaintDetailReq.builder().ucNo("RP256461039568614").build();
        Result<UserComplaintDetailResp> userComplaintDetail =
                userComplaintViewProvider.getUserComplaintDetail(userComplaintDetailReq);
        log.info("resp:{}", JSON.toJSONString(userComplaintDetail));
    }

    @Test
    public void testGetFollowUpRecords() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        ComplaintFollowUpRecordsReq req = new ComplaintFollowUpRecordsReq();
        req.setUcNo("RC256711003059367");
        Result<ComplaintFollowUpRecordsResp> followUpRecords = complaintViewProvider.getFollowUpRecords(req);
        log.info("frame:{}", GsonUtil.toJson(followUpRecords));
    }

    @Test
    public void testGetUcOrderInfo() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        List<String> ucNoList = new ArrayList<>();
        ucNoList.add("RP256461015540045");
        UcOrderInfoBatchReq req = new UcOrderInfoBatchReq();
        req.setUcNoList(ucNoList);
        Result<UcOrderInfoBatchResp> ucOrderInfo = userComplaintViewProvider.getUcOrderInfo(req);
        log.info("frame:{}", GsonUtil.toJson(ucOrderInfo));
    }

    @Test
    public void testGetUcLightInfo() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        List<String> ucNoList = new ArrayList<>();
        ucNoList.add("ST256461032013461");
        UcOrderLightInfoBatchReq req = new UcOrderLightInfoBatchReq();
        req.setBizOrderList(CollUtil.toList());
        Result<UcOrderLightInfoBatchResp> ucOrderInfo = userComplaintViewProvider.getUcOrderLightInfo(req);
        log.info("frame:{}", GsonUtil.toJson(ucOrderInfo));
    }
}
