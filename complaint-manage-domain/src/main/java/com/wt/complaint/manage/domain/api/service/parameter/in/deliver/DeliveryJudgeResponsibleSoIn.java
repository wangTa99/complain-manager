package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 判责入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryJudgeResponsibleSoIn {
    // 投诉单号
    private String drNo;

    // 判责结论 1-有责 2-无责
    private Integer responsible;

    // 判责理由
    private String responsibleJudgeDesc;

    // 判责�?mid
    private Long operateMid;
    private String operateName;

    // 申请�?岗位 ID
    private Integer operatePositionId;
    private String operatePositionName;

}
