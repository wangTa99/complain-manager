package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailSubmitFinishApplySoIn implements Serializable {

    // 客诉单号
    private String drNo;

    // 申请门店ID
    private String applyOrgId;

    // 是否和解
    private Integer isReconcile;

    // 是否可回�?
    private String canBeRevisited;

    // 解决方案
    private String solutionDesc;

    // 附件列表
    private List<Attachment> attachmentList;

    // 单据状�?
    private Integer orderStatus;

    // 申请结案�?
    private Long operatorMid;

    // 风险等级
    private RiskLevelEnum riskLevel;

    // 申请人姓�?
    private String applyName;

    //催单标识
    private Integer reminderFlag;

    /** ---------------- 只有 BPM 使用字段 ------------- **/
    // 联系人姓�?投诉)
    private String contactName;

    // 联系人电�?
    private String contactTel;

    // 投诉类型 (产品投诉 服务投诉)
    private String complaintTypeName;

    // 问题分类
    private String problemCategory;

    // 投诉门店名称
    private String orgName;

    // 问题详情
    private String questionDesc;

    // 投诉场景
    private String complaintScene;

    /**
     * 大区id
     * 对应省分公司标识
     */
    private Integer zoneId;
    /**
     * 小区id
     */
    private Integer littleZoneId;

}
