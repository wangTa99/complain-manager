package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 紧急程度枚举类
 */
@Getter
@AllArgsConstructor
public enum UrgencyLevelEnum {

    NORMAL(4, "一�?),
    HIGH(8, "�?),
    URGENT(16, "紧�?);

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (UrgencyLevelEnum level : UrgencyLevelEnum.values()) {
            if (level.getCode().equals(code)) {
                return level.getDesc();
            }
        }
        return null;
    }
}