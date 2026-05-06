package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.retail.RetailComplaintFinishApplyReq;
import com.wt.complaint.manage.api.model.req.retail.RetailFollowRecordReq;
import com.wt.complaint.manage.api.model.req.retail.RetailOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.retail.CreateRetailComplaintOrderReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.retail.CreateRetailComplaintOrderResp;
import com.xiaomi.youpin.infra.rpc.Result;

import javax.validation.Valid;

/**
 * 零售投诉视图操作服务
 *
 * @author p-wangkai95
 * @version 1.0
 */
public interface RetailComplaintOperateProvider {

    /**
     * 创建交付或零售客诉单
     *
     * @param req 创建投诉单请求参�?
     * @return 创建投诉单响应结�?
     */
    Result<CreateRetailComplaintOrderResp> createComplaintOrder(@Valid CreateRetailComplaintOrderReq req);

    /**
     * 添加跟进记录
     *
     * @param req 跟进记录请求参数
     * @return 跟进记录响应结果
     */
    Result<AddFollowRecordResp> addFollowRecord(@Valid RetailFollowRecordReq req);

    /**
     * 结案申请
     *
     * @param req 结案申请请求参数
     * @return 结案申请响应结果
     */
    Result<OrgApplyResp> submitFinishApply(@Valid RetailComplaintFinishApplyReq req);

    /**
     * 申请改派门店
     *
     * @param req 改派申请请求参数
     * @return 改派申请响应结果
     */
    Result<OrgApplyResp> submitChangeOrgApply(@Valid RetailOrgChangeApplyReq req);

}
