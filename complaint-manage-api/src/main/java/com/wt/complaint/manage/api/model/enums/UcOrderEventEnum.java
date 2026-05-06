package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Uc单操作事件枚�?
 * @author linjiehong
 * @date 2025/5/21 15:13
 */
@Getter
@AllArgsConstructor
public enum UcOrderEventEnum {
    PICKUP_ORDER(1,"接单", ProcessTypeEnum.PICKUP_ORDER),

    REMIND_ORDER(2,"催单", ProcessTypeEnum.REMIND),

    ADD_FOLLOW_RECORD(3,"添加跟进记录", ProcessTypeEnum.ADD_FOLLOW_RECORD),

    JUDGE_ORDER(4,"判定", ProcessTypeEnum.REPORT_JUDGE),

    CREATE_ORDER(5, "创建", null)
    ;
    private final int code;

    private final String desc;

    private final ProcessTypeEnum processType;
}
