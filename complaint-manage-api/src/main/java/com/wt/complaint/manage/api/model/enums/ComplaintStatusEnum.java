package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 服务投诉单状态枚举类
 */
@Getter
@AllArgsConstructor
public enum ComplaintStatusEnum {

    PENDING_ORDER(1, "待接�?, "待接�?, "已接�?),
    ORG_REASSIGN_PENDING(15, "申请改派门店待审�?, "", ""),
    FIRST_RESPONSE_PENDING(30, "待首�?, "待首�?, "已首�?),
    APPLY_FINISH_PENDING(50, "待申请结�?, "待申请结�?, "已申请结�?),
    FINISH_EVALUATION_PENDING(70, "待结案评�?, "待结案评�?, "已结案评�?),
    FINISH_COMPLETE(90, "结案完成", "结案待完�?, "结案完成");

    private final Integer code;
    private final String desc;
    private final String barFutureDesc;
    private final String barBeenDesc;

    /**
     * 是否可首�?
     * @param code
     * @return
     */
    public static Boolean canFirstResponse(Integer code) {
        return code.equals(FIRST_RESPONSE_PENDING.getCode());
    }

    public static Boolean canAddFollowUpRecords(Integer code) {
        if (code.equals(APPLY_FINISH_PENDING.getCode()) || code.equals(FINISH_EVALUATION_PENDING.getCode())) {
            return true;
        }
        return false;
    }

    public static String getDescByCode(Integer code) {
        for (ComplaintStatusEnum complaintStatusEnum : ComplaintStatusEnum.values()) {
            if (complaintStatusEnum.getCode().equals(code)) {
                return complaintStatusEnum.getDesc();
            }
        }
        return null;
    }

    public static List<Integer> getUnfinishedStatus() {
        return Arrays.asList(FIRST_RESPONSE_PENDING.getCode(), APPLY_FINISH_PENDING.getCode(), FINISH_EVALUATION_PENDING.getCode());
    }

    /**
     * 返回需要计算超时的状�?
     * @return
     */
    public static List<Integer> getTagNeedStatus() {
        return Arrays.asList(PENDING_ORDER.getCode(), ORG_REASSIGN_PENDING.getCode(), FIRST_RESPONSE_PENDING.getCode(), APPLY_FINISH_PENDING.getCode(), FINISH_EVALUATION_PENDING.getCode());
    }

    /**
     * 返回待首响前的状�?
     * @return
     */
    public static List<Integer> getBeforeFirstRespStatus() {
        return Arrays.asList(PENDING_ORDER.getCode(), ORG_REASSIGN_PENDING.getCode(), FIRST_RESPONSE_PENDING.getCode());
    }

    /**
     * 返回首响后结案前的状�?
     * @return
     */
    public static List<Integer> getAfterFirstRespStatus() {
        return Arrays.asList(APPLY_FINISH_PENDING.getCode(), FINISH_EVALUATION_PENDING.getCode());
    }

    public static List<Integer> getWaitJudgeTimeOutFinishStatus() {
        return Arrays.asList(PENDING_ORDER.getCode(), FIRST_RESPONSE_PENDING.getCode(), APPLY_FINISH_PENDING.getCode());
    }
}
