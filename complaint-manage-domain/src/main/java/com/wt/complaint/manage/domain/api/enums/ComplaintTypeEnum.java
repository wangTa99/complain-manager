package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ComplaintTypeEnum {

    PRODUCT_COMPLAINT(1, "产品投诉"),
    SERVICE_COMPLAINT(2, "服务投诉"),
    PRODUCT_RISK(3, "产品风险");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (ComplaintTypeEnum complaintTypeEnum : ComplaintTypeEnum.values()) {
            if (complaintTypeEnum.getCode().equals(code)) {
                return complaintTypeEnum.getDesc();
            }
        }
        return "";
    }

    /**
     * 根据code获取枚举
     * @param code code�?
     * @return 枚举对象
     */
    public static ComplaintTypeEnum getEnumByCode(Integer code) {
        for (ComplaintTypeEnum complaintTypeEnum : ComplaintTypeEnum.values()) {
            if (complaintTypeEnum.getCode().equals(code)) {
                return complaintTypeEnum;
            }
        }
        return null;
    }

}
