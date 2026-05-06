package com.wt.complaint.manage.domain.enumInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PrivilegeStateEnun {

    INVALID(0, "无效"),
    EFFECTIVE(1, "有效"),
    LOCK(2, "临时锁定"),
    LOCK_LEAVE(3, "离职锁定");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (PrivilegeStateEnun value : PrivilegeStateEnun.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}
