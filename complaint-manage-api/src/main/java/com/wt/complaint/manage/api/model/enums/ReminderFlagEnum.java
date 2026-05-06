package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReminderFlagEnum {

    TRUE(1, "Êò?),
    FALSE(0, "Âê?);

    private final Integer code;
    private final String desc;
}
