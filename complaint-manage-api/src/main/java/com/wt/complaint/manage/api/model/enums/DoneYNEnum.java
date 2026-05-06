package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DoneYNEnum {
    YES(1, "已完�?),
    NO(0, "未完�?);

    private final Integer code;
    private final String desc;
}
