package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.approve.*;
import com.wt.complaint.manage.api.model.resp.approve.AuditDetailForCustomerServiceResp;
import com.wt.complaint.manage.api.model.resp.approve.AuditTypeOptionResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditDetailResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditListResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintPreNextResp;
import com.xiaomi.youpin.infra.rpc.Result;

public interface ComplaintAuditProvider {

    /**
     * 查询投诉单审批列表�?
     * 售后工作�? /mtop/proretailcar/complaint/searchComplaintAuditList
     */
    Result<ComplaintAuditListResp> searchComplaintAuditList(ComplaintAuditListReq req);

    /**
     * 查询有权限的投诉单类型�?
     * 基于当前登录用户岗位，仅返回当前岗位能展示的审批类型，按 id 递增排序�?
     * 无入参�?
     * @return 有权限的投诉单类�?
     */
    Result<AuditTypeOptionResp> listAllowedAuditTypes();

    Result<ComplaintPreNextResp> preNextAudit(ComplaintPreNextReq req);

    Result<Boolean> submitForApproval(SubmitForApprovalReq req);

    Result<ComplaintAuditDetailResp> getComplaintAuditDetail(ComplaintAuditDetailReq req);

    Result<AuditDetailForCustomerServiceResp> getAuditDetailForCustomerService(AuditDetailForCustomerServiceReq req);

    /**
     * 服务投诉判责
     * 售后工作�? /mtop/proretailcar/complaint/audit/judgeResponsibility
     *
     * @param req 判责请求
     * @return 是否成功
     */
    Result<Boolean> judgeResponsibility(JudgeResponsibilityReq req);
}
