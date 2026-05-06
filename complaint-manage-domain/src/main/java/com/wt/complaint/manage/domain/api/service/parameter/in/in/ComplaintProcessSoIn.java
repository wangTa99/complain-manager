package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintProcessSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉类单据号
     */
    private String ucNo;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 咨询单超级工单号
     */
    private String consultSuperTicketNo;

    /**
     * 来源：零售通PAD_DETAIL，售后工作台AFTER_SALE_WORKBENCH
     */
    private String source;
}
