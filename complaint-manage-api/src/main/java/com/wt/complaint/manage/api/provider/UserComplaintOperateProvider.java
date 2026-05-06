package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.operate.CreateOrderReq;
import com.wt.complaint.manage.api.model.req.operate.JudgeOrderReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.RemindOrderReq;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.operate.CreateOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.JudgeOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.PickUpOrderResp;
import com.wt.complaint.manage.api.model.resp.operate.RemindOrderResp;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * 客诉类单据操作提供�?
 * @author linjiehong
 * @date 2025/5/19 10:37
 */
public interface UserComplaintOperateProvider {
    /**
     * 新建客诉类作业单
     * @param req 通用建单参数
     * @return 作业单号
     */
    Result<CreateOrderResp> createOrder(CreateOrderReq req);

    /**
     * 客诉类作业单接单
     * @param req 作业单号
     * @return 接单结果
     */
    Result<PickUpOrderResp> pickUpOrder(PickUpOrderReq req);

    /**
     * 新增跟进记录
     * @param req 跟进记录内容
     * @return 新增结果
     */
    Result<AddFollowRecordResp> addFollowRecord(FollowRecordReq req);

    /**
     * 作业单催�?
     * @param req 作业单号
     * @return 催单结果
     */
    Result<RemindOrderResp> remindOrder(RemindOrderReq req);

    /**
     * 作业单判�?
     * @param req 作业单号
     * @return 判单结果
     */
    Result<JudgeOrderResp> judgeOrder(JudgeOrderReq req);
}
