package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceEnum {
    PAD_DETAIL("PAD_DETAIL", "零售通pad-投诉单详�?),
    PAD_LIST("PAD_LIST", "零售通PAD�?投诉单列�?),
    PAD_RELATE_LIST("PAD_RELATE_LIST", "零售通PAD�?新建工单时关联客诉单列表"),
    AFTER_SALE_WORKBENCH("AFTER_SALE_WORKBENCH", "售后工作�?),
    CUSTOMER_SERVICE_WORKBENCH("CUSTOMER_SERVICE_WORKBENCH", "客服工作�?);

    private final String code;
    private final String desc;

    public static SourceEnum getByCode(String code) {
        for (SourceEnum sourceEnum : values()) {
            if (sourceEnum.getCode().equals(code)) {
                return sourceEnum;
            }
        }
        return null;
    }
}
