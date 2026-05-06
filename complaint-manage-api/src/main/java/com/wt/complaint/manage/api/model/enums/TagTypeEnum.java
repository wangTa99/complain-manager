package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TagTypeEnum {

    COMPLAINT_RATE_ASSESSMENT_FREE("COMPLAINT_RATE_ASSESSMENT_FREE", "投诉率免考核"),
    FINISH_72H_ASSESSMENT_FREE("FINISH_72H_ASSESSMENT_FREE", "72H无法结案"),
    FIRST_RESPONSE_TIMEOUT("FIRST_RESPONSE_TIMEOUT", "首响超时"),
    FINISH_TIMEOUT("FINISH_TIMEOUT", "结案超时"),
    STORE_RESPONSIBLE("STORE_RESPONSIBLE", "门店有责");

    private final String code;
    private final String desc;

    public static TagTypeEnum getByCode(String code) {
        for (TagTypeEnum tagTypeEnum : values()) {
            if (tagTypeEnum.getCode().equals(code)) {
                return tagTypeEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        for (TagTypeEnum tagTypeEnum : values()) {
            if (tagTypeEnum.getCode().equals(code)) {
                return tagTypeEnum.getDesc();
            }
        }
        return "";
    }
}
