package com.wt.complaint.manage.domain.aggregation;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceInfo;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceSoIn;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Data
@Builder
public class ComplaintOrderBatchAggregation {
    private List<ComplaintOrderInfoGoIn> complaintOrderInfoGoInList;

    private List<ComplaintFollowProcessGoIn> complaintFollowProcessGoInList;


    public void updateCustomerServiceInfo(OrderUpdateCustomerServiceSoIn soIn) {
        List<OrderUpdateCustomerServiceInfo> orderUpdateCustomerServiceInfos = soIn.getOrderUpdateCustomerServiceInfos();
        Map<String, Long> newCstMidMap = orderUpdateCustomerServiceInfos.stream().collect(Collectors.toMap(e -> e.getStNo(), e -> e.getCustomerServiceMid(), (v1, v2) -> v1));
        List<ComplaintOrderInfoGoIn> updateList = new ArrayList<>();
        List<ComplaintOrderInfoGoIn> collect = complaintOrderInfoGoInList.stream().filter(e -> !Objects.equals(e.getStatus(), ComplaintStatusEnum.FINISH_COMPLETE.getCode())).collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            this.complaintOrderInfoGoInList = updateList;
            return;
        }
        collect.forEach(e -> {
            if (newCstMidMap.containsKey(e.getSuperTicketNo())) {
                Long l = newCstMidMap.get(e.getSuperTicketNo());
                ComplaintOrderInfoGoIn build = ComplaintOrderInfoGoIn.builder()
                    .complaintNo(e.getComplaintNo())
                    .customerServiceMid(l)
                    .build();
                updateList.add(build);
            }
        });
        this.complaintOrderInfoGoInList = updateList.stream().filter(e-> Objects.nonNull(e.getCustomerServiceMid())).collect(Collectors.toList());
    }
}
