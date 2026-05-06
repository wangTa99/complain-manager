package com.wt.complaint.manage.api.model.resp;

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
public class ClosingTagDTO implements Serializable {
    private static final long serialVersionUID = 3189845025924216677L;

    @ApiDocClassDefine(value = "closingTagIdLink", description = "结案标签id链路,�?连接,例如 1/2/3")
    private String closingTagIdLink;

    @ApiDocClassDefine(value = "closingTagNameLink", description = "结案标签名称链路,�?连接,例如 汽车/一般投�?)
    private String closingTagNameLink;
}
