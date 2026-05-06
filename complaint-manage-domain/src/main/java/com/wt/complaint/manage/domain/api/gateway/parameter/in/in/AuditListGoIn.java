package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditListGoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;
    /**
     * 申请类型 1 申请改派门店 2 申请72H无法结案 3 申请免责 4 申请结案
     */
    private String auditType;
    /**
     * 审批状�? 0 默认 1 审核�?2 通过 3 驳回
     */
    private Integer auditStatus;
}
