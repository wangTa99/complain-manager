package com.wt.complaint.manage.domain.api.service.parameter.out.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintListGoOut {

    @ApiDocClassDefine(value = "drNo", description = "投诉单号")
    private String drNo;

    @ApiDocClassDefine(value = "lastReminderTime", description = "最近催单时�?)
    private Long lastReminderTime;

    @ApiDocClassDefine(value = "reminderTimes", description = "累计催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "tradeOrderId", description = "订单号，需支持跳转至业务办理的订单详情")
    private String tradeOrderId;

    @ApiDocClassDefine(value = "carTypeName", description = "车型")
    private String carTypeName;
    @ApiDocClassDefine(value = "saleCarVersion", description = "车型版本")
    private String saleCarVersion;

    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;

    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;

    @ApiDocClassDefine(value = "complaintScene", description = "投诉场景")
    private String complaintScene;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级")
    private Integer riskLevel;

    @ApiDocClassDefine(value = "riskLevelName", description = "风险等级")
    private String riskLevelName;

    @ApiDocClassDefine(value = "problemDesc", description = "投诉详情")
    private String problemDesc;

    @ApiDocClassDefine(value = "createTime", description = "投诉单创建时�?)
    private Long createTime;

    @ApiDocClassDefine(value = "orderStatus", description = "投诉单状态code, 10-待首�?20-跟进�?50-已结�?)
    private Integer orderStatus;
    @ApiDocClassDefine(value = "orderStatusName", description = "投诉单状态name")
    private String orderStatusName;

    @ApiDocClassDefine(value = "zoneName", description = "大区name")
    private String zoneName;

    @ApiDocClassDefine(value = "littleZoneName", description = "小区name")
    private String littleZoneName;

    @ApiDocClassDefine(value = "cityZoneName", description = "城市区域name")
    private String cityZoneName;

    @ApiDocClassDefine(value = "positionAUserName", description = "交付邀约专�?)
    private String positionAUserName;
    @ApiDocClassDefine(value = "positionBUserName", description = "交付接待专员")
    private String positionBUserName;

    @ApiDocClassDefine(value = "orgId", description = "跟进门店code")
    private String orgId;
    @ApiDocClassDefine(value = "orgName", description = "跟进门店name")
    private String orgName;

    @ApiDocClassDefine(value = "operatorPositionId", description = "跟进岗位id")
    private Integer operatorPositionId;
    @ApiDocClassDefine(value = "operatorPositionName", description = "跟进岗位name")
    private String operatorPositionName;

    @ApiDocClassDefine(value = "operatorMid", description = "跟进人员mid")
    private Long operatorMid;
    @ApiDocClassDefine(value = "operatorName", description = "跟进人员name")
    private String operatorName;

    @ApiDocClassDefine(value = "expectedFirstResponseTime", description = "首响截止时间")
    private Long expectedFirstResponseTime;

    @ApiDocClassDefine(value = "realFirstResponseTime", description = "实际首响时间")
    private Long realFirstResponseTime;

    @ApiDocClassDefine(value = "lastFollowDesc", description = "最新工单跟进描�?)
    private String lastFollowDesc;

    @ApiDocClassDefine(value = "expectedFinishTime", description = "结案截止时间")
    private Long expectedFinishTime;

    @ApiDocClassDefine(value = "realFinishTime", description = "实际结案时间")
    private Long realFinishTime;

    @ApiDocClassDefine(value = "responsible", description = "责任情况,  0-默认 1-有责 2-无责 3-待判�?)
    private Integer responsible;
    @ApiDocClassDefine(value = "responsibleName", description = "责任情况, 0-默认 1-有责 2-无责 3-待判�?)
    private String responsibleName;

    @ApiDocClassDefine(value = "firstResponseTag", description = "首响超时，单选： 0-未首响超�? 1-已首响超�?)
    private Integer firstResponseTag;

    @ApiDocClassDefine(value = "finishTag", description = "结案超时，单选：0-未结案超�? 1-已结案超�?)
    private Integer finishTag;

    private Integer zoneId;
    private Integer littleZoneId;
}
