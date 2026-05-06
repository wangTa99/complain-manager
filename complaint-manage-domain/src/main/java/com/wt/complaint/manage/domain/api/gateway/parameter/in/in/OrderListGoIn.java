package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListGoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 工单号List
     */
    private List<String> stNoList;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 客诉单号列表
     */
    private List<String> complaintNoList;

    /**
     * 客诉单状态列�?
     */
    private List<Integer> complaintStatusList;

    /**
     * 幂等id
     */
    private String idempotentId;
}
