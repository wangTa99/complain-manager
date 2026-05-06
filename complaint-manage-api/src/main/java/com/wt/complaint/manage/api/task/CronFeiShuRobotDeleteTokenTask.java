package com.wt.complaint.manage.api.task;

import com.xiaomi.youpin.infra.rpc.Result;

/**
 * 定时任务相关飞书机器人删除token相关接口
 * @author p-wangkai95
 * @date 2025/8/18
 */
public interface CronFeiShuRobotDeleteTokenTask {

    /**
     * 飞书机器人删除token相关定时任务
     */
    Result<String> deleteFeiShuToken(String req);
}
