package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FieldTypeEnum {
    SINGLE_TEXT(1, "单行文本输入�?),
    OPTION(2, "下拉列表�?),
    CASCADE_SELECTION(3, "联级选择�?),
    MULTI_TEXT(4, "多行文本输入�?),
    ATTACHMENT(5, "附件"),
    LINK(6, "链接");

    private Integer code;
    private String name;
}
