package com.wt.complaint.manage.domain.api.enums;

/**
 * 岗位渠道枚举
 *
 * @author p-wangkai95
 * @version 1.0
 */
public enum CarChannelTypeEnum {

    CAR_SALE(4, "汽车销�?),
    CAR_DELIVERY(2, "汽车交付"),
    CAR_SERVICE(1, "汽车服务"),
    ALL(7, "全部"),
    CAR_BUSINESS(8, "汽车合作�?),
    CAR_CLIENT(9, "汽车客户"),
    ;

    private final Integer code;

    private final String desc;

    CarChannelTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}