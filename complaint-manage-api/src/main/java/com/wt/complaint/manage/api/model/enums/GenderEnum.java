package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum GenderEnum {
    MALE(1, "�?, "先生"),
    FEMALE(2, "�?, "女士"),
    UNKNOWN(3, "未知", "**");

    private final Integer code;
    private final String desc;
    private final String extend;

    public static String getDescByCode(Integer code) {
        for (GenderEnum value : GenderEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}
