package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.req.DemoReq;
import com.wt.complaint.manage.api.model.req.TestMsgSend;
import com.wt.complaint.manage.api.model.resp.DemoResp;
import com.wt.complaint.manage.api.provider.DemoProvider;
import com.wt.complaint.manage.domain.api.enums.ArticleTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.DemoService;
import com.wt.complaint.manage.domain.api.service.parameter.in.DemoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.DemoSoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.proretail.newcommon.user.RpcContextUtils;
import com.wt.proretail.newcommon.util.ProviderInvokeUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huwei
 * @date 2021-06-16
 */
@Slf4j
@DubboService(timeout = 1000, group = "${dubbo.group}", version = "1.0")
@Validated
public class DemoProviderImpl implements DemoProvider {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;
    
    @Resource
    private DemoService demoService;
    
    @Override
    public Result<DemoResp> toggleLike(DemoReq req) {
        log.info("#PolicyCenterLikeCommentProviderImpl.toggleLike# request:{}", req);
        DemoSoIn demoSoIn = new DemoSoIn();
        demoSoIn.setBusinessId(req.getId());
        demoSoIn.setBusinessType(ArticleTypeEnum.POLICY.getCode());
        demoSoIn.setMino(String.valueOf(RpcContextUtils.getUserId()));
        DemoSoOut demoSoOut = demoService.toggleLike(demoSoIn);
        ProviderInvokeUtil.checkBaseParamModelSoOut(demoSoOut);
        return Result.success(DemoResp.builder().build());
    }

    @Override
    public Result<Boolean> testMsgSend(TestMsgSend req) {
        log.info("start testMsgSend req:{}", RetailJsonUtil.toJson(req));
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(req.getComplaintNo());

        MessageInformedStrategy messageStrategy =
                messageInformedEventFactory.getStrategy(req.getPushConstant());
        MessageInformedEvent messageInformedEvent;
        if (StringUtils.isNotBlank(req.getTargetOrgId())) {
            Map<String, String> extParams = new HashMap<>();
            extParams.put("targetOrgId", "F5358");
            messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                    extParams);
        } else {
            messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                    new HashMap<>());
        }
        eventPublisher.publishEvent(messageInformedEvent);
        return Result.success(true);
    }
}
