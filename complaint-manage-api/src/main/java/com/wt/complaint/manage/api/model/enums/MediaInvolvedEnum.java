package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否涉媒枚举
 */
@Getter
@AllArgsConstructor
public enum MediaInvolvedEnum {
    NO(0, "�?),
    YES(1, "�?);

    private final Integer code;
    private final String desc;

    /**
     * 根据code获取描述
     * @param code 编码
     * @return 描述
     */
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return NO.getDesc();
        }
        for (MediaInvolvedEnum mediaInvolvedEnum : MediaInvolvedEnum.values()) {
            if (mediaInvolvedEnum.getCode().equals(code)) {
                return mediaInvolvedEnum.getDesc();
            }
        }
        return NO.getDesc();
    }

    /**
     * 根据code获取枚举
     * @param code 编码
     * @return 枚举
     */
    public static MediaInvolvedEnum fromCode(Integer code) {
        if (code == null) {
            return NO;
        }
        for (MediaInvolvedEnum mediaInvolvedEnum : MediaInvolvedEnum.values()) {
            if (mediaInvolvedEnum.getCode().equals(code)) {
                return mediaInvolvedEnum;
            }
        }
        return NO;
    }

    /**
     * 根据字符串code获取描述
     * @param codeStr 字符串编�?
     * @return 描述
     */
    public static String getDescByCodeStr(String codeStr) {
        if (codeStr == null || codeStr.trim().isEmpty()) {
            return NO.getDesc();
        }
        try {
            Integer code = Integer.valueOf(codeStr);
            return getDescByCode(code);
        } catch (NumberFormatException e) {
            return NO.getDesc();
        }
    }
}
