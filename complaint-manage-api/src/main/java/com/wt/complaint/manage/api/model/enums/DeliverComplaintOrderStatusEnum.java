package com.wt.complaint.manage.api.model.enums;

import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 交付侧客诉工单状�?
 *
 * @author huxiankang
 * @date 2025/6/11
 */
@Getter
@AllArgsConstructor
public enum DeliverComplaintOrderStatusEnum {

    WAITING_FIRST_RESPONSE(10, "待首�?),
    HANDLING(20, "跟进�?),
    FINISHED(50, "已结�?);

    private final Integer code;
    private final String name;

    /**
     * 根据code获取desc
     * @param code 状态code
     * @return desc
     */
    public static String getDescByCode(Integer code) {
        for (DeliverComplaintOrderStatusEnum value : DeliverComplaintOrderStatusEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getName();
            }
        }
        return null;
    }

    /**
     * 根据code获取枚举
     * @param code 状态code
     * @return 枚举
     */
    public static DeliverComplaintOrderStatusEnum getEnumByCode(Integer code) {
        for (DeliverComplaintOrderStatusEnum value : DeliverComplaintOrderStatusEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取前端下拉选状态列�?
     * @return 前端下拉选状态列�?
     */
    public static List<CommonOptionResp> getCommonOptionList() {
        return Arrays.stream(DeliverComplaintOrderStatusEnum.values()).map(value ->
            CommonOptionResp.builder()
                    .statusCode(value.getCode())
                    .statusName(value.getName())
                    .build()
        ).collect(Collectors.toList());
    }

    /**
     * 返回需要计算超时的状�?
     */
    public static List<Integer> getTagNeedStatus() {
        return Arrays.asList(WAITING_FIRST_RESPONSE.getCode(), HANDLING.getCode(), FINISHED.getCode());
    }

}
