package com.wt.complaint.manage.api.task;

import com.xiaomi.youpin.infra.rpc.Result;

/**
 * 定时任务相关发送消息相关接�?
 * @author zhangzheyang
 * @date 2025/1/5
 */
public interface CronPushTask {

    /**
     * 消息中心相关定时任务
     */
    Result<String> cronPush(String req);
}
