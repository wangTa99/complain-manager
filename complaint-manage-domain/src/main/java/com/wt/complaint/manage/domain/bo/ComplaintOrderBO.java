package com.wt.complaint.manage.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客诉�?
 *
 * @TableName complaint_order
 */
@Data
public class ComplaintOrderBO implements Serializable {

    private static final long serialVersionUID = 5861703039711847292L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 业务幂等key
     */
    private String idempotentKey;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 投诉分类 1 产品投诉 2 服务投诉
     */
    private Integer complaintType;

    /**
     * 风险等级 1 2 3 4
     */
    private Integer riskLevel;

    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 是否有责�? 无责 1 有责
     */
    private Integer responsibility;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 门店Id
     */
    private String orgId;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人性别 0 默认 1 �?2 �?
     */
    private Integer contactGender;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 手机号后4�?
     */
    private Integer contactPhoneSufix;

    /**
     * vin�?�?
     */
    private Integer vinSufix;

    /**
     * 客诉单状�?
     */
    private Integer status;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 客诉内容
     */
    private String complaintContent;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 结案时间
     */
    private Date finishTime;

    /**
     * 首响时间
     */
    private Date firstResponseTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /**
     * 问题类目
     */
    private String problemCategory;

    /**
     * 用户诉求
     */
    private String userDemand;

    /**
     * 投诉单是否门店仅查阅, 0-否，需要门店处�? 1-仅查�?不需要门店处�?
     */
    private Integer onlyView;

    /**
     * 是否涉媒 0-�?1-�?
     */
    private Integer mediaInvolved;

    /**
     * 涉媒链接
     */
    private String mediaLink;

    /**
     * 升级投诉时间，默认�?'1970-08-02 00:00:00' 表示未升�?
     */
    private Date upgradeTime;

}
