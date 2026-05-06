package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultFinishReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultReassignReq;
import com.wt.complaint.manage.api.model.req.consult.EditConsultReq;
import com.wt.complaint.manage.api.model.req.operate.ConsultCreateExpandDTO;
import com.wt.complaint.manage.api.model.req.operate.CreateConsultReq;
import com.wt.complaint.manage.api.model.req.operate.CsEnquireInfo;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.UpdateHandlerReq;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.api.provider.UserConsultOperateProvider;
import com.wt.complaint.manage.backups.BaseTest;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.wt.complaint.manage.api.model.Attachment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wt.complaint.manage.domain.api.enums.EnquireTypeEnum.REPAIR_QUOTATION;

/**
 * UserConsultOperateProvider集成测试
 * 测试咨询单操作相关接�?
 *
 * @author linjiehong
 * @date 2025/5/23
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class UserConsultOperateProviderTest extends BaseTest {

    @Resource
    private UserConsultOperateProvider userConsultOperateProvider;

    @Before
    public void init() {
        RpcContext.getContext().setAttachment("$curr_role", MrRoleConstant.CAR_ORG_MANAGER);
        RpcContext.getContext().setAttachment("$upc_miID", "3150270561");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");
    }

    /**
     * 测试新建咨询�?
     */
    @Test
    public void testCreateOrder() {
        CreateConsultReq req = CreateConsultReq.builder()
                .vid("LKBQ5UA4PX3EF3GU8")
                .workType(25)
                .soNo("MKSO255022235654925")
                .superTicketNo("MKST255162082005155")
                .idempotentId("6926")
                .contactName("尚韧�?)
                .contactTel("15001159987")
                .contactTitle(1)
                .testTag(1)
                .createMid(2776215961L)
                .operatorMid(2776215961L)
                .operatorPositionId(1)
                .build();

        ConsultCreateExpandDTO expand = new ConsultCreateExpandDTO();
        CsEnquireInfo csEnquireInfo = new CsEnquireInfo();
        csEnquireInfo.setPriority(4);
        csEnquireInfo.setEnquireType(REPAIR_QUOTATION.getCode());
        csEnquireInfo.setRemark("咨询单测�?);
        expand.setCsEnquire(csEnquireInfo);

        log.info("testCreateOrder req:{}", GsonUtil.toJson(req));
        Result<CreateOrderResp> result = userConsultOperateProvider.createOrder(req);
        log.info("testCreateOrder result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试编辑咨询�?
     */
    @Test
    public void testEditConsult() {

/*        req:{"consultNo":"ZX265711024995087",
                "expand":{"csEnquire":{"priority":16,"enquireType":1,
                "remark":"测试暂存","expectedTouchTime":1774242785,
                "orgId":"F1039直营�?,"mrSuperTicketNo":"ST265711032460826"}}}*/
        EditConsultReq req = EditConsultReq.builder()
                .consultNo("ZX265711024995087")
                .operatorMid(2776215961L)
                .operatorPositionId(1)
                .build();

        ConsultCreateExpandDTO expand = new ConsultCreateExpandDTO();
        CsEnquireInfo csEnquireInfo = new CsEnquireInfo();
        csEnquireInfo.setPriority(16);
        csEnquireInfo.setEnquireType(REPAIR_QUOTATION.getCode());
        csEnquireInfo.setRemark("测试暂存");
        csEnquireInfo.setExpectedTouchTime(1774242785L);
        csEnquireInfo.setOrgId("F1039直营�?);
        csEnquireInfo.setMrSuperTicketNo("ST265711032460826");
        expand.setCsEnquire(csEnquireInfo);
        req.setExpand(expand);

        log.info("testEditConsult req:{}", GsonUtil.toJson(req));
        Result<EditComplaintResp> result = userConsultOperateProvider.editConsult(req);
        log.info("testEditConsult result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试咨询单接�?
     */
    @Test
    public void testPickUpOrder() {
        PickUpOrderReq req = PickUpOrderReq.builder()
                .consultNo("UC256851101565086")
                .build();

        log.info("testPickUpOrder req:{}", GsonUtil.toJson(req));
        Result<PickUpOrderResp> result = userConsultOperateProvider.pickUpOrder(req);
        log.info("testPickUpOrder result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试新增跟进记录
     */
    @Test
    public void testAddFollowRecord() {
        FollowRecordReq req = FollowRecordReq.builder()
                .consultNo("UC256851101565086")
                .followInfo("添加跟进记录测试")
                .attachmentList(Collections.emptyList())
                .build();

        log.info("testAddFollowRecord req:{}", GsonUtil.toJson(req));
        Result<AddFollowRecordResp> result = userConsultOperateProvider.addFollowRecord(req);
        log.info("testAddFollowRecord result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试申请改派门店
     */
    @Test
    public void testSubmitChangeOrgApply() {
        ConsultOrgChangeApplyReq req = ConsultOrgChangeApplyReq.builder()
                .consultNo("UC256851101565086")
                .applyOrgId("F1038")
                .desOrgId("F1039")
                .reassignRemark("申请改派门店测试")
                .build();

        log.info("testSubmitChangeOrgApply req:{}", GsonUtil.toJson(req));
        Result<ChangeOrgResp> result = userConsultOperateProvider.submitChangeOrgApply(req);
        log.info("testSubmitChangeOrgApply result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试更新处理�?
     */
    @Test
    public void testUpdateHandler() {
        UpdateHandlerReq req = UpdateHandlerReq.builder()
                .consultNo("UC256851101565086")
                .handlerMid("3150270561")
                .build();

        log.info("testUpdateHandler req:{}", GsonUtil.toJson(req));
        Result<UpdateHandlerResp> result = userConsultOperateProvider.updateHandler(req);
        log.info("testUpdateHandler result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试改派跟进�?
     */
    @Test
    public void testReassign() {
        ConsultReassignReq req = ConsultReassignReq.builder()
                .consultNo("UC256851101565086")
                .orgId("F1038")
                .reassignOperatorPositionId(1)
                .reassignOperatorMid(2776215961L)
                .reassignDesc("改派跟进人测�?)
                .attachmentList(Collections.emptyList())
                .build();

        log.info("testReassign req:{}", GsonUtil.toJson(req));
        Result<String> result = userConsultOperateProvider.reassign(req);
        log.info("testReassign result:{}", GsonUtil.toJson(result));
    }

    /**
     * 测试结案
     */
    @Test
    public void testFinish() {
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(Attachment.builder().id(63607039L).type(2).build());

        ConsultFinishReq req = ConsultFinishReq.builder()
                .consultNo("UC256851101565086")
                .applyOrgId("F1038")
                .finishDesc("结案测试描述")
                .handleType(1)
                .finishAttachmentList(attachmentList)
                .build();

        log.info("testFinish req:{}", GsonUtil.toJson(req));
        Result<String> result = userConsultOperateProvider.finish(req);
        log.info("testFinish result:{}", GsonUtil.toJson(result));
    }
}
