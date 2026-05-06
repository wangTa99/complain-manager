package com.wt.complaint.manage.api.task;

import com.xiaomi.youpin.infra.rpc.Result;

/**
 * 定时任务相关飞书机器人切换相关接�?
 * @author p-wangkai95
 * @date 2025/8/18
 */
public interface CronFeiShuRobotSwitchTask {

    /**
     * 飞书机器人切换相关定时任�?
     */
    Result<String> robotSwitch(String req);
}
