package com.wt.complaint.manage.api.task;

import com.wt.complaint.manage.api.model.req.task.TimeOutTagTaskReq;
import com.wt.complaint.manage.api.model.resp.task.TimeOutTagTaskResp;
import com.xiaomi.youpin.infra.rpc.Result;

public interface TimeOutTagTask {
    Result<TimeOutTagTaskResp> syncTimeOutTag(TimeOutTagTaskReq req);
}
