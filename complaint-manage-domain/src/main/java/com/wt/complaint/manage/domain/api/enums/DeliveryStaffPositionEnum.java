package com.wt.complaint.manage.domain.api.enums;

/**
 * 交付专员岗位枚举�?
 */
public enum DeliveryStaffPositionEnum {

    POSITION_A(1, "A�?),
    POSITION_B(2, "B�?);

    /**
     * 岗位状态码�?�?
     */
    public final Integer code;

    /**
     * 岗位描述：A岗、B�?
     */
    public final String desc;

    DeliveryStaffPositionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
