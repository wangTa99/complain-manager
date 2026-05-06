package com.wt.complaint.manage.domain.api.service.parameter.out;

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
public class ClosingTagSoOut implements Serializable {
    private static final long serialVersionUID = 3189845025924216677L;

    /**
     * 结案标签id链路,�?连接,例如 1/2/3
     */
    private String closingTagIdLink;

    /**
     * 结案标签名称链路,�?连接,例如 汽车/一般投�?
     */
    private String closingTagNameLink;
}
