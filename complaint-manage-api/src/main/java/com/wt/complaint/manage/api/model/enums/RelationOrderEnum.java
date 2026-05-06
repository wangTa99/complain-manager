package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RelationOrderEnum {
    MR_NO(1, "维保�?),

    SUPER_TICKET_NO(2, "超级工单");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (RelationOrderEnum relationOrderEnum : RelationOrderEnum.values()) {
            if (relationOrderEnum.getCode().equals(code)) {
                return relationOrderEnum.getDesc();
            }
        }
        return "";
    }
}
