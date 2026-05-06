package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.AuditListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintAuditGoOut;

public interface ComplaintAuditRepositoryGateway {
    Boolean save(ComplaintAuditGoIn complaintAudit);
}
