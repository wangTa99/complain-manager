package com.wt.complaint.manage.api.model.enums;

import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import com.xiaomi.youpin.infra.rpc.exception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RiskLevelEnum {
    LEVEL_1(1, "L1", 24),
    LEVEL_2(2, "L2", 24),
    LEVEL_3(3, "L3", 24),
    LEVEL_4(4, "L4", 24);

    private final Integer code;
    private final String desc;
    private Integer delayHours;

    public static String getDescByCode(Integer code) {
        for (RiskLevelEnum riskLevelEnum : RiskLevelEnum.values()) {
            if (riskLevelEnum.getCode().equals(code)) {
                return riskLevelEnum.getDesc();
            }
        }
        return "";
    }

    public static Integer getCodeByDesc(String desc) {
        for (RiskLevelEnum riskLevelEnum : RiskLevelEnum.values()) {
            if (riskLevelEnum.getDesc().equals(desc)) {
                return riskLevelEnum.getCode();
            }
        }
        throw new IllegalArgumentException("非法的风险等级描�? " + desc);
    }

    /**
     * 根据风险描述确定风险等级
     * @param code 风险描述
     * @return 风险等级枚举�?
     */
    public static RiskLevelEnum fromCode(Integer code) {
        for (RiskLevelEnum riskLevelEnum : RiskLevelEnum.values()) {
            if (riskLevelEnum.getCode().equals(code)) {
                return riskLevelEnum;
            }
        }
        throw new IllegalArgumentException("非法的风险等级枚�? " + code);
    }

    public static List<Integer> getLowLevel() {
        return Arrays.asList(LEVEL_1.getCode(), LEVEL_2.getCode());
    }

    public static List<Integer> getHighLevel() {
        return Arrays.asList(LEVEL_3.getCode(), LEVEL_4.getCode());
    }

    /**
     * 校验当前等级是否高等�?
     * @param riskLevelCode 风险等级code, 1,2,3,4
     * @return 是否高等�?
     */
    public static boolean checkHighLevel(Integer riskLevelCode) {
        return LEVEL_3.getCode().equals(riskLevelCode) || LEVEL_4.getCode().equals(riskLevelCode);
    }

    public static List<CommonOptionResp> getCommonOptionList() {
        return Arrays.stream(RiskLevelEnum.values()).map(value ->
                CommonOptionResp.builder()
                        .statusCode(value.getCode())
                        .statusName(value.getDesc())
                        .build()
        ).collect(Collectors.toList());
    }
}
