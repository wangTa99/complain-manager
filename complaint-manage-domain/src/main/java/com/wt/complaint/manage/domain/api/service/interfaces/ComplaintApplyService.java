package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;

public interface ComplaintApplyService {
    ComplaintApplySoOut submitApply(ComplaintApplySoIn soIn);

    /**
     * 根据条件持久化服务投诉判责申请记�?
     *
     * @param orderInfoGoIn 客诉单入�?
     * @param carStoreName 门店名称
     * @return 服务投诉判责审批出参
     */
    ComplaintAuditGoIn persistComplaintAdjudicationApplyRecord(ComplaintOrderInfoGoIn orderInfoGoIn, String carStoreName);

}
