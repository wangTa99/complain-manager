package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.DemoReq;
import com.wt.complaint.manage.api.model.req.TestMsgSend;
import com.wt.complaint.manage.api.model.resp.DemoResp;
import com.xiaomi.youpin.infra.rpc.Result;

import javax.validation.Valid;

/**
 * 政策中心provider
 * * @author huwei
 * @date 2021-06-18
 */
public interface DemoProvider {
    
    /**
     * �?取消�?
     *
     * @param req 点赞信息  {@link DemoReq}
     * @return true/false  点赞成功/失败
     */
    Result<DemoResp> toggleLike(@Valid  DemoReq req);

    Result<Boolean> testMsgSend(@Valid TestMsgSend req);
}
