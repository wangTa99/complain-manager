package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.task.CronPushTask;
import com.wt.complaint.manage.domain.api.service.interfaces.CronPushTaskService;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.maindatacommon.constant.DubboConst;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author zhangzheyang
 * @date 2025/1/5
 */
@Slf4j
@DubboService(timeout = DubboConst.DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0", interfaceClass = CronPushTask.class)
@ApiModule(value = "消息中心相关定时任务", apiInterface = CronPushTask.class)
public class CronPushTaskImpl implements CronPushTask {

    @Resource
    private CronPushTaskService cronPushTaskService;


    @Override
    public Result<String> cronPush(String req) {
        log.info("start call CronPushTask#cronPush, req:{}", req);
        try {
            cronPushTaskService.cronPush();
            log.info("call success CronPushTask#cronPushTask");
            return Result.success("ok");
        } catch (BusinessException e) {
            log.error("call CronPushTask#cronPushTask error", e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("call CronPushTask#cronPushTask fail", e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }
}
