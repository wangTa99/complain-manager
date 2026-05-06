package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 咨询单信息入�?
 */
@Data
public class UcConsultOrderGoIn {
    /**
     * 咨询类单�?
     */
    private String consultNo;

    /**
     * 咨询类单号列�?
     */
    private List<String> consultNoList;

    /**
     * 超级工单列表
     */
    private List<String> stNoList;

    /**
     * 咨询类单据类�?
     */
    private Integer consultType;

    /**
     * 业务幂等key
     */
    private String idempotentKey;

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
     * vin�?�?
     */
    private String vinSufix;

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
     * 处理人mid
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
     * 创建人mid
     */
    private Long createMid;
}
