package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordInfoSoOut {
    /**
     * 申请ID
     */
    private Integer applyId;

    /**
     * 申请类型
     * REASSIGNMENT_STORES(1, "改派门店"),
     * APPLICATION_72H_CANNOT_BE_CLOSED(2, "申请72H无法结案"),
     * APPLICATION_FOR_WAIVER(3, "申请免责"),
     * APPLICATION_FOR_CLOSURE(4, "申请结案")
     */
    private Integer applyType;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 申请人mid
     */
    private Long applyMid;

    /**
     * 申请人姓�?
     */
    private String applyName;

    /**
     * 车辆交付日期
     */
    private String deliveryTime;

    /**
     * 里程�?
     */
    private Double mileage;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 申请门店ID
     */
    private String applyOrgId;

    /**
     * 申请门店名称
     */
    private String applyOrgName;

    /**
     * 改派门店id
     */
    private String reassignOrgId;

    /**
     * 改派门店名称
     */
    private String reassignOrgName;

    /**
     * 申请门店展示名称
     */
    private String applyOrgDisplayName;

    /**
     * 改派门店展示名称
     */
    private String reassignOrgDisplayName;

    /**
     * 审核时间 2023-10-11 12:23:45
     */
    private String auditTime;

    /**
     * 审核人mid
     */
    private Long auditMid;

    /**
     * 审核人姓�?
     */
    private String auditName;

    /**
     * 审核结果 审核通过 审核驳回
     */
    private String auditResult;

    /**
     * 解决方案
     */
    private String solutionDesc;

    /**
     * 结案标签
     */
    private List<String> finishTabList;

    /**
     * 驳回原因
     */
    private String auditReason;

    /**
     * 接单时间 2023-12-24 14:45:21
     */
    private String pickUpTime;

    /**
     * 接单人mid
     */
    private String orderReceiverMid;

    /**
     * 接单人姓�?
     */
    private String orderReceiverName;

    /**
     * 派单时间
     */
    private String dispatchTime;

    /**
     * 派单人mid
     */
    private String dispatcherMid;

    /**
     * 派单人姓�?
     */
    private String dispatcherName;

    /**
     * 跟进时间 2023-12-24 14:45:21
     */
    private String followUpTime;

    /**
     * 跟进人员mid
     */
    private String followUpMid;

    /**
     * 跟进人员姓名
     */
    private String followUpName;

    /**
     * 跟进详情
     */
    private String followUpContent;

    /**
     * 催单时间
     */
    private String remindOrderTime;

    /**
     * 催单人mid
     */
    private String orderReminderMid;

    /**
     * 催单人姓�?
     */
    private String orderReminderName;

    /**
     * 催单信息
     */
    private String orderRemindInfo;

    /**
     * 工单�?
     */
    private String stNo;

    /**
     * 维保单号
     */
    private String mrNo;

    /**
     * 维保单状�?
     */
    private Integer mrStatus;
    /**
     * 维保单状�?
     */
    private String mrStatusName;

    /**
     * 创建时间 2023-12-12 23:12:56
     */
    private String createTime;

    /**
     * 创建人mid
     */
    private String createMid;

    /**
     * 创建人姓�?
     */
    private String createName;

    /**
     * 联系人mid
     */
    private String contactMid;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系人电�?
     */
    private String contactPhoneNumber;

    /**
     * 预约时间
     */
    private String appointTime;

    /**
     * 预估交车时间
     */
    private String estimatedDeliveryTime;

    /**
     * 服务接待人mid
     */
    private String serviceReceiverMid;

    /**
     * 服务接待人姓�?
     */
    private String serviceReceiverName;

    /**
     * 问题描述
     */
    private String questionDescription;

    /**
     * 积分下发id
     */
    private Long distributionId;

    /**
     * 积分下发时间
     */
    private String distributionTime;

    /**
     * 积分批次
     */
    private Integer pointsBatch;

    /**
     * 积分发放�?
     */
    private String distributionMid;

    /**
     * 积分发放人姓�?
     */
    private String distributionName;

    /**
     * 积分数量
     */
    private Integer pointsQuantity;

    /**
     * 积分价�?
     */
    private Integer pointsAmount;

    /**
     * 积分审批状�?
     */
    private String pointsAuditStatus;

    /**
     * 积分审批状态名�?
     */
    private String pointsAuditStatusName;

    /**
     * 积分发放状�?
     */
    private String pointsDistributionStatus;

    /**
     * 积分发放状态名�?
     */
    private String pointsDistributionStatusName;

    /**
     * 附件列表
     */
    private List<AttachmentSoOut> attachments;

    /**
     * 操作人mid(判定�?
     */
    private String operateMid;

    /**
     * 操作人名�?判定)
     */
    private String operateName;

    /**
     * 操作说明(判定)
     */
    private String operateDesc;

    /**
     * 操作时间(判定�?
     */
    private String operateTime;

    /**
     * 判定结果 1:判定有效 2:判定无效
     * com.wt.complaint.manage.api.model.enums.JudgeTypeEnum
     */
    private Integer judgeResult;

    /**
     * 判定结果描述
     */
    private String judgeResultDesc;


    /**
     * 操作人岗位code
     */
    private String operatePositionId;
    /**
     * 操作人岗位name
     */
    private String operatePositionName;


    // 跟进
    /**
     * 工单跟进描述
     */
    private String followDesc;

    // 改派责任�?

    /**
     * 改派岗位
     */
    private Integer reassignOperatorPositionId;
    /**
     * 改派岗位name
     */
    private String reassignOperatorPositionName;

    /**
     * 改派人员mid
     */
    private Long reassignOperatorMid;
    /**
     * 改派人员name
     */
    private String reassignOperatorName;

    /**
     * 改派描述
     */
    private String reassignDesc;

    // 结案
    /**
     * 是否和解
     */
    private String reconciled;

    /**
     * 是否回访
     */
    private String revisited;

    /**
     * 结案描述
     */
    private String finishDesc;

    /**
     * 判责
     */
    private String responsible;

    /**
     * 判责说明
     */
    private String responsibleJudgeDesc;

    /**
     * 问题分类
     */
    private String problemCategory;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 跟进门店code
     */
    private String orgId;
    /**
     * 跟进门店name
     */
    private String orgName;

    /**
     * 跟进岗位id
     */
    private Integer operatorPositionId;
    /**
     * 跟进岗位name
     */
    private String operatorPositionName;

    /**
     * 升级投诉记录相关字段
     * 升级时间
     */
    private String upgradeTime;

    /**
     * 升级�?
     */
    private String upgraderName;

    /**
     * 升级原因
     */
    private String upgradeReason;

    /**
     * 原投诉类型描�?
     */
    private String originalTypeDesc;

    /**
     * 目标投诉类型描述
     */
    private String targetTypeDesc;

    /**
     * 投诉单信息更新记录相关字�?
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人mid
     */
    private Long updaterMid;

    /**
     * 更新人姓�?
     */
    private String updaterName;

    /**
     * 投诉场景：由"xxxx"更新�?xxxx"
     */
    private String complaintTypeChange;

    /**
     * 风险等级：由"xxxx"更新�?xxxx"
     */
    private String riskLevelChange;

    /**
     * 是否涉媒：由"xxxx"更新�?xxxx"
     */
    private String mediaInvolvedChange;

    /**
     * 涉媒链接：由"xxxx"更新�?xxxx"
     */
    private String mediaLinkChange;

    /**
     * 申请结案记录扩展字段
     * 是否与用户达成一致描�?
     */
    private String userAgreementDesc;

    /**
     * 车辆异常是否修复描述
     */
    private String vehicleRepairedDesc;

    /**
     * 涉媒信息描述
     */
    private String mediaInfoDesc;

    /**
     * 复盘材料，飞书链接（客诉三期 processType=SUBMIT_REVIEW�?
     */
    private String reviewMaterialUrl;

    /**
     * 当前审批节点（仅申请免责有效）：1-一�?2-二审 3-三审
     */
    private Integer currentNode;

    /**
     * 处理类型 1 已处�?2 无需门店处理
     */
    private Integer handleType;

    /**
     * 更新前的字段�?
     */
    private String beforeUpdate;

    /**
     * 更新后的字段�?
     */
    private String afterUpdate;
}
