package com.wt.complaint.manage.domain.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户投诉扩展信息�?
 *
 * @author linjiehong
 * @date 2025/5/22 21:08
 */
@Data
public class UserComplaintExpandInfo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户投诉编号
     */
    private String ucNo;

    /**
     * 提醒次数
     */
    private Integer reminderTimes;

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 区域ID
     */
    private Integer zoneId;

    /**
     * 小区ID
     */
    private Integer littleZoneId;

    /**
     * 服务场景
     */
    private List<String> serviceScene;

    /**
     * 联系电话后缀
     */
    private Integer contactPhoneSuffix;

    /**
     * 联系电话的MD5�?
     */
    private String contactPhoneMd5;

    /**
     * VIN码后缀
     */
    private String vinSuffix;

    /**
     * 判断类型
     */
    private Integer judgeType;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 联系人性别
     */
    private Integer contactGender;

    /**
     * 客服处理人mid
     */
    private Long customerServiceMid;

    /**
     * 扩展信息
     */
    private String expand;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
