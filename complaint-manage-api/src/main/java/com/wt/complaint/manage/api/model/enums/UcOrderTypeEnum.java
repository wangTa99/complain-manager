package com.wt.complaint.manage.api.model.enums;

import com.wt.car.soc.api.constant.WorkTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Uc单据类型枚举
 * @author linjiehong
 * @date 2025/5/21 14:55
 */
@Getter
@AllArgsConstructor
public enum UcOrderTypeEnum {
    COMPLAINT_ORDER(1, "投诉�?, "TS"),

    REPORT_ORDER(2, "举报�?, "RP"),

    DELIVER_COMPLAINT_ORDER(3, "交付客诉�?, "DR"),

    RETAIL_COMPLAINT_ORDER(4, "零售客诉�?, "RC"),

    CONSULT_ORDER(5, "咨询�?, "ZX")
    ;

    private final int code;

    private final String desc;

    private final String prefix;

    /**
     * 根据类型获取单据枚举
     * @param code 单据类型code
     * @return 单据枚举
     */
    public static UcOrderTypeEnum getByCode(int code) {
        for (UcOrderTypeEnum typeEnum : UcOrderTypeEnum.values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 根据ucNo获取单据枚举
     * @param ucNo 单据编号
     * @return 单据枚举
     */
    public static UcOrderTypeEnum getByUcNo(String ucNo) {
        if (ucNo == null) {
            return null;
        }
        for (UcOrderTypeEnum typeEnum : UcOrderTypeEnum.values()) {
            if (ucNo.startsWith(typeEnum.getPrefix())) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 根据工单类型和场景类型获取单据类�?
     * @param workType 工单类型
     * @param serviceScene 场景类型
     * @return 单据类型
     */
    public static UcOrderTypeEnum mapToUcOrderTypeEnum(Integer workType, List<String> serviceScene) {
        if (WorkTypeEnum.USER_SUPERVISION.id == workType) {
            return UcOrderTypeEnum.REPORT_ORDER;
        } else if (WorkTypeEnum.COMPLAINT.id == workType) {
            return UcOrderTypeEnum.COMPLAINT_ORDER;
        }

        return null;
    }
}
