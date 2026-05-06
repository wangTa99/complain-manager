package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 判定类型枚举�?
 * @author linjiehong
 * @date 2025/5/21 14:59
 */
@Getter
@AllArgsConstructor
public enum JudgeTypeEnum {
    NOT_JUDGE(0, "未判�?),

    JUDGE_VALID(1, "举报有效"),

    JUDGE_INVALID(2, "举报无效")

    ;
    private final int code;

    private final String desc;

    /**
     * 根据code获取desc
     * @param code 举报状态code
     * @return 举报状态描�?
     */
    public static String getDescByCode(Integer code) {
        for (JudgeTypeEnum value : JudgeTypeEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}
