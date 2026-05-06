package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailApplyRetailCallBackSoIn {

    // bpm Id
    private String processInstanceId;

    // 执行 序列
    private String taskNo;

    // 操作�?
    private String operator;

    // 操作类型
    private ProcessAction action;

    // 拒绝原因
    private String refuseReason;

    // 是否完成
    private Boolean finished;

    // 拓展字段
    private Map<String, Object> extra;

    // 单据状�?
    private Integer orderStatus;

    // 客诉单单�?
    private String drNo;

    // 操作�?mid
    private Long auditMid;

    // 结案人姓�?
    private String auditName;

}
