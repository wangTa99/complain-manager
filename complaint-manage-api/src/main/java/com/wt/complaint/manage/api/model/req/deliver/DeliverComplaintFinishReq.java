package com.wt.complaint.manage.api.model.req.deliver;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 交付投诉工单结案请求
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintFinishReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;

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
    private List<Attachment> finishAttachmentList;

    @ApiDocClassDefine(value = "isApplyExemption", description = "是否申请免责", required = true)
    @NotNull(message = "applyExemption不能为空")
    private Boolean applyExemption;

    @ApiDocClassDefine(value = "exemptionReason", description = "免责理由, applyExemption为true时必�?)
    private String exemptionReason;

    @ApiDocClassDefine(value = "applyExemptionAttachmentList", description = "申请免责附件")
    private List<Attachment> applyExemptionAttachmentList;
}
