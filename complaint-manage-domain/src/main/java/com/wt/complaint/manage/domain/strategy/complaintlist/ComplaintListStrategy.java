package com.wt.complaint.manage.domain.strategy.complaintlist;

import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;

public interface ComplaintListStrategy {

    ComplaintListSearchSoOut searchComplaintList(ComplaintListSearchGoIn complaintListSearchGoIn);

}
