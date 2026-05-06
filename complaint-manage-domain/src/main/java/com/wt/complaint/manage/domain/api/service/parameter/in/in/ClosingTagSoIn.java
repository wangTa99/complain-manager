package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClosingTagSoIn {
    /**
     * 结案标签ID链路，用/连接�?/32/333/4513
     */
    private String tagId;
    /**
     * 结案标签名称链路，用/连接�?/32/333/4513
     */
    private String tagName;
}
