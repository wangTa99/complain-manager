package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 咨询单更新入�?
 */
@Data
@Builder
public class UcConsultOrderUpdateGoIn {
    /**
     * 咨询类单�?
     */
    private String consultNo;

    /**
     * 状态�? 1-待接�?2-待首�?3-待结�?4-已完�?
     */
    private Integer orderStatus;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 咨询类型
     */
    private Integer consultType;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 车型
     */
    private String carType;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 优先级，4 一般，8 高，16 紧�?
     */
    private Integer priority;

    /**
     * 门店Id
     */
    private String orgId;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /**
     * 期望回电时间
     */
    private Date expectingBackTime;

    /**
     * 创建人mid
     */
    private Long createMid;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 处理结果�?-无需门店处理�?-已处�?
     */
    private Integer handleResult;


    /**
     * 结案描述
     */
    private String finishDesc;
}
