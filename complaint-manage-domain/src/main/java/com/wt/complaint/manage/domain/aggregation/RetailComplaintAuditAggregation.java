package com.wt.complaint.manage.domain.aggregation;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


@Data
@Builder
@Slf4j
public class RetailComplaintAuditAggregation {
    /**
     * BPM创建申请
     */
    private RetailComplaintCreateBPMGoIn retailComplaintCreateBPMGoIn;

    /**
     * 跟进记录
     */
    private ComplaintFollowProcessGoIn complaintFollowProcessGoIn;

    private List<StoreInfoGoOut> carStoreList;

    private Map<Long, EmployeeInfoGoOut> employeeMap;


}
