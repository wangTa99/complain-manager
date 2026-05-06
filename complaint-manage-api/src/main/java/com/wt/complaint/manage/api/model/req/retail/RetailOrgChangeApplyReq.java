package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 零售投诉改派门店申请请求参数
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailOrgChangeApplyReq implements Serializable {
    @ApiDocClassDefine(value = "drNo", description = "客诉单号")
    @NotBlank(message = "客诉单号不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "applyOrgId", description = "申请门店id")
    @NotBlank(message = "申请门店id不能为空")
    private String applyOrgId;

    @ApiDocClassDefine(value = "desOrgId", description = "申请要改派到的门店id")
    @NotBlank(message = "申请要改派到的门店id不能为空")
    private String desOrgId;

    @ApiDocClassDefine(value = "reassignRemark", description = "改派说明")
    @NotBlank(message = "改派说明不能为空")
    private String reassignRemark;

    public void checkReq() {
        if (!this.drNo.startsWith("RC")) {
            throw new IllegalArgumentException("非零售客诉单, 请联系管理员");
        }
        if (this.reassignRemark.length() > 200) {
            throw new IllegalArgumentException("改派说明请保�?200 字以�?);
        }
    }
}
