package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BusinessTypeEnum {

    CAR("car", "汽车门店"),
    PHONE("phone", "3C门店");

    private final String code;
    private final String desc;
}
