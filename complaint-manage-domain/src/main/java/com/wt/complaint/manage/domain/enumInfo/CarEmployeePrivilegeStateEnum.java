package com.wt.complaint.manage.domain.enumInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 组织中台角色枚举
 */
@Getter
@AllArgsConstructor
public enum CarEmployeePrivilegeStateEnum {

    INVALID("无效", 0),
    VALID("有效", 1);

    private String desc;
    private Integer code;

    public static String getDescByCode(Integer code) {
        for (CarEmployeePrivilegeStateEnum value : CarEmployeePrivilegeStateEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }

}
