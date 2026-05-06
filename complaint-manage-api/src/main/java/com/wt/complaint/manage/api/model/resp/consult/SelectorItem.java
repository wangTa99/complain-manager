package com.wt.complaint.manage.api.model.resp.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

@Data
public class SelectorItem {

    @ApiDocClassDefine(value = "key", description = "枚举 code")
    private Integer key;

    @ApiDocClassDefine(value = "value", description = "枚举描述")
    private String value;
}
