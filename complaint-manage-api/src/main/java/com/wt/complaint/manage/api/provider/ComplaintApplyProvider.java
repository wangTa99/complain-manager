package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.apply.ExemptionApplyReq;
import com.wt.complaint.manage.api.model.req.apply.Org72HFreeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgFinishApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.xiaomi.youpin.infra.rpc.Result;

public interface ComplaintApplyProvider {
    // 需要有四个申请方法，改派门店申请，免责申请�?2H无法结案申请，结案申�?
    Result<OrgApplyResp> submitChangeOrgApply(OrgChangeApplyReq req);

    Result<OrgApplyResp> submitExemptionApply(ExemptionApplyReq req);

    Result<OrgApplyResp> submit72HFreeApply(Org72HFreeApplyReq req);

    /**
     * 暂留，待客诉二期上线一段时间后，可以移除此接口
     */
    @Deprecated
    Result<OrgApplyResp> submitFinishApply(OrgFinishApplyReq req);

    /**
     * 提交结案申请（pad端）
     * 客诉二期开始使用，旧接口废�?
     */
    Result<OrgApplyResp> submitFinishApplyV2(OrgFinishApplyReq req);
}
