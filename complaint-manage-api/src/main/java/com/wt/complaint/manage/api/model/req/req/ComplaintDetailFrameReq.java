package com.wt.complaint.manage.api.model.req;

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
public class ComplaintDetailFrameReq implements Serializable {

    private static final long serialVersionUID = -1812249032902185150L;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号", required = true)
    @NotBlank(message = "complaintNo 不能为空")
    private String complaintNo;

    @ApiDocClassDefine(value = "source",
            description = "来源, PAD_DETAIL:零售通pad-投诉单详�? AFTER_SALE_WORKBENCH:售后工作�?,
            required = true)
    @NotBlank(message = "source不能为空")
    private String source;

    @ApiDocClassDefine(value = "orgId", description = "门店 ID", required = true)
    // todo: 灰度之后需要去掉这个字�?
    private String orgId;
}
