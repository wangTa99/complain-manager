package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交付零售source字段枚举
 * @author zhangzheyang
 * @date 2025/6/24
 */
@Getter
@AllArgsConstructor
public enum DeliverRetailSourceEnum {

    DELIVER(0, "交付客诉�?),
    RETAIL(1, "零售客诉�?);

    private final Integer code;
    private final String desc;

}
