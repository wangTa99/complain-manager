package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnquireTypeEnum {

    REPAIR_QUOTATION(1, "维修/配件报价"),
    REPAIR_DURATION(2, "维修时长"),
    PARTS_FUNCTION(3, "车辆配件作用"),
    PROCESS_NORMAL(4, "车辆工艺是否正常"),
    QUALITY_CONFIRM(5, "质量问题取车确认"),
    PERSONAL_REQUEST(6, "个性需�?维修发票/午餐/充电�?"),
    CAPACITY_CONSULT(7, "产能咨询");

    public final Integer code;

    public final String desc;
}
