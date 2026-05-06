package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交付客诉单进度条状态枚�?
 * @author huxiankang
 * @date 2025/6/11
 */
@Getter
@AllArgsConstructor
public enum ProgressStatusEnum {

    WAITING_FIRST_RESPONSE(DeliverComplaintOrderStatusEnum.WAITING_FIRST_RESPONSE.getCode(), "待首�?),
    HANDLING(DeliverComplaintOrderStatusEnum.HANDLING.getCode(), "跟进�?),
    PENDING_JUDGE(45, "待判�?),
    FINISHED(DeliverComplaintOrderStatusEnum.FINISHED.getCode(), "已结�?);

    private final Integer code;
    private final String name;


}
