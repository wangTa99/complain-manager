package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintListSearchInfo implements Serializable {

    private static final long serialVersionUID = 8048291777112043027L;

    /**
     * 客诉单号
     */
    private String complaintNo;


    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * vin�?
     */
    private String vin;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系人电�?
     */
    private String contactPhone;

    /**
     * 联系人性别 0 默认 1 �?2 �?
     */
    private Integer contactGender;

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
    private Integer status;

    /**
     * 投诉单状态名�?
     */
    private String statusName;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 首响时间
     */
    private String firstResponseTime;

    /**
     * 结案时间
     */
    private String finishTime;

    /**
     * 是否有责�? 无责 1 有责
     */
    private Integer responsibility;

    /**
     * 是否有责名称
     */
    private String responsibilityName;

    /**
     * 风险等级,int 1~4
     */
    private Integer riskLevel;

    /**
     * 风险等级名称
     */
    private String riskLevelName;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 处理人姓�?
     */
    private String operatorName;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

    /**
     * 跟进客服姓名
     */
    private String customerServiceName;

    /**
     * 门店ID
     */
    private String orgId;

    /**
     * 门店名称
     */
    private String orgName;

    /**
     * 标签列表
     */
    private List<TagSoOut> tagList;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 问题分类
     */
    private String problemCategory;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 投诉单是否门店仅查阅, 0-否，需要门店处�? 1-仅查�?不需要门店处�?
     */
    private Integer onlyView;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 区域id
     */
    private String zoneId;

    /**
     * 小区id
     */
    private String littleZoneId;

    /**
     * 客诉内容
     */
    private String complaintContent;

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
    private String upgradeTime;

    /**
     * 创建来源（客诉三期）�?-服务门店 2-线上客服
     */
    private Integer createSource;

    /**
     * 创建来源描述
     */
    private String createSourceDesc;

    /**
     * 是否已提交复盘（客诉三期）：0-�?1-�?
     */
    private Integer reviewed;

    /**
     * 免责申请次数，默�?
     */
    private Integer exemptionApplyTimes;

}
