package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 零售客诉单详情响�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintDetailResp implements Serializable {

    private static final long serialVersionUID = -1183571417801094136L;

    @ApiDocClassDefine(value = "drNo", description = "客诉单号")
    private String drNo;
    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;
    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;
    @ApiDocClassDefine(value = "contactPhone", description = "联系人性别")
    private Integer contactGender;
    @ApiDocClassDefine(value = "clueId", description = "线索id")
    private Long clueId;
    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private String createTime;
    @ApiDocClassDefine(value = "complaintType", description = "投诉类型 1 产品投诉 2 服务投诉")
    private Integer complaintType;
    @ApiDocClassDefine(value = "complaintTypeName", description = "投诉类型名称:产品投诉,服务投诉")
    private String complaintTypeName;
    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;
    @ApiDocClassDefine(value = "complaintScene", description = "投诉场景")
    private String complaintScene;
    @ApiDocClassDefine(value = "riskLevel", description = "风险等级,L1~L4")
    private Integer riskLevel;
    @ApiDocClassDefine(value = "riskLevelName", description = "风险等级名称")
    private String riskLevelName;
    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;
    @ApiDocClassDefine(value = "orgName", description = "门店名称")
    private String orgName;
    @ApiDocClassDefine(value = "problemDesc", description = "问题详情")
    private String problemDesc;
    @ApiDocClassDefine(value = "userDemand", description = "用户诉求")
    private String userDemand;
    @ApiDocClassDefine(value = "orderStatus", description = "交付客诉单状�?0-初始�?10-待首�?20-跟进�?50-已结�? 零售客诉单额外增�?30-申请结案�?)
    private Integer orderStatus;
    @ApiDocClassDefine(value = "orderStatusName", description = "交付客诉单状态名�?)
    private String orderStatusName;
    @ApiDocClassDefine(value = "reassignmentTimes", description = "改派次数")
    private Integer reassignmentTimes;
    @ApiDocClassDefine(value = "operatorMid", description = "跟进人mid")
    private Long operatorMid;
    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;
    @ApiDocClassDefine(value = "attachmentList", description = "附件列表")
    private List<AttachmentSoResp> attachmentList;
}
