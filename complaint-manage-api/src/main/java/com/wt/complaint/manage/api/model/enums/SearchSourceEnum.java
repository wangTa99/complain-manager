package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 搜索来源枚举
 */
@Getter
@AllArgsConstructor
public enum SearchSourceEnum {

    AFTER_SALE_WORKBENCH_REPORT_ORDER("AFTER_SALE_WORKBENCH_REPORT_ORDER", "售后工作�?举报�?);

    private final String code;
    private final String desc;

    /**
     * 根据code获取枚举�?
     * @param code 枚举code
     * @return 枚举�?
     */
    public static SearchSourceEnum getByCode(String code) {
        for (SearchSourceEnum sourceEnum : values()) {
            if (sourceEnum.getCode().equals(code)) {
                return sourceEnum;
            }
        }
        return null;
    }
}
