package com.wt.complaint.manage.domain.aggregation;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;

import java.util.List;

public class ComplaintOrderAggregationFactory {
    public static ComplaintOrderBatchAggregation getComplaintOrderBatchAggregation(List<ComplaintOrderInfoGoIn> complaintOrderInfoGoIns) {
        return ComplaintOrderBatchAggregation.builder().complaintOrderInfoGoInList(complaintOrderInfoGoIns).build();
    }

    public static ComplaintOrderBatchAggregation getComplaintOrderBatchAggregation(List<ComplaintOrderInfoGoIn> complaintOrderInfoGoIns, List<ComplaintFollowProcessGoIn> complaintFollowProcessGoInList) {
        return ComplaintOrderBatchAggregation.builder()
            .complaintOrderInfoGoInList(complaintOrderInfoGoIns)
            .complaintFollowProcessGoInList(complaintFollowProcessGoInList)
            .build();
    }

    public static ComplaintOrderAggregation getComplaintOrderAggregation() {
        return ComplaintOrderAggregation.builder().build();
    }

    public static ComplaintOrderAggregation getComplaintOrderAggregation(ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        return ComplaintOrderAggregation.builder().complaintOrderInfoGoIn(complaintOrderInfoGoIn).build();
    }
}
