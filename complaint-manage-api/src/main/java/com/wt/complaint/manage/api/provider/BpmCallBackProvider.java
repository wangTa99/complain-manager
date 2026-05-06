package com.wt.complaint.manage.api.provider;

import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedRequest;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedResponse;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * BPM回调
 */
public interface BpmCallBackProvider {

    /**
     * 改派门店BPM审批结果回调
     * @param request 状态变更请求参�?
     * @return 状态变更响应参�?
     */
    Result<OnStatusChangedResponse> changeOrgAuditCallback(OnStatusChangedRequest request);
    Result<OnStatusChangedResponse> applyFinishRetailCallback(OnStatusChangedRequest request);

    /**
     * 免责申请BPM回调
     * @param request 请求入参
     * @return 响应结果
     */
    Result<OnStatusChangedResponse> responsibilityExemptionCallback(OnStatusChangedRequest request);

}
