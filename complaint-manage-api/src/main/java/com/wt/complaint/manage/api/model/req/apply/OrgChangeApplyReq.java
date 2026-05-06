package com.wt.complaint.manage.api.model.req.apply;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgChangeApplyReq implements Serializable {
    @ApiDocClassDefine(value = "客诉单号", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "申请门店id", description = "申请门店id")
    private String applyOrgId;

    @ApiDocClassDefine(value = "申请要改派到的门店id", description = "申请要改派到的门店id")
    private String desOrgId;

    @ApiDocClassDefine(value = "改派说明", description = "改派说明")
    private String reassignRemark;
}
