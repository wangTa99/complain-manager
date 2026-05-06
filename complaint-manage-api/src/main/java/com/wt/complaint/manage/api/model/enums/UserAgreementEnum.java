package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户达成一致枚�?
 */
@AllArgsConstructor
@Getter
public enum UserAgreementEnum {

    NO(0, "�?),
    YES(1, "�?),
    UNKNOWN(2, "未知");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (UserAgreementEnum userAgreementEnum : UserAgreementEnum.values()) {
            if (userAgreementEnum.getCode().equals(code)) {
                return userAgreementEnum.getDesc();
            }
        }
        return "";
    }

    public static UserAgreementEnum getByCode(Integer code) {
        for (UserAgreementEnum userAgreementEnum : UserAgreementEnum.values()) {
            if (userAgreementEnum.getCode().equals(code)) {
                return userAgreementEnum;
            }
        }
        return null;
    }
}
