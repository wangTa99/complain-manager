package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@AllArgsConstructor
@Getter
public enum AuditStatusEnum {

    PENDING(1, "待审�?),
    APPROVED(2, "已通过"),
    REJECTED(3, "已驳�?),
    CANCELLED(4, "已撤销");

    private final Integer code;
    private final String desc;


    public static String getDescByCode(Integer code) {
        for (AuditStatusEnum auditStatusEnum : AuditStatusEnum.values()) {
            if (auditStatusEnum.getCode().equals(code)) {
                return auditStatusEnum.getDesc();
            }
        }
        return "";
    }

    /**
     * 返回无法继续提出申请的状�?
     * @return
     */
    public static List<Integer> getNoApplyCodes() {
        List<Integer> processIngCodes = new ArrayList<>();
        processIngCodes.add(PENDING.getCode());
        processIngCodes.add(APPROVED.getCode());
        return processIngCodes;
    }
}
