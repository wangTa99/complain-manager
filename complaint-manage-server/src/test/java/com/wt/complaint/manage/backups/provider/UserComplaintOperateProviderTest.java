package com.wt.complaint.manage.backups.provider;

import cn.hutool.core.lang.Assert;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.operate.CreateOrderReq;
import com.wt.complaint.manage.api.model.req.operate.JudgeOrderReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.RemindOrderReq;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.operate.CreateOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.JudgeOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.PickUpOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.RemindOrderResp;
import com.wt.complaint.manage.api.provider.UserComplaintOperateProvider;
import com.wt.complaint.manage.backups.BaseTest;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/23 11:03
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class UserComplaintOperateProviderTest extends BaseTest {
    @Resource
    private UserComplaintOperateProvider userComplaintOperateProvider;

    @Before
    public void init() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");
        RpcContext.getContext().setAttachment("$upc_email", "linjiehong@xiaomi.com");
        RpcContext.getContext().setAttachment("$curr_role", "car_org_manager");
    }

    @Test
    public void testCreateOrder() {
        CreateOrderReq req = GsonUtil.fromJson("{\n" +
                "  \"vid\": \"LKBQ5UA4PX3EF3GU8\",\n" +
                "  \"workType\": 25,\n" +
                "  \"soNo\": \"MKSO255022235654925\",\n" +
                "  \"superTicketNo\": \"MKST255162082005155\",\n" +
                "  \"idempotentId\": \"6926\",\n" +
                "  \"contactName\": \"尚韧初\",\n" +
                "  \"contactTel\": \"15001159987\",\n" +
                "  \"contactTitle\": 1,\n" +
                "  \"testTag\": 1,\n" +
                "  \"createMid\": 2776215961,\n" +
                "  \"expand\": {\n" +
                "    \"customerServiceMid\": 2776215961,\n" +
                "    \"carNo\": \"111\",\n" +
                "    \"complaintInfo\": [\n" +
                "      {\n" +
                "        \"groupName\": \"基本信息\",\n" +
                "        \"groupOrder\": 1,\n" +
                "        \"fields\": [\n" +
                "          {\n" +
                "            \"id\": 20,\n" +
                "            \"order\": 2,\n" +
                "            \"required\": 1,\n" +
                "            \"fieldType\": 2,\n" +
                "            \"fieldName\": \"选择门店\",\n" +
                "            \"fieldCode\": \"orgId\",\n" +
                "            \"value\": [\n" +
                "              {\n" +
                "                \"code\": \"F1038\",\n" +
                "                \"desc\": \"我是一家授权服务门店\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"attachments\": [\n" +
                "\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 149,\n" +
                "            \"order\": 1,\n" +
                "            \"required\": 1,\n" +
                "            \"fieldType\": 2,\n" +
                "            \"fieldName\": \"服务类型\",\n" +
                "            \"fieldCode\": \"serviceScene\",\n" +
                "            \"value\": [\n" +
                "              {\n" +
                "                \"code\": \"1\",\n" +
                "                \"desc\": \"过度维修\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"code\": \"2\",\n" +
                "                \"desc\": \"以次充好\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"attachments\": [\n" +
                "\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 150,\n" +
                "            \"order\": 3,\n" +
                "            \"required\": 1,\n" +
                "            \"fieldType\": 2,\n" +
                "            \"fieldName\": \"关联单号\",\n" +
                "            \"fieldCode\": \"relatedOrder\",\n" +
                "            \"value\": [\n" +
                "              {\n" +
                "                \"code\": \"ST237431002018150\",\n" +
                "                \"desc\": \"维保单号\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"groupName\": \"扩展分组\",\n" +
                "        \"groupOrder\": 2,\n" +
                "        \"fields\": [\n" +
                "          {\n" +
                "            \"id\": 21,\n" +
                "            \"order\": 8,\n" +
                "            \"required\": 1,\n" +
                "            \"fieldType\": 5,\n" +
                "            \"fieldName\": \"附件\",\n" +
                "            \"fieldCode\": \"attachments\",\n" +
                "            \"value\": [\n" +
                "              {\n" +
                "                \"code\": \"\",\n" +
                "                \"desc\": \"\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"attachments\": [\n" +
                "              {\n" +
                "                \"id\": 63607039,\n" +
                "                \"type\": 2\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}", CreateOrderReq.class);
//        CreateOrderReq req = getTestData("CreateReportOrderReq.json", CreateOrderReq.class);
        Result<CreateOrderResp> order = userComplaintOperateProvider.createOrder(req);
        Assert.notNull(order);
    }

    @Test
    public void testPickUpOrder() {
        PickUpOrderReq pickUpOrderReq = new PickUpOrderReq();
        pickUpOrderReq.setUcNo("RP256461005897017");
        Result<PickUpOrderResp> pickUpOrderRespResult = userComplaintOperateProvider.pickUpOrder(pickUpOrderReq);
        Assert.notNull(pickUpOrderRespResult);
    }

    @Test
    public void testRemindOrder() {
        RemindOrderReq req = new RemindOrderReq();
        req.setConsultNo("ZX265711004542689");
        req.setOrderRemindInfo("test一�?);
        Result<RemindOrderResp> remindOrderRespResult = userComplaintOperateProvider.remindOrder(req);
        Assert.notNull(remindOrderRespResult);
    }

    @Test
    public void testAddFollowRecord() {
        FollowRecordReq req = new FollowRecordReq();
        req.setUcNo("RP256461005897017");
        req.setFollowInfo("添加跟进记录测试");
        Result<AddFollowRecordResp> addFollowRecordRespResult = userComplaintOperateProvider.addFollowRecord(req);
        Assert.notNull(addFollowRecordRespResult);
    }

    @Test
    public void testJudgeOrder() {
        JudgeOrderReq req = new JudgeOrderReq();
        req.setUcNo("RP256461005897017");
        req.setJudgeType(2);
        req.setJudgeReason("无效");
//        req.setAttachmentList();
        Result<JudgeOrderResp> judgeOrderRespResult = userComplaintOperateProvider.judgeOrder(req);
        Assert.notNull(judgeOrderRespResult);
    }

}
