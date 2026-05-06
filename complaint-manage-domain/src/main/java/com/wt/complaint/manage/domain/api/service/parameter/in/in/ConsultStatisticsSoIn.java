package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.Data;


/**
 * 咨询单统计项查询入参
 */
@Data
public class ConsultStatisticsSoIn {

    /**
     * 门店id列表
     */
    private String orgId;

    private int onlyMe;

    private Long mid;
}
