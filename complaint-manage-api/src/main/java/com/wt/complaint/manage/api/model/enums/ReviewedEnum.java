package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否已复盘枚�?
 * @author zhangzheyang
 * @date 2026/3/10
 */
@AllArgsConstructor
@Getter
public enum ReviewedEnum {

    NO(0, "�?),
    YES(1, "�?);

    private final Integer code;
    private final String desc;
}
