package com.wt.complaint.manage.domain.strategy.process;

import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;

public interface FollowProcessStrategy {
    ComplaintProcessListSoOut getFollowUpRecords(ComplaintProcessSoIn soIn);
}
