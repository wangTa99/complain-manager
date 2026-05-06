package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客诉单创建来源（客诉三期�?
 */
@AllArgsConstructor
@Getter
public enum CreateSourceEnum {

    STORE(1, "服务门店"),
    ONLINE_CS(2, "线上客服");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (CreateSourceEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "";
    }
}
