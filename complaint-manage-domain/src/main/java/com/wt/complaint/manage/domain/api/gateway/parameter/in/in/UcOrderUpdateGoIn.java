package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author linjiehong
 * @date 2025/5/21 15:41
 */
@Data
@Builder
public class UcOrderUpdateGoIn {
    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 状态�?
     */
    private Integer orderStatus;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 处理人mid
     */
    private Long operatorMid;
}
