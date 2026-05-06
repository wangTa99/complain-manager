package com.wt.complaint.manage.api.model.resp;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 记录信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordInfo implements Serializable {

    @ApiDocClassDefine(value = "applyId", description = "申请ID")
    private Integer applyId;

    @ApiDocClassDefine(value = "applyType", description = "申请类型" +
            "REASSIGNMENT_STORES-改派门店\n" +
            "APPLICATION_72H_CANNOT_BE_CLOSED-申请72H无法结案\n" +
            "APPLICATION_FOR_WAIVER-申请免责\n" +
            "APPLICATION_FOR_CLOSURE-申请结案")
    private Integer applyType;

    @ApiDocClassDefine(value = "applyTime", description = "申请时间")
    private String applyTime;

    @ApiDocClassDefine(value = "applyMid", description = "申请人mid")
    private Long applyMid;

    @ApiDocClassDefine(value = "applyName", description = "申请人姓�?)
    private String applyName;

    @ApiDocClassDefine(value = "deliveryTime", description = "车辆交付日期")
    private String deliveryTime;

    @ApiDocClassDefine(value = "mileage", description = "里程�?)
    private Double mileage;

    @ApiDocClassDefine(value = "applyReason", description = "申请原因")
    private String applyReason;

    @ApiDocClassDefine(value = "applyOrgId", description = "申请门店ID")
    private String applyOrgId;

    @ApiDocClassDefine(value = "applyOrgName", description = "申请门店名称")
    private String applyOrgName;

    @ApiDocClassDefine(value = "applyOrgDisplayName", description = "申请门店展示名称")
    private String applyOrgDisplayName;

    @ApiDocClassDefine(value = "reassignOrgId", description = "改派门店id")
    private String reassignOrgId;

    @ApiDocClassDefine(value = "reassignOrgName", description = "改派门店名称")
    private String reassignOrgName;

    @ApiDocClassDefine(value = "reassignOrgDisplayName", description = "改派门店展示名称")
    private String reassignOrgDisplayName;

    @ApiDocClassDefine(value = "auditTime", description = "审核时间 2023-10-11 12:23:45")
    private String auditTime;

    @ApiDocClassDefine(value = "auditMid", description = "审核人mid")
    private String auditMid;

    @ApiDocClassDefine(value = "auditName", description = "审核人姓�?)
    private String auditName;

    @ApiDocClassDefine(value = "auditResult", description = "审核结果 审核通过 审核驳回")
    private String auditResult;

    @ApiDocClassDefine(value = "solutionDesc", description = "解决方案")
    private String solutionDesc;

    @ApiDocClassDefine(value = "finishTabList", description = "结案标签")
    private List<String> finishTabList;

    @ApiDocClassDefine(value = "auditReason", description = "审批意见（免责通过/驳回等场景）")
    private String auditReason;

    @ApiDocClassDefine(value = "pickUpTime", description = "接单时间 2023-12-24 14:45:21")
    private String pickUpTime;

    @ApiDocClassDefine(value = "orderReceiverMid", description = "接单人mid")
    private String orderReceiverMid;

    @ApiDocClassDefine(value = "orderReceiverName", description = "接单人姓�?)
    private String orderReceiverName;

    @ApiDocClassDefine(value = "dispatchTime", description = "派单时间")
    private String dispatchTime;

    @ApiDocClassDefine(value = "dispatcherMid", description = "派单人mid")
    private String dispatcherMid;

    @ApiDocClassDefine(value = "dispatcherName", description = "派单人姓�?)
    private String dispatcherName;

    @ApiDocClassDefine(value = "followUpTime", description = "跟进时间 2023-12-24 14:45:21")
    private String followUpTime;

    @ApiDocClassDefine(value = "followUpMid", description = "跟进人员mid")
    private String followUpMid;

    @ApiDocClassDefine(value = "followUpName", description = "跟进人员姓名")
    private String followUpName;

    @ApiDocClassDefine(value = "followUpContent", description = "跟进详情")
    private String followUpContent;

    @ApiDocClassDefine(value = "remindOrderTime", description = "催单时间")
    private String remindOrderTime;

    @ApiDocClassDefine(value = "orderReminderMid", description = "催单人mid")
    private String orderReminderMid;

    @ApiDocClassDefine(value = "orderReminderName", description = "催单人姓�?)
    private String orderReminderName;

    @ApiDocClassDefine(value = "orderRemindInfo", description = "催单信息")
    private String orderRemindInfo;

    @ApiDocClassDefine(value = "stNo", description = "工单�?)
    private String stNo;

    @ApiDocClassDefine(value = "mrNo", description = "维保单号")
    private String mrNo;

    @ApiDocClassDefine(value = "mrStatus", description = "维保单状�?)
    private Integer mrStatus;

    @ApiDocClassDefine(value = "mrStatusName", description = "维保单状�?)
    private String mrStatusName;

    @ApiDocClassDefine(value = "createTime", description = "创建时间 2023-12-12 23:12:56")
    private String createTime;

    @ApiDocClassDefine(value = "createMid", description = "创建人mid")
    private String createMid;

    @ApiDocClassDefine(value = "createName", description = "创建人姓�?)
    private String createName;

    @ApiDocClassDefine(value = "contactMid", description = "联系人mid")
    private String contactMid;

    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactPhoneNumber", description = "联系人电�?)
    private String contactPhoneNumber;

    @ApiDocClassDefine(value = "appointTime", description = "预约时间")
    private String appointTime;

    @ApiDocClassDefine(value = "estimatedDeliveryTime", description = "预估交车时间")
    private String estimatedDeliveryTime;

    @ApiDocClassDefine(value = "serviceReceiverMid", description = "服务接待人mid")
    private String serviceReceiverMid;

    @ApiDocClassDefine(value = "serviceReceiverName", description = "服务接待人姓�?)
    private String serviceReceiverName;

    @ApiDocClassDefine(value = "questionDescription", description = "问题描述")
    private String questionDescription;

    @ApiDocClassDefine(value = "distributionId", description = "积分下发id")
    private Long distributionId;

    @ApiDocClassDefine(value = "distributionTime", description = "积分下发时间")
    private String distributionTime;

    @ApiDocClassDefine(value = "pointsBatch", description = "积分批次")
    private Integer pointsBatch;

    @ApiDocClassDefine(value = "distributionMid", description = "积分发放�?)
    private String distributionMid;

    @ApiDocClassDefine(value = "distributionName", description = "积分发放人姓�?)
    private String distributionName;

    @ApiDocClassDefine(value = "pointsQuantity", description = "积分数量")
    private Integer pointsQuantity;

    @ApiDocClassDefine(value = "pointsAmount", description = "积分价�?)
    private Integer pointsAmount;

    @ApiDocClassDefine(value = "pointsAuditStatus", description = "积分审批状�?)
    private String pointsAuditStatus;

    @ApiDocClassDefine(value = "pointsAuditStatusName", description = "积分审批状态名�?)
    private String pointsAuditStatusName;

    @ApiDocClassDefine(value = "pointsDistributionStatus", description = "积分发放状�?)
    private String pointsDistributionStatus;

    @ApiDocClassDefine(value = "pointsDistributionStatusName", description = "积分发放状态名�?)
    private String pointsDistributionStatusName;

    @ApiDocClassDefine(value = "attachments", description = "附件列表")
    private List<Attachment> attachments;

    @ApiDocClassDefine(value = "operateMid", description = "操作人mid")
    private String operateMid;

    @ApiDocClassDefine(value = "operateName", description = "操作人name")
    private String operateName;

    @ApiDocClassDefine(value = "operateDesc", description = "操作说明")
    private String operateDesc;

    @ApiDocClassDefine(value = "operateTime", description = "操作时间")
    private String operateTime;

    @ApiDocClassDefine(value = "operatePositionId", description = "操作人岗位code")
    private String operatePositionId;
    @ApiDocClassDefine(value = "operateName", description = "操作人岗位name")
    private String operatePositionName;

    @ApiDocClassDefine(value = "judgeResult", description = "判定结果 1:判定有效 2:判定无效")
    private Integer judgeResult;

    @ApiDocClassDefine(value = "judgeResultDesc", description = "判定结果描述")
    private String judgeResultDesc;

    // 跟进
    @ApiDocClassDefine(value = "followDesc", description = "工单跟进描述")
    private String followDesc;

    // 改派责任�?

    @ApiDocClassDefine(value = "reassignOperatorPositionId", description = "改派岗位")
    private Integer reassignOperatorPositionId;
    @ApiDocClassDefine(value = "reassignOperatorPositionName", description = "改派岗位name")
    private String reassignOperatorPositionName;

    @ApiDocClassDefine(value = "reassignOperatorMid", description = "改派人员mid")
    private Long reassignOperatorMid;
    @ApiDocClassDefine(value = "reassignOperatorName", description = "改派人员name")
    private String reassignOperatorName;

    @ApiDocClassDefine(value = "reassignDesc", description = "改派描述")
    private String reassignDesc;

    // 结案
    @ApiDocClassDefine(value = "reconciled", description = "是否和解")
    private String reconciled;

    @ApiDocClassDefine(value = "revisited", description = "是否回访")
    private String revisited;

    @ApiDocClassDefine(value = "finishDesc", description = "结案描述", required = false)
    private String finishDesc;


    @ApiDocClassDefine(value = "responsible", description = "判责�?有责，无�?)
    private String responsible;

    @ApiDocClassDefine(value = "responsibleJudgeDesc", description = "判责说明，即审批意见")
    private String responsibleJudgeDesc;

    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级")
    private String riskLevel;

    @ApiDocClassDefine(value = "orgId", description = "跟进门店code")
    private String orgId;
    @ApiDocClassDefine(value = "orgName", description = "跟进门店name")
    private String orgName;

    @ApiDocClassDefine(value = "operatorPositionId", description = "跟进岗位id")
    private Integer operatorPositionId;
    @ApiDocClassDefine(value = "operatorPositionName", description = "跟进岗位name")
    private String operatorPositionName;

    // 升级投诉记录相关字段
    @ApiDocClassDefine(value = "upgradeTime", description = "升级时间")
    private String upgradeTime;
    @ApiDocClassDefine(value = "upgraderName", description = "升级�?)
    private String upgraderName;
    @ApiDocClassDefine(value = "originalTypeDesc", description = "原投诉类型描�?参考ComplaintTypeEnum")
    private String originalTypeDesc;
    @ApiDocClassDefine(value = "targetTypeDesc", description = "目标投诉类型描述,参考ComplaintTypeEnum")
    private String targetTypeDesc;
    @ApiDocClassDefine(value = "upgradeReason", description = "升级原因")
    private String upgradeReason;

    // 投诉单信息更新记录相关字�?
    @ApiDocClassDefine(value = "complaintTypeChange", description = "投诉场景：由\"xxxx\"更新为\"xxxx\"")
    private String complaintTypeChange;
    @ApiDocClassDefine(value = "riskLevelChange", description = "风险等级：由\"xxxx\"更新为\"xxxx\"")
    private String riskLevelChange;
    @ApiDocClassDefine(value = "mediaInvolvedChange", description = "是否涉媒：由\"xxxx\"更新为\"xxxx\"")
    private String mediaInvolvedChange;
    @ApiDocClassDefine(value = "mediaLinkChange", description = "涉媒链接：由\"xxxx\"更新为\"xxxx\"")
    private String mediaLinkChange;

    // 申请结案记录扩展字段
    @ApiDocClassDefine(value = "userAgreementDesc", description = "是否与用户达成一�?参考UserAgreementEnum")
    private String userAgreementDesc;
    @ApiDocClassDefine(value = "vehicleRepairedDesc", description = "车辆异常是否修复,参考VehicleRepairedEnum")
    private String vehicleRepairedDesc;
    @ApiDocClassDefine(value = "mediaInfoDesc", description = "涉媒信息描述,参考MediaInfoEnum")
    private String mediaInfoDesc;
    @ApiDocClassDefine(value = "handleType", description = "处理类型 1 已处�?2 无需门店处理")
    private Integer handleType;

    // 提交复盘扩展字段
    @ApiDocClassDefine(value = "reviewMaterialUrl", description = "复盘材料，飞书链�?)
    private String reviewMaterialUrl;

    @ApiDocClassDefine(value = "currentNode", description = "当前审批节点（仅申请免责有效）：1-一�?2-二审 3-三审")
    private Integer currentNode;

    @ApiDocClassDefine(value = "beforeUpdate", description = "更新前的字段�?)
    private String beforeUpdate;

    @ApiDocClassDefine(value = "afterUpdate", description = "更新后的字段�?)
    private String afterUpdate;
}
