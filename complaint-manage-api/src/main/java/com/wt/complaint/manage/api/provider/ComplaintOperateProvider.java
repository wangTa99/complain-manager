package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.AddKindPointsDistributionRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.FollowRecordReqV2;
import com.wt.complaint.manage.api.model.req.operate.*;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * 客诉操作 Provider
 * 路径前缀�?mtop/proretailcarpad/complaint/operate/
 */
public interface ComplaintOperateProvider {
    Result<CreateComplaintOrderResp> createComplaintOrder(CreateComplaintOrderReq req);

    Result<PickUpOrderResp> pickUpOrder(PickUpOrderReq req);

    Result<UpdateHandlerResp> updateHandler(UpdateHandlerReq req);

    Result<AddFollowRecordResp> addFollowRecord(FollowRecordReq req);

    Result<AddFollowRecordResp> addFollowRecordV2(FollowRecordReqV2 req);

    Result<AddDistributionRecordResp> addKindPointsDistributionRecord(AddKindPointsDistributionRecordReq req);

    Result<RemindOrderResp> remindOrder(RemindOrderReq req);

    Result<UpdateCustomerServiceResp> updateCustomerService(UpdateCustomerServiceReq req);

    Result<UpdateCustomerServiceResp> upgradeComplaint(ComplaintOrderUpgradeReq req);

    Result<EditComplaintResp> editComplaint(EditComplaintReq req);

    /**
     * 提交复盘（客诉三期）
     * 路径�?mtop/proretailcarpad/complaint/operate/submitReview
     * @param req 提交复盘参数
     * @return 复盘响应
     */
    Result<SubmitReviewResp> submitReview(SubmitReviewReq req);
}
