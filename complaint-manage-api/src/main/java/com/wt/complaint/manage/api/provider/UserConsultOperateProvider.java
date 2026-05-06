package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultFinishReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultReassignReq;
import com.wt.complaint.manage.api.model.req.consult.EditConsultReq;
import com.wt.complaint.manage.api.model.req.operate.CreateConsultReq;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.UpdateHandlerReq;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.xiaomi.youpin.infra.rpc.Result;
import javax.validation.Valid;

public interface UserConsultOperateProvider {
    /**
     * 新建咨询作业�?
     * @param req 通用建单参数
     * @return 作业单号
     */
    Result<CreateOrderResp> createOrder(CreateConsultReq req);


    /**
     * 编辑咨询�?
     * @param req
     * @return
     */
    Result<EditComplaintResp> editConsult(EditConsultReq req);

    /**
     * 咨询作业单接�?
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
     * 改派门店
     * @param req  申请请求参数
     * @return  申请响应结果
     */
    Result<ChangeOrgResp> submitChangeOrgApply(@Valid ConsultOrgChangeApplyReq req);


    /**
     * 更新作业单处理人
     * @param req 更新处理人请求参�?
     * @return 更新处理人响应结�?
     */
    Result<UpdateHandlerResp> updateHandler(UpdateHandlerReq req);

    /**
     * 改派跟进�?
     * @param req 请求参数
     * @return 改派跟进人结�?
     */
    Result<String> reassign(@Valid ConsultReassignReq req);


    /**
     * 结案
     * @param req 请求参数
     * @return 结案结果
     */
    Result<String> finish(@Valid ConsultFinishReq req);

}
