package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 咨询类型枚举�?后续放到nacos)
 */
@Getter
@AllArgsConstructor
public enum ConsultTypeEnum {

    REPAIR_PART_QUOTE(1, "维修/配件报价"),
    REPAIR_DURATION(2, "维修时长"),
    VEHICLE_PART_FUNCTION(3, "车辆配件作用"),
    PROCESS_NORMAL(4, "车辆工艺是否正常"),
    QUALITY_ISSUE_CAR_PICK_CONFIRM(5, "质量问题取车确认"),
    PERSONAL_NEED(6, "个性需�?维修发票/午餐/充电�?"),
    CAPACITY_CONSULT(7,"产能咨询");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (ConsultTypeEnum type : ConsultTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return null;
    }
}