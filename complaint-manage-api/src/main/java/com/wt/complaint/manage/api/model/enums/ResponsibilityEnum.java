package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 售后客诉，有责无责相关枚�?
 *
 * @author zhangzheyang
 * @date 2024/12/25
 */
@AllArgsConstructor
@Getter
public enum ResponsibilityEnum {

    YES(1, "有责"),
    NO(0, "无责");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (ResponsibilityEnum responsibilityEnum : ResponsibilityEnum.values()) {
            if (responsibilityEnum.getCode().equals(code)) {
                return responsibilityEnum.getDesc();
            }
        }
        return "";
    }
}
