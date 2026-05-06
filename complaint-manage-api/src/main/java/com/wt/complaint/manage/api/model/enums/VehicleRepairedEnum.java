package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 车辆修复状态枚�?
 */
@AllArgsConstructor
@Getter
public enum VehicleRepairedEnum {

    NO(0, "�?),
    YES(1, "�?),
    NOT_INVOLVED(2, "不涉�?),
    UNKNOWN(3, "未知");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (VehicleRepairedEnum vehicleRepairedEnum : VehicleRepairedEnum.values()) {
            if (vehicleRepairedEnum.getCode().equals(code)) {
                return vehicleRepairedEnum.getDesc();
            }
        }
        return "";
    }

    public static VehicleRepairedEnum getByCode(Integer code) {
        for (VehicleRepairedEnum vehicleRepairedEnum : VehicleRepairedEnum.values()) {
            if (vehicleRepairedEnum.getCode().equals(code)) {
                return vehicleRepairedEnum;
            }
        }
        return null;
    }
}
