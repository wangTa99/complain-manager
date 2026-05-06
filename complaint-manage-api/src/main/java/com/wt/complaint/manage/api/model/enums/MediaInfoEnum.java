package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 涉媒信息枚举
 */
@AllArgsConstructor
@Getter
public enum MediaInfoEnum {

    USER_DELETED(1, "用户已删�?),
    USER_NOT_DELETED(2, "用户未删�?),
    NOT_INVOLVED(3, "不涉�?);

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (MediaInfoEnum mediaInfoEnum : MediaInfoEnum.values()) {
            if (mediaInfoEnum.getCode().equals(code)) {
                return mediaInfoEnum.getDesc();
            }
        }
        return "";
    }

    public static MediaInfoEnum getByCode(Integer code) {
        for (MediaInfoEnum mediaInfoEnum : MediaInfoEnum.values()) {
            if (mediaInfoEnum.getCode().equals(code)) {
                return mediaInfoEnum;
            }
        }
        return null;
    }
}
