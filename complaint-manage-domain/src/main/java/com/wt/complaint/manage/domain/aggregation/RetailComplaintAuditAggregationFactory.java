package com.wt.complaint.manage.domain.aggregation;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;

import java.util.List;
import java.util.Map;

public class RetailComplaintAuditAggregationFactory {
    private RetailComplaintAuditAggregationFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static RetailComplaintAuditAggregation getRetailComplaintAuditAggregation() {
        return RetailComplaintAuditAggregation.builder().build();
    }

    public static RetailComplaintAuditAggregation getRetailComplaintAuditAggregation(List<StoreInfoGoOut> carStore, Map<Long, EmployeeInfoGoOut> employeeMap) {
        return RetailComplaintAuditAggregation.builder().carStoreList(carStore).employeeMap(employeeMap).build();
    }
}
