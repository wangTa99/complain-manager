package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

@Data
public class OrderUpdateCustomerServiceSoOut {
    private String result;

    /**
     * 更新数量
     */
    private Boolean updateResult;
}
