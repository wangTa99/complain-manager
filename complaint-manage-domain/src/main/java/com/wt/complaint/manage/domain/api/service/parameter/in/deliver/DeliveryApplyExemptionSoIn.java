package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 申请免责入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryApplyExemptionSoIn {

    // 投诉单号
    private String drNo;

    // 申请免责理由
    private String exemptionReason;

    // 申请�?mid
    private Long applyMid;
    private String applyName;

    // 申请�?岗位 ID
    private Integer applyPositionId;
    private String applyPositionName;

    // 申请判责 附件列表
    private List<AttachmentGoIn> attachmentList;
}
