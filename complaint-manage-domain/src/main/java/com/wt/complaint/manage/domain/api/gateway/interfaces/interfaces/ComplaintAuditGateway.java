package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintPreNextSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintRelationClosingTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintPreNextSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintRelationClosingTagSoOut;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
public interface ComplaintAuditGateway {

    ComplaintAuditListSoOut searchComplaintAuditList(ComplaintAuditListSoIn req);

    ComplaintPreNextSoOut selectPreAndAfter(ComplaintPreNextSoIn req);

    ComplaintAuditSoOut selectById(Long id);

    Boolean updateAuditById(SubmitForApprovalSoIn req);

    ComplaintAuditSoOut getRecentAuditByComplaintNo(String complaintNo, Integer auditType);

    List<ComplaintRelationClosingTagSoOut> getClosingTagListByComplaintNo(String complaintNo);

    void deleteClosingTagByComplaintNo(String complaintNo);

    void insertClosingTag(ComplaintRelationClosingTagSoIn req);

    Boolean batchInsertClosingTag(List<ComplaintRelationClosingTagSoIn> req);

}
