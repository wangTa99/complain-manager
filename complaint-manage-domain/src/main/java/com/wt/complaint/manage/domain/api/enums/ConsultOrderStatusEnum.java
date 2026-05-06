package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConsultOrderStatusEnum {

    /**
     * 咨询单状�?1-待接�?2-待首�?3-待结�?4-已完�?
     */
    WAIT_RECEIVE(1, "待接�?),
    WAIT_FIRST_RESPONSE(2, "待首�?),
    WAIT_CLOSE(3, "待结�?),
    COMPLETED(4, "已完�?),
    ;

    private final Integer code;
    private final String desc;
}
