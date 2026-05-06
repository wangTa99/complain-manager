package com.wt.complaint.manage.domain.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚�?
 * @author zhangzheyang
 * @date 2025/6/20
 */
@Getter
@AllArgsConstructor
public enum UserStateEnum {
    INVALID(0, "无效"),
    VALID(1, "有效"),
    FROZEN(2, "冻结");

    private Integer code;
    private String desc;


}
