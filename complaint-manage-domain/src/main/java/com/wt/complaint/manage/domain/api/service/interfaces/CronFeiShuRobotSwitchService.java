package com.wt.complaint.manage.domain.api.service.interfaces;

/**
 * 定时任务相关飞书机器人切换相关接�?
 * @author p-wangkai95
 * @date 2025/8/18
 */
public interface CronFeiShuRobotSwitchService {

    /**
     * 飞书机器人切换相关定时任�?
     */
    void robotSwitch(String req);
}
