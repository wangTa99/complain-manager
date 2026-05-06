package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PadTabEnum {

    TOTAL(1, "全部"),
    PENDING_ORDER(2, "待接�?),
    IN_PROGRESS(3, "处理�?),
    APPROACHING_TIMEOUT(4, "即将超时"),
    FINISH_EVALUATION_PENDING(5, "待结案评�?),
    ONLY_VIEW(6, "仅查�?),
    /**
     * 客诉三期：待复盘，展示服务投诉未复盘单（reviewed=0�?
     */
    PENDING_REVIEW(8, "待复�?);

    private final Integer code;
    private final String desc;

    public static PadTabEnum getByCode(Integer code) {
        for (PadTabEnum padTabEnum : PadTabEnum.values()) {
            if (padTabEnum.getCode().equals(code)) {
                return padTabEnum;
            }
        }
        return null;
    }
}
