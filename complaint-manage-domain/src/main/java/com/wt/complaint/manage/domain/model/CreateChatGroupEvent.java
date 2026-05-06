package com.wt.complaint.manage.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 创建飞书群聊事件
 * @author zhangzheyang
 * @date 2025/6/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatGroupEvent {

    /**
     * 交易或交付客诉单�?
     */
    private String drNo;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 大区id
     */
    private Integer zoneId;

    /**
     * 小区id
     */
    private Integer littleZoneId;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

    /**
     * 操作人mid
     */
    private Long operatorMid;

    /**
     * 跟进人岗位类�?
     */
    private Integer operatorPositionId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 客诉内容
     * json格式
     */
    private String complaintContent;

    /**
     * 风险等级
     * 1,2,3,4
     */
    private Integer riskLevel;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 问题描述
     */
    private String problemDesc;

}
