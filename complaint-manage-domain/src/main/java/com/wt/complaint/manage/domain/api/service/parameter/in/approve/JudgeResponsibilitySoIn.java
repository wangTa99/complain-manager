package com.wt.complaint.manage.domain.api.service.parameter.in.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务投诉判责领域入参
 *
 * @author generated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResponsibilitySoIn {

    @ApiDocClassDefine(value = "id", description = "审批单ID")
    private Long id;

    @ApiDocClassDefine(value = "responsible", description = "门店是否有责: 0-无责, 1-有责")
    private Integer responsible;

    @ApiDocClassDefine(value = "responsibleJudgeDesc", description = "审批意见")
    private String responsibleJudgeDesc;

    /**
     * 判责操作人mid（服务满意度管理岗位�?
     */
    private Long auditMid;

    /**
     * 投诉单号（由审批单带出，Service 层赋值，�?Manager 使用�?
     */
    private String complaintNo;
}
