package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.DemoGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.DemoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DemoGoOut;
import com.wt.complaint.manage.infrastructure.outer.rpc.DemoInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 点赞评论网关层实�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
@Slf4j
@Service
public class DemoGatewayImpl implements DemoGateway {
    
    /**
     * 点赞评论通用服务代理
     */
    @Resource
    private DemoInvoker demoInvoker;
    
    @Override
    public DemoGoOut toggleLike(DemoGoIn demoGoIn) {

        return new DemoGoOut();
    }
}
