package com.wt.complaint.manage.api.model.resp.deliver;

import com.wt.complaint.manage.api.model.req.operate.TemplateField;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉详情响应�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintDetailResp implements Serializable {


    // ----------------客诉情况字段---------------------

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    private String drNo;

    @ApiDocClassDefine(value = "tradeOrderId", description = "订单�?)
    private String tradeOrderId;

    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @ApiDocClassDefine(value = "contactPhone", description = "联系人电�?)
    private String contactPhone;

    @ApiDocClassDefine(value = "customerServiceName", description = "客服人员姓名")
    private String customerServiceName;

    @ApiDocClassDefine(value = "problemCategory", description = "问题分类")
    private String problemCategory;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级")
    private String riskLevel;

    @ApiDocClassDefine(value = "riskLevelName", description = "风险等级")
    private String riskLevelName;

    @ApiDocClassDefine(value = "problemDesc", description = "投诉详情")
    private String problemDesc;// 从complaint_content中取

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private Long createTime;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "lastReminderTime", description = "新催单时�?)
    private Long lastReminderTime;

    @ApiDocClassDefine(value = "lastReminderDesc", description = "新催单描�?)
    private String lastReminderDesc;

    @ApiDocClassDefine(value = "customFields", description = "客服系统自定义字�?)
    private List<TemplateField> customFields;

    // ----------------响应情况字段---------------------
    @ApiDocClassDefine(value = "orderStatus", description = "投诉单状态code, 10-待首�?20-跟进�?50-已结�?)
    private Integer orderStatus;

    @ApiDocClassDefine(value = "orderStatusName", description = "投诉单状态name")
    private String orderStatusName;

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

    @ApiDocClassDefine(value = "reassignmentTimes", description = "改派次数")
    private Integer reassignmentTimes;

    @ApiDocClassDefine(value = "lastReassignmentTime", description = "最新改派时�?)
    private Long lastReassignmentTime;

    @ApiDocClassDefine(value = "lastReassignmentDesc", description = "最新改派描�?)
    private String lastReassignmentDesc;

    // ----------------判责情况字段---------------------

    @ApiDocClassDefine(value = "responsible", description = "判责状�?  1-有责 2-无责 3-待判�?)
    private Integer responsible;

    @ApiDocClassDefine(value = "responsibleName", description = "判责状态name")
    private String responsibleName;

    @ApiDocClassDefine(value = "exemptionReason", description = "申免责理�?)
    private String exemptionReason;

    @ApiDocClassDefine(value = "responsibleJudgeDesc", description = "判责说明")
    private String responsibleJudgeDesc;

    @ApiDocClassDefine(value = "lastFollowDesc", description = "最新一条跟�?)
    private String lastFollowDesc;

    @ApiDocClassDefine(value = "progressBar", description = "进度条相关字�?)
    private ProgressBarDTO progressBar;

    @ApiDocClassDefine(value = "buttonList", description = "操作按钮列表")
    private List<Button> buttonList;

    @ApiDocClassDefine(value = "systemTime", description = "系统时间")
    private long systemTime;

    /**
     * 详情按钮
     */
    @Data
    public static class Button implements Serializable {

        @ApiDocClassDefine(value = "buttonKey", description = "操作key")
        private String buttonKey;

        @ApiDocClassDefine(value = "buttonName", description = "操作名称")
        private String buttonName;

        @ApiDocClassDefine(value = "disabled", description = "是否禁用, true禁用, false不禁�?)
        private boolean disabled;

    }
}
