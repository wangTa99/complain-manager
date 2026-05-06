package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.DemoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.DemoSoOut;

/**
 * @author huwei
 * @date 2021-06-18
 */
public interface DemoService {
    
    /**
     * �?取消�?
     *
     * @param demoSoIn 点赞信息  {@link DemoSoIn}
     * @return true/false  点赞成功/失败
     */
    DemoSoOut toggleLike(DemoSoIn demoSoIn);
}
