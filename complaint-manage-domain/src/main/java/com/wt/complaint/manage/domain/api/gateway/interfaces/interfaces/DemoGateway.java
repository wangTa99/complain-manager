package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.DemoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DemoGoOut;

/**
 * 点赞评论网关�?
 *
 * @author wangshanjun
 * @date 2021/6/17
 */
public interface DemoGateway {
    
    /**
     * 点赞状�?
     *
     * @param demoGoIn {@link DemoGoIn}
     * @return LikeCommentLikeOut
     */
    DemoGoOut toggleLike(DemoGoIn demoGoIn);
}
