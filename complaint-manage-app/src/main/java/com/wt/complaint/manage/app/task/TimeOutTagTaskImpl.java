package com.wt.complaint.manage.app.task;

import com.wt.complaint.manage.api.model.req.task.TimeOutTagTaskReq;
import com.wt.complaint.manage.api.model.resp.task.TimeOutTagTaskResp;
import com.wt.complaint.manage.api.task.TimeOutTagTask;
import com.wt.complaint.manage.app.convert.ComplaintTaskConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintTaskService;
import com.wt.complaint.manage.domain.api.service.parameter.out.task.TimeOutTagTaskSoOut;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.maindatacommon.constant.DubboConst;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;

@DubboService(interfaceClass = TimeOutTagTask.class, timeout = DubboConst.DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class TimeOutTagTaskImpl implements TimeOutTagTask  {
    @Resource
    private ComplaintTaskService complaintTaskService;

    @Override
    @ExceptionHandler
    public Result<TimeOutTagTaskResp> syncTimeOutTag(TimeOutTagTaskReq req) {
        try {
            log.info("TimeOutTagTaskImpl syncTimeOutTag req:{}", req);
            TimeOutTagTaskSoOut timeOutTagTaskSoOut = complaintTaskService.syncTimeOutTag(ComplaintTaskConvert.INSTANCE.toSoIn(req));
            TimeOutTagTaskResp resp = new TimeOutTagTaskResp();
            resp.setResult(timeOutTagTaskSoOut.getResult());
            return Result.success(resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
        }
    }
}
