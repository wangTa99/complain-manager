package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * 结案入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintFinishGoIn{
    @ApiDocClassDefine(value = "operatorPositionEnum", description = "角色名称", required = true)
    private DeliverPositionEnum operatorPositionEnum;

    @ApiDocClassDefine(value = "operatorMid", description = "操作人mid", required = true)
    private Long operatorMid;
    @ApiDocClassDefine(value = "operatorName", description = "操作人name", required = true)
    private String operatorName;

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    private String drNo;

    // 结案
    @ApiDocClassDefine(value = "reconciled", description = "是否和解", required = true)
    @NotNull(message = "reconciled不能为空")
    private Boolean reconciled;

    @ApiDocClassDefine(value = "revisited", description = "是否回访", required = true)
    @NotNull(message = "revisited不能为空")
    private Boolean revisited;

    @ApiDocClassDefine(value = "finishDesc", description = "结案描述", required = true)
    @NotBlank(message = "finishDesc不能为空")
    private String finishDesc;

    @ApiDocClassDefine(value = "finishAttachmentList", description = "结案附件")
    private List<AttachmentGoIn> finishAttachmentList;

    @ApiDocClassDefine(value = "isApplyExemption", description = "是否申请免责", required = true)
    @NotNull(message = "applyExemption不能为空")
    private Boolean applyExemption;

    @ApiDocClassDefine(value = "exemptionReason", description = "免责理由, applyExemption为true时必�?)
    private String exemptionReason;

    @ApiDocClassDefine(value = "applyExemptionAttachmentList", description = "申请免责附件")
    private List<AttachmentGoIn> applyExemptionAttachmentList;
}
