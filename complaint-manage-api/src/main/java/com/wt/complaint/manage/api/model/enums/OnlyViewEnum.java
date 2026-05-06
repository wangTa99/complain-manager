package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 投诉单是否门店仅查阅
 */
@AllArgsConstructor
@Getter
public enum OnlyViewEnum {

    YES(1, "�?仅查�?不需要门店处�?),
    NO(0, "�?需要门�?);

    private final Integer code;
    private final String desc;
}
