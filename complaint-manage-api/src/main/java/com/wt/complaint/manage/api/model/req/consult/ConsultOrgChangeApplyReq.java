package com.wt.complaint.manage.api.model.req.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultOrgChangeApplyReq implements Serializable {
    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    @NotBlank(message = "咨询单号不能为空")
    private String consultNo;

    @ApiDocClassDefine(value = "applyOrgId", description = "申请门店id")
    @NotBlank(message = "申请门店id不能为空")
    private String applyOrgId;

    @ApiDocClassDefine(value = "desOrgId", description = "申请要改派到的门店id")
    @NotBlank(message = "申请要改派到的门店id不能为空")
    private String desOrgId;

    @ApiDocClassDefine(value = "reassignRemark", description = "改派说明")
    @NotBlank(message = "改派说明不能为空")
    private String reassignRemark;
}
