package com.wt.complaint.manage.api.model.resp.retail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 投诉单查询响�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintListSearchInfo implements Serializable {

    /**
     * 投诉工单id
     */
    private String drNo;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 投诉类型 1 产品投诉 2 服务投诉
     */
    private Integer complaintType;

    /**
     * 投诉类型名称:产品投诉,服务投诉
     */
    private String complaintTypeName;

    /**
     * 投诉单状�?
     */
    private Integer orderStatus;

    /**
     * 投诉单状态名�?
     */
    private String orderStatusName;

    /**
     * 创建/更新时间
     */
    private String updateTime;

    /**
     * 风险等级,int 1~4
     */
    private Integer riskLevel;

    /**
     * 风险等级名称
     */
    private String riskLevelName;

    /**
     * 问题分类
     */
    private String problemCategory;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 预期首响时间
     */
    private Long expectedFirstResponseTime;

    /**
     * 预期结案时间
     */
    private Long expectedFinishTime;

    /**
     * 跟进人mid
     */
    private Long operatorMid;

    /**
     * 门店id
     */
    private String orgId;
}
