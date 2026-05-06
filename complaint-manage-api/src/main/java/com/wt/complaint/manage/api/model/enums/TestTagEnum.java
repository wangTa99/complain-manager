package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TestTagEnum {
    NON_TEST(0, "非测试数�?),
    TEST(1, "测试数据");

    private final Integer code;
    private final String desc;
}
