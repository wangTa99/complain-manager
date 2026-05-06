package com.wt.complaint.manage.api.model.resp.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.util.List;

@Data
public class ConsultSelectorResp {

    @ApiDocClassDefine(value = "consultStatusEnum", description = "咨询单状态枚举列�?)
    private List<SelectorItem> consultStatusEnum;

    @ApiDocClassDefine(value = "consultTypeEnum", description = "咨询类型枚举列表")
    private List<SelectorItem> consultTypeEnum;

    @ApiDocClassDefine(value = "urgencyLevelEnum", description = "紧急程度枚举列�?)
    private List<SelectorItem> urgencyLevelEnum;

    @ApiDocClassDefine(value = "handleResultEnum", description = "处理结果枚举列表")
    private List<SelectorItem> handleResultEnum;
}
