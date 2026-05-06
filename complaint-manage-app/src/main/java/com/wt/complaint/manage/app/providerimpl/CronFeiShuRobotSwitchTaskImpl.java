package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.task.CronFeiShuRobotSwitchTask;
import com.wt.complaint.manage.domain.api.service.interfaces.CronFeiShuRobotSwitchService;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.maindatacommon.constant.DubboConst;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@Slf4j
@DubboService(timeout = DubboConst.DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0", interfaceClass = CronFeiShuRobotSwitchTask.class)
@ApiModule(value = "飞书机器人切换相关定时任�?, apiInterface = CronFeiShuRobotSwitchTask.class)
public class CronFeiShuRobotSwitchTaskImpl implements CronFeiShuRobotSwitchTask {

    @Resource
    private CronFeiShuRobotSwitchService cronFeiShuRobotSwitchService;

    /**
     * 飞书机器人切�?
     *
     * @return 结果
     */
    @Override
    public Result<String> robotSwitch(String req) {
        log.info("start call CronFeiShuRobotSwitchTask#robotSwitch, req:{}", req);
        try {
            cronFeiShuRobotSwitchService.robotSwitch(req);
            log.info("call success CronFeiShuRobotSwitchTask#robotSwitch");
            return Result.success("ok");
        } catch (BusinessException e) {
            log.warn("call CronFeiShuRobotSwitchTask#robotSwitch error", e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("call CronFeiShuRobotSwitchTask#robotSwitch fail", e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }
}
