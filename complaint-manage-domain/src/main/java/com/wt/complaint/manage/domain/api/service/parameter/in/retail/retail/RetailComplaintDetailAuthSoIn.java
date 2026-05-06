package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetailComplaintDetailAuthSoIn {

    /**
     * 客诉单号
     */
    private String drNo;


    /**
     * 用户mid
     */
    private String mid;
}
