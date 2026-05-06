package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理结果枚举�?
 */
@Getter
@AllArgsConstructor
public enum HandleResultEnum {

    NO_NEED_HANDLE(2, "无需门店处理"),
    HANDLED(1, "已处�?);

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (HandleResultEnum result : HandleResultEnum.values()) {
            if (result.getCode().equals(code)) {
                return result.getDesc();
            }
        }
        return null;
    }
}