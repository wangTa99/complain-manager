package com.wt.complaint.manage.api.model.enums;

import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 提醒次数选项枚举
 * @author huxiankang
 * @date 2025/6/13
 */
@Getter
@AllArgsConstructor
public enum TimeoutOptionEnum {

    NO(0, "未超�?),
    YES(1, "已超�?),
;
    private final Integer code;
    private final String name;

    /**
     * 根据code获取name
     * @param code code
     * @return name
     */
    public static String getDescByCode(Integer code) {
        for (TimeoutOptionEnum value : TimeoutOptionEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static List<CommonOptionResp> getCommonOptionList() {
        return Arrays.stream(TimeoutOptionEnum.values()).map(value ->
                CommonOptionResp.builder()
                        .statusCode(value.getCode())
                        .statusName(value.getName())
                        .build()
        ).collect(Collectors.toList());
    }
}
