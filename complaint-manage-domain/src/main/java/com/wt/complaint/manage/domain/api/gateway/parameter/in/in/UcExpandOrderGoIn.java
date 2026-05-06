package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/23 16:53
 */
@Data
@Builder
public class UcExpandOrderGoIn {
    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 举报判定结果
     */
    private Integer judgeType;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;
}
