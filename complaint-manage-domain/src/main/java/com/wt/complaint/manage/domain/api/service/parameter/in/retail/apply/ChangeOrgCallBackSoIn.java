package com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply;

import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import com.xiaomi.newretail.bpm.api.model.callback.TaskNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeOrgCallBackSoIn {
    private String drNo;
    private String orgId;
    private Integer zoneId;
    /**
     * 小区id
     */
    private Integer littleZoneId;

    /**
     * 城市id
     */
    private Integer cityId;
    // 门店跟进�?
    private Long orgFollowMid;
    // 门店跟进人职�?
    private Integer orgFollowPositionId;
    private Integer orderStatus;
    // 风险等级 1-4对应L1-L4
    private Integer riskLevel;
    private String processInstanceId;
    private String taskNo;
    /**
     * 审核人邮箱前缀
     */
    private String operator;
    /**
     * 审核人mid
     */
    private Long operatorMid;
    /**
     * 审核人姓�?
     */
    private String operatorName;
    /**
     * 改派次数，失败成功都�?
     */
    private Integer reassignmentTimes;
    private ProcessAction action;
    private String refuseReason;
    private Boolean finished;
    private Map<String, Object> extra;
    private String processDefinitionKey;
    private TaskNodeType taskNodeType;
}
