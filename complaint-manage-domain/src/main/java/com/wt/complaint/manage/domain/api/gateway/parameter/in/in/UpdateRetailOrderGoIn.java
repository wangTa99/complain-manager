package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 更新订单状态入�?
 *
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRetailOrderGoIn {

    /**
     * 客诉单号
     */
    private String drNo;
    /**
     * 交付客诉单状�?
     */
    private Integer orderStatus;

    /**
     * 实际首响时间
     */
    private Date realFirstResponseTime;
    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 催单标识 0-不需要处理或已处�?1-需要处�?
     */
    private Integer reminderFlag;

    /**
     * 最新催单时�?
     */
    private Date lastReminderTime;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 跟进人岗位id
     */
    private Integer operatorPositionId;

    /**
     * 实际结案时间
     */
    private Date realFinishTime;

    private Integer zoneId;

    private Integer littleZoneId;

    private Integer cityId;

    private String orgId;

    private Date expectedResponseTime;

    private Date expectedFinishTime;

    private Integer reassignmentTimes;

    boolean isFirstResp;

    ComplaintFollowProcessGoIn complaintFollowProcessGoIn;

    /**
     * 首响标识
     * 0-未首响超�? 1-已首响超�?
     */
    private Integer firstResponseTag;

    /**
     * 结案标识
     * 0-未结案超�? 1-已结案超�?
     */
    private Integer finishTag;

}
