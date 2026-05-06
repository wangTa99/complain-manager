package com.wt.complaint.manage.backups.provider;

import com.wt.complaint.manage.api.model.req.ComplaintDetailFrameReq;
import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultDetailReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;
import com.wt.complaint.manage.api.model.req.consult.StatisticsItemReq;
import com.wt.complaint.manage.api.model.resp.ComplaintDetailFrameResp;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultDetailResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultSelectorResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultStatisticsItemResp;
import com.wt.complaint.manage.api.provider.UserConsultViewProvider;
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

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * UserConsultViewProvider 集成测试
 * 测试咨询单视图相关接�?
 *
 * @author linjiehong
 * @date 2025/5/23
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class UserConsultViewProviderTest extends BaseTest {

    @Resource
    private UserConsultViewProvider userConsultViewProvider;

    @Before
    public void init() {
        RpcContext.getContext().setAttachment("$curr_role", "car_brand_representative");
        RpcContext.getContext().setAttachment("$upc_miID", "3150455859");
        RpcContext.getContext().setAttachment("$upc_email", "zhangzheyang@xiaomi.com");
    }

    /**
     * 测试咨询单详情查�?
     */

    @Test
    public void testDetail() {
        ConsultDetailReq req = ConsultDetailReq.builder()
                .consultNo("ZX256851014099802")
                .build();

        log.info("testDetail req:{}", GsonUtil.toJson(req));
        Result<ConsultDetailResp> result = userConsultViewProvider.detail(req);
        log.info("testDetail result:{}", GsonUtil.toJson(result));
    }


    @Test
    public void testGetComplaintAuth() {
        ComplaintDetailFrameReq req = new ComplaintDetailFrameReq();
        req.setComplaintNo("ZX265711005900757");
        req.setSource("PAD_DETAIL");
        log.info("testDetail req:{}", GsonUtil.toJson(req));
        Result<ComplaintDetailFrameResp> result = userConsultViewProvider.getComplaintAuth(req);
        log.info("testDetail result:{}", GsonUtil.toJson(result));
    }

}
