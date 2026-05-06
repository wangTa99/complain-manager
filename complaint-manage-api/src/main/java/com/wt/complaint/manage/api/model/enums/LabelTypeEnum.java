package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


@Getter
@AllArgsConstructor
public enum LabelTypeEnum {

    CAR_LABEL("汽车标签", 1),
    PERSON_LABEL("人员标签", 2);

    private String desc;
    private Integer code;

    public static String getDescByCode(Integer code) {
        for (LabelTypeEnum value : LabelTypeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }

}

