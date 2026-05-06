package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.DemoGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DemoGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.DemoService;
import com.wt.complaint.manage.domain.api.service.parameter.in.DemoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.DemoSoOut;
import com.wt.proretail.newcommon.util.ServiceInvokeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author huwei
 * @date 2021-06-18
 */
@Slf4j
@Service
public class DemoServiceImpl implements DemoService {
    
    
    @Resource
    private DemoGateway demoGateway;
    
    @Override
    public DemoSoOut toggleLike(DemoSoIn demoSoIn) {
        log.info("#PolicyCenterLikeCommentServiceImpl.toggleLike# request:{}", demoSoIn);
        DemoGoOut demoGoOut = demoGateway.toggleLike(demoSoIn.convert2service());
        ServiceInvokeUtil.checkBaseParamModelGoOut(demoGoOut);
        return new DemoSoOut().convert2service(demoGoOut);
    }
}
