package com.wt.complaint.manage.api.model;

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
public class ClosingTag implements Serializable {
    @ApiDocClassDefine(value = "结案标签ID链路", description = "结案标签ID链路，用/连接�?/32/333/4513")
    private String tagId;
    @ApiDocClassDefine(value = "结案标签名称链路", description = "结案标签名称链路，用/连接�?/32/333/4513")
    private String tagName;
}
