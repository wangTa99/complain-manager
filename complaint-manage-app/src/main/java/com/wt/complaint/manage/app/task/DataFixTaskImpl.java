package com.wt.complaint.manage.app.task;

import com.wt.complaint.manage.api.task.DataFixTask;
import com.wt.complaint.manage.domain.api.service.interfaces.DataFixTaskService;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.maindatacommon.constant.DubboConst;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = DataFixTask.class, timeout = DubboConst.DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class DataFixTaskImpl implements DataFixTask {
    @Resource
    private DataFixTaskService dataFixTaskService;

    @Override
    public Result<String> fillComplaintSceneTask(String req) {
        try {
            log.info("DataFixTaskImpl#fillComplaintSceneTask req:{}", req);
            dataFixTaskService.fillComplaintSceneTask(req);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("DataFixTaskImpl#fillComplaintSceneTask message:{}", e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public Result<String> fixOperatorPosition(String req) {
        try {
            log.info("DataFixTaskImpl#fixOperatorPosition req:{}", req);
            dataFixTaskService.fixOperatorPosition(req);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("DataFixTaskImpl#fixOperatorPosition message:{}", e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public Result<String> updateZoneData(String req) {
        try {
            log.info("DataFixTaskImpl#fillCityZoneId req:{}", req);
            dataFixTaskService.updateZoneData(req);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("DataFixTaskImpl#fillCityZoneId message:{}", e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public Result<Integer> convertResponsibilityToTag(String complaintNo) {
        try {
            log.info("DataFixTaskImpl#convertResponsibilityToTag complaintNo:{}", complaintNo);
            return Result.success(dataFixTaskService.convertResponsibilityToTag(complaintNo));
        } catch (Exception e) {
            log.error("DataFixTaskImpl#convertResponsibilityToTag message:{}", e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.BUS_ERROR.getErrorCode(), e.getMessage());
        }
    }
}
