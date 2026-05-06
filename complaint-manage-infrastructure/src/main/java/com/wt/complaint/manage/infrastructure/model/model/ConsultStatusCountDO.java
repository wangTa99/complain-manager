package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

/**
 * 咨询单状态统计结�?
 */
@Data
public class ConsultStatusCountDO {

    /**
     * 咨询单状�?
     */
    private Integer orderStatus;

    /**
     * 该状态的数量
     */
    private Integer cnt;

    /**
     * 门店ID
     */
    private String  orgId;
}
