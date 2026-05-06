package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 举报场景枚举�?
 * @author linjiehong
 * @date 2025/5/21 21:16
 */
@Getter
@AllArgsConstructor
public enum ServiceSceneEnum {
    EXCESSIVE_MAINTENANCE(1, "过度维修"),

    SUBSTANDARD_FOR_GOOD(2, "以次充好")

    ;
    private final int code;

    private final String desc;

    /**
     * 根据code获取desc
     * @param code 举报场景code
     * @return 举报场景描述
     */
    public static ServiceSceneEnum getByCode(int code) {
        for (ServiceSceneEnum sceneEnum : ServiceSceneEnum.values()) {
            if (sceneEnum.getCode() == code) {
                return sceneEnum;
            }
        }
        return null;
    }
}
