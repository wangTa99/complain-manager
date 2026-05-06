package com.wt.complaint.manage.domain.enumInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 工单完成消息状态枚�?
 */
@Getter
@AllArgsConstructor
public enum WorkFinishTypeEnum {

    COMPLETED("完成", 1),
    CANCEL("取消", 2);

    private String desc;
    private Integer code;

    public static String getDescByCode(Integer code) {
        for (WorkFinishTypeEnum value : WorkFinishTypeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}

