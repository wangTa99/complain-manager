package com.wt.complaint.manage.api.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintOrder {
    private Long id; // 自增id
    private String idempotentKey; // 业务幂等key
    private String complaintNo; // 客诉单号
    private Integer complaintType; // 投诉分类 1 产品投诉 2 服务投诉
    private Integer riskLevel; // 风险等级 1 2 3 4
    private String vid; // 车辆vid
    private String carType; // 车型
    private String carNo; // 车牌�?
    private Integer responsibility; // 是否有责�? 无责 1 有责
    private String superTicketNo; // 超级工单�?
    private String soNo; // 服务单号
    private String orgId; // 门店Id
    private String contactNameC; // 联系人姓名密�?
    private Integer contactGender; // 联系人性别 0 默认 1 �?2 �?
    private String contactPhoneC; // 联系人电话密�?
    private Integer contactPhoneSufix; // 手机号后4�?
    private Integer vinSufix; // vin�?�?
    private Integer status; // 客诉单状�?
    private String problemDesc; // 问题描述
    private String complaintContent; // 客诉内容
    private Integer reminderTimes; // 催单次数
    private Long customerServiceMid; // 跟进客服mid
    private Long operatorMid; // 处理人mid
    private Date finishTime; // 结案时间
    private Date firstResponseTime; // 首响时间
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private String cityId; // 城市id
    private String zoneId; // 大区id
    private String littleZoneId; // 小区id
    private Integer testTag; // 测试标识, 0-非测试环�? 1-是测试环�?
    private String problemCategory; // 问题类目
    private String userDemand; // 用户诉求
    private Integer onlyView; // 投诉单是否门店仅查阅, 0-否，需要门店处�? 1-仅查�?不需要门店处�?
    private String contactPhoneMd5;//联系人电话md5
    private Integer mediaInvolved; // 是否涉媒 0-�?1-�?
    private String mediaLink; // 涉媒链接
    private Date upgradeTime; // 升级投诉时间，默认�?'1970-08-02 00:00:00' 表示未升�?

}
