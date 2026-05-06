package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.task.CronFeiShuRobotDeleteTokenTask;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.config.Constants;
import com.wt.maindatacommon.constant.DubboConst;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@Slf4j
@DubboService(timeout = DubboConst.DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0", interfaceClass = CronFeiShuRobotDeleteTokenTask.class)
@ApiModule(value = "飞书机器人删除token相关定时任务", apiInterface = CronFeiShuRobotDeleteTokenTask.class)
public class CronFeiShuRobotDeleteTokenTaskImpl implements CronFeiShuRobotDeleteTokenTask {

    @Resource
    private RedisRemoteGateway redisRemoteGateway;
    /**
     * 飞书机器人删除token相关定时任务
     *
     * @return 结果
     */
    @Override
    public Result<String> deleteFeiShuToken(String req) {
        boolean b = redisRemoteGateway.unLock(Constants.LARK_ACCESS_TOKEN_REDIS);
        if (b) {
            log.info("call success CronFeiShuRobotDeleteTokenTask#deleteFeiShuToken");
            return Result.success("ok");
        } else {
            log.error("call CronFeiShuRobotDeleteTokenTask#deleteFeiShuToken fail");
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(),"删除token失败");
        }
    }
}
