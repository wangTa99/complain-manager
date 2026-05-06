package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.task.TimeOutTagTaskSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.task.TimeOutTagTaskSoOut;

public interface ComplaintTaskService {
    TimeOutTagTaskSoOut syncTimeOutTag(TimeOutTagTaskSoIn soIn);
}
