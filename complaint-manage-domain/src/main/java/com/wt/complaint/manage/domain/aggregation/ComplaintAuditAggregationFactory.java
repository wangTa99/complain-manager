package com.wt.complaint.manage.domain.aggregation;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;

import java.util.List;
import java.util.Map;

public class ComplaintAuditAggregationFactory {
    public static ComplaintAuditAggregation getComplaintAuditAggregation() {
        return ComplaintAuditAggregation.builder().build();
    }

    public static ComplaintAuditAggregation getComplaintAuditAggregation(ComplaintOrderInfoGoIn orderInfo, List<StoreInfoGoOut> carStore, Map<Long, EmployeeInfoGoOut> employeeMap) {
        return ComplaintAuditAggregation.builder().orderInfo(orderInfo).carStoreList(carStore).employeeMap(employeeMap).build();
    }

    public static ComplaintAuditAggregation getComplaintAuditAggregation(List<StoreInfoGoOut> carStore, Map<Long, EmployeeInfoGoOut> employeeMap) {
        return ComplaintAuditAggregation.builder().carStoreList(carStore).employeeMap(employeeMap).build();
    }
}
