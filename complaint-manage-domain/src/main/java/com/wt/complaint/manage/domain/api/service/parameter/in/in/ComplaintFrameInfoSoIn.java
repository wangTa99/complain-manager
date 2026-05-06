package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.proretail.newcommon.param.BaseParamModelSoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintFrameInfoSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 来源, PAD_DETAIL:零售通pad-投诉单详�? AFTER_SALE_WORKBENCH:售后工作�?
     */
    private String source;

    private String orgId;
}
