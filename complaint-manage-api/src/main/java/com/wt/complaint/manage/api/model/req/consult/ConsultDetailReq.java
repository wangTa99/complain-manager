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
public class ConsultDetailReq implements Serializable {

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号", required = true)
    @NotBlank(message = "consultNo不能为空")
    private String consultNo;

    @ApiDocClassDefine(value = "source", description = "来源, PAD_DETAIL:零售通pad-投诉单详�? AFTER_SALE_WORKBENCH:售后工作�?, required = true)
    @NotBlank(message = "source不能为空")
    private String source;


}
