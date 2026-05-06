package com.wt.complaint.manage.domain.api.enums;

import com.wt.complaint.manage.api.model.enums.ConsultTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriorityEnum {

    /**
     * 优先级，4 一般，8 高，16 紧�?
     */
    NORMAL(4, "一�?),
    HIGH(8, "�?),
    URGENT(16, "紧�?),
    ;

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (PriorityEnum type : PriorityEnum.values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return null;
    }
}
