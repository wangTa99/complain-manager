package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
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
public class RetailComplaintListSearchDTO implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号")
    private String drNo;

    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @ApiDocClassDefine(value = "complaintType", description = "投诉类型 1 产品投诉 2 服务投诉")
    private Integer complaintType;

    @ApiDocClassDefine(value = "complaintTypeName", description = "投诉类型名称:产品投诉,服务投诉")
    private String complaintTypeName;

    @ApiDocClassDefine(value = "orderStatus", description = "投诉单状�?")
    private Integer orderStatus;

    @ApiDocClassDefine(value = "orderStatusName", description = "投诉单状态名�?)
    private String orderStatusName;

    @ApiDocClassDefine(value = "updateTime", description = "创建/更新时间")
    private String updateTime;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级,int 1~4")
    private Integer riskLevel;

    @ApiDocClassDefine(value = "riskLevelName", description = "风险等级名称")
    private String riskLevelName;

    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;

    @ApiDocClassDefine(value = "problemDesc", description = "问题描述")
    private String problemDesc;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "expectedFirstResponseTime", description = "预期首响时间")
    private Long expectedFirstResponseTime;

    @ApiDocClassDefine(value = "expectedFinishTime", description = "预期结案时间")
    private Long expectedFinishTime;

    @ApiDocClassDefine(value = "operatorMid", description = "跟进人mid")
    private Long operatorMid;

    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;
}
