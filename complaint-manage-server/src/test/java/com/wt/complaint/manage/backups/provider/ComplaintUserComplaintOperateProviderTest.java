package com.wt.complaint.manage.backups.provider;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.req.AddKindPointsDistributionRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.operate.*;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.api.provider.ComplaintOperateProvider;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
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

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class ComplaintUserComplaintOperateProviderTest extends BaseTest {
    @Resource
    private ComplaintOperateProvider complaintOperateProvider;
    
    @Test
    public void coolRequestCreateComplaintManageOrder() {
        String json = "{\n" +
                "    \"vid\": \"HXMQXHPLBZSMD15U5\",\n" +
                "    \"source\": 2,\n" +
                "    \"workType\": 20,\n" +
                "    \"soNo\": \"SO265102336913129\",\n" +
                "    \"superTicketNo\": \"ST265102247241054\",\n" +
                "    \"idempotentId\": \"2839681\",\n" +
                "    \"contactName\": \"林瀚城\",\n" +
                "    \"contactTel\": \"18610697596\",\n" +
                "    \"contactTitle\": 1,\n" +
                "    \"testTag\": 0,\n" +
                "    \"createMid\": 3150463116,\n" +
                "    \"expand\": {\n" +
                "        \"customerServiceMid\": \"3150463116\",\n" +
                "        \"carNo\": \"京AD03386\",\n" +
                "        \"complaintInfo\": [{\n" +
                "                \"groupName\": \"基本信息\",\n" +
                "                \"groupOrder\": 1,\n" +
                "                \"fields\": [{\n" +
                "                        \"id\": 28,\n" +
                "                        \"order\": 1,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"车型\",\n" +
                "                        \"fieldCode\": \"carModel\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"102\",\n" +
                "                                \"desc\": \"YU7 Pro\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 6,\n" +
                "                        \"order\": 2,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 3,\n" +
                "                        \"fieldName\": \"问题分类\",\n" +
                "                        \"fieldCode\": \"issueType\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"4347\",\n" +
                "                                \"desc\": \"车窗玻璃松动\",\n" +
                "                                \"pathId\": \"1/2/5/4347\",\n" +
                "                                \"pathName\": \"产品/外饰/前车�?车窗玻璃松动\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }, {\n" +
                "                        \"id\": 13,\n" +
                "                        \"order\": 3,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"门店是否跟进\",\n" +
                "                        \"fieldCode\": \"orgFollowTag\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"1\",\n" +
                "                                \"desc\": \"是\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 15,\n" +
                "                        \"order\": 5,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"风险等级\",\n" +
                "                        \"fieldCode\": \"riskLevel\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"1\",\n" +
                "                                \"desc\": \"L1\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 9,\n" +
                "                        \"order\": 6,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"服务门店\",\n" +
                "                        \"fieldCode\": \"orgId\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"F1039\",\n" +
                "                                \"desc\": \"小米汽车北京市朝阳区国贸销售服务中心\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 17,\n" +
                "                        \"order\": 7,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"投诉分类\",\n" +
                "                        \"fieldCode\": \"complaintType\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"2\",\n" +
                "                                \"desc\": \"服务投诉 \"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 16,\n" +
                "                        \"order\": 8,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 4,\n" +
                "                        \"fieldName\": \"问题详情\",\n" +
                "                        \"fieldCode\": \"problemDesc\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"\",\n" +
                "                                \"desc\": \"\\n用户问题：用户反馈主驾副驾车窗三角区处漏风之前已经在其他门店维修过了，但是没有处理好，感受非常明显，需要尽快协助处理\\n当前处理进展/结果：线上已做沟通需要进店处理，用户意向门店为国贸\\n用户反馈诉求或风险点：用户希望尽快处理完成，辛苦老师协助沟通\\n回复号码�?8610697596\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 24,\n" +
                "                        \"order\": 9,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 3,\n" +
                "                        \"fieldName\": \"投诉场景\",\n" +
                "                        \"fieldCode\": \"complaint\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"625\",\n" +
                "                                \"desc\": \"外饰\",\n" +
                "                                \"pathId\": \"600/625\",\n" +
                "                                \"pathName\": \"产品/外饰\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 135,\n" +
                "                        \"order\": 7,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"是否涉媒\",\n" +
                "                        \"fieldCode\": \"mediaInvolved\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"1\",\n" +
                "                                \"desc\": \"是\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }, {\n" +
                "                        \"id\": 135,\n" +
                "                        \"order\": 7,\n" +
                "                        \"required\": 1,\n" +
                "                        \"fieldType\": 2,\n" +
                "                        \"fieldName\": \"涉媒链接\",\n" +
                "                        \"fieldCode\": \"mediaLink\",\n" +
                "                        \"value\": [{\n" +
                "                                \"code\": \"\",\n" +
                "                                \"desc\": \"https://mi.feishu.cn/wiki/VykwwWENNiW6zyke7GCcsp1Nn5g\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"attachments\": []\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}\n";
        CreateComplaintOrderReq testData = GsonUtil.fromJson(json, CreateComplaintOrderReq.class);
        log.info("createComplaintOrder req:{}", GsonUtil.toJson(testData));
        Result<CreateComplaintOrderResp> complaintOrder = complaintOperateProvider.createComplaintOrder(testData);
        log.info("createComplaintOrder resp:{}", GsonUtil.toJson(complaintOrder));
    }

    @Test
    public void testPickUpOrder() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150430536");
        PickUpOrderReq testData = getTestData("PickUpOrderReq.json", PickUpOrderReq.class);
        log.info("PickUpOrder req:{}", GsonUtil.toJson(testData));
        Result<PickUpOrderResp> pickUpOrderRespResult = complaintOperateProvider.pickUpOrder(testData);
        log.info("PickUpOrder resp:{}", GsonUtil.toJson(pickUpOrderRespResult));
    }

    @Test
    public void testUpdateHandler() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        UpdateHandlerReq testData = getTestData("UpdateHandlerReq.json", UpdateHandlerReq.class);
        log.info("PickUpOrder req:{}", GsonUtil.toJson(testData));
        Result<UpdateHandlerResp> updateHandlerRespResult = complaintOperateProvider.updateHandler(testData);
        log.info("PickUpOrder resp:{}", GsonUtil.toJson(updateHandlerRespResult));
    }

    @Test
    public void testAddRecords() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        FollowRecordReq testData = getTestData("FollowRecordReq.json", FollowRecordReq.class);
        log.info("PickUpOrder req:{}", GsonUtil.toJson(testData));
        Result<AddFollowRecordResp> addFollowRecordRespResult = complaintOperateProvider.addFollowRecord(testData);
        log.info("PickUpOrder resp:{}", GsonUtil.toJson(addFollowRecordRespResult));
    }

    @Test
    public void testAddDistributionRecords() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        AddKindPointsDistributionRecordReq testData = new AddKindPointsDistributionRecordReq();
        testData.setComplaintNo("TS248331002026768");
        testData.setDistributionId(2314L);
        log.info("PickUpOrder req:{}", GsonUtil.toJson(testData));
        Result<AddDistributionRecordResp> addDistributionRecordRespResult = complaintOperateProvider.addKindPointsDistributionRecord(testData);
        log.info("PickUpOrder resp:{}", GsonUtil.toJson(addDistributionRecordRespResult));
    }

    @Test
    public void testRemindOrder() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150391509");
        RemindOrderReq req = new RemindOrderReq();
        req.setComplaintNo("TS248541000028418");
        req.setOrderRemindInfo("232435454656564");
        Result<RemindOrderResp> remindOrderRespResult = complaintOperateProvider.remindOrder(req);
    }

    @Test
    public void testUpdateCustomerService() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150391509");
