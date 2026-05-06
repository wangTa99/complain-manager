package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单统计项查询出参
 */
@Data
public class ConsultStatisticsSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待接单数�?*/
    private Integer pendingReceiveCount;
    /** 待首响数�?*/
    private Integer pendingFirstResponseCount;
    /** 待结案数�?*/
    private Integer pendingCloseCount;
    /** 已完成数�?*/
    private Integer completedCount;

}
