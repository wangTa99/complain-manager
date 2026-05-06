package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.req.view.UcOrderInfoBatchReq;
import com.wt.complaint.manage.api.model.req.view.UcOrderLightInfoBatchReq;
import com.wt.complaint.manage.api.model.resp.*;
import com.wt.complaint.manage.api.model.resp.view.UcOrderInfoBatchResp;
import com.wt.complaint.manage.api.model.resp.view.UcOrderLightInfoBatchResp;
import com.xiaomi.youpin.infra.rpc.Result;

import javax.validation.Valid;

/**
 * 客诉类视图提供�?
 * @author linjiehong
 * @date 2025/5/19 13:31
 */
public interface UserComplaintViewProvider {
    /**
     * 查询用户客诉列表
     * @param req 请求参数
     * @return 用户客诉列表
     */
    Result<UserComplaintListSearchResp> searchUserComplaintList(@Valid UserComplaintListSearchReq req);

    /**
     * 查询用户客诉框架
     * @param req 请求参数
     * @return 框架信息
     */
    Result<UserComplaintDetailFrameResp> getUserComplaintFrame(@Valid UserComplaintDetailFrameReq req);

    /**
     * 查询用户客诉详情
     * @param req 请求参数
     * @return 客诉详情
     */
    Result<UserComplaintDetailResp> getUserComplaintDetail(@Valid UserComplaintDetailReq req);

    /**
     * 批量查询用户客诉信息
     * @param req 请求参数
     * @return 客诉信息
     */
    Result<UcOrderInfoBatchResp> getUcOrderInfo(UcOrderInfoBatchReq req);

    /**
     * 批量查询用户客诉轻量信息
     * @param req 请求参数
     * @return 客诉轻量信息
     */
    Result<UcOrderLightInfoBatchResp> getUcOrderLightInfo(UcOrderLightInfoBatchReq req);
}
