package com.wt.complaint.manage.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * 咨询单信息模�?
 */
@Data
public class UserConsultOrderInfo {
    /**
     * 自增 id
     */
    private Long id;

    /**
     * 咨询类单�?
     */
    private String consultNo;

    /**
     * 咨询类单据类�?
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
     * 业务幂等 key
     */
    private String idempotentKey;

    /**
     * 车辆 vid
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
     * 咨询单状�?1-待接�?2-待首�?3-待结�?4-已完�?
     */
    private Integer orderStatus;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 优先级，4 一般，8 高，16 紧�?
     */
    private Integer priority;

    /**
     * 门店 Id
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
     * 测试标识�?-非测试环境，1-是测试环�?
     */
    private Byte testTag;

    /**
     * 处理�?mid
     */
    private Long operatorMid;

    /**
     * 期望回电时间
     */
    private Date expectingBackTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建�?mid
     */
    private Long createMid;

    /**
     * 处理结果�?-无需门店处理�?-已处�?
     */
    private Integer handleResult;
}