//        UpdateCustomerServiceReq testData = getTestData("UpdateCustomerService.json", UpdateCustomerServiceReq.class);
        UpdateCustomerServiceReq testData = new UpdateCustomerServiceReq();
        CustomerServiceReq customerServiceReq = new CustomerServiceReq();
        customerServiceReq.setCustomerServiceMid(1214805526L);
        customerServiceReq.setStNo("ST256461038305234");
        testData.setCustomerServiceReqList(CollUtil.toList(customerServiceReq));

        Result<UpdateCustomerServiceResp> updateCustomerServiceRespResult = complaintOperateProvider.updateCustomerService(testData);
        log.info("UpdateCustomerService resp:{}", GsonUtil.toJson(updateCustomerServiceRespResult));
    }

    @Test
    public void coolRequestUpgradeComplaintOrder() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150463116");
        String json = "{\n" +
                "    \"complaintNo\": \"TS256851088008865\",\n" +
                "    \"targetType\": 1,\n" +
                "    \"upgradeReason\": \"测试原因\"\n" +
                "}";
        ComplaintOrderUpgradeReq testData = GsonUtil.fromJson(json, ComplaintOrderUpgradeReq.class);
        log.info("upgradeComplaint req:{}", GsonUtil.toJson(testData));
        Result<UpdateCustomerServiceResp> complaintOrder = complaintOperateProvider.upgradeComplaint(testData);
        log.info("upgradeComplaint resp:{}", GsonUtil.toJson(complaintOrder));
    }

    /**
     * 测试升级投诉 - 来源为客服工作台（CUSTOMER_SERVICE_WORKBENCH�?
     * 覆盖：升级成功后自动生成判责审批任务的分�?
     */
    @Test
    public void coolRequestUpgradeComplaintOrderFromCustomerServiceWorkbench() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150463116");
        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("TS256851059300297");
        req.setTargetType(2);
        req.setUpgradeReason("客服工作台升级测�?触发判责审批任务");
        req.setOperateSource("CUSTOMER_SERVICE_WORKBENCH");
        log.info("testUpgradeComplaintOrderFromCustomerServiceWorkbench req:{}", GsonUtil.toJson(req));
        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);
        log.info("testUpgradeComplaintOrderFromCustomerServiceWorkbench resp:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试升级投诉 - 来源为PAD（PAD_DETAIL�?
     * 覆盖：升级成功后不触发判责审批任务的分支
     */
    @Test
    public void coolRequestUpgradeComplaintOrderFromPadDetail() {
        RpcContext.getContext().setAttachment("$upc_miID", "3150463116");
        ComplaintOrderUpgradeReq req = new ComplaintOrderUpgradeReq();
        req.setComplaintNo("TS265711005895957");
        req.setTargetType(2);
        req.setUpgradeReason("客服工作台升级测�?触发判责审批任务");
        req.setOperateSource("CUSTOMER_SERVICE_WORKBENCH");
        log.info("testUpgradeComplaintOrderFromPadDetail req:{}", GsonUtil.toJson(req));
        Result<UpdateCustomerServiceResp> result = complaintOperateProvider.upgradeComplaint(req);
        log.info("testUpgradeComplaintOrderFromPadDetail resp:{}", GsonUtil.toJson(result));
    }

}
