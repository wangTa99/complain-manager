package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ConsultStatusEnum {
//    1-待接�?2-待首�?3-待结�?4-已完�?
    PENDING_ORDER(1, "待接�?,"待接�?, "已接�?),
    FIRST_RESPONSE_PENDING(2, "待首�?,"待首�?, "已首�?),
    FINISH_PENDING(3, "待结�?,"待结�?, "已结�?),
    FINISH_COMPLETE(4, "已完�?,"已完�?, "已完�?);
    private final Integer code;
    private final String desc;
    private final String barFutureDesc;
    private final String barBeenDesc;

    /**
     * 获取未完成状�?
     * @return
     */
    public static List<Integer> getUnfinishedStatus() {
        return Arrays.asList(FIRST_RESPONSE_PENDING.getCode(), FINISH_PENDING.getCode());
    }
}
