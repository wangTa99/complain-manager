package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

@Data
public class OrderListParam {
    /**
     * 门店id
     */
    private String orgId;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉单号列表
     */
    private List<String> complaintNoList;

    /**
     * 工单号List
     */
    private List<String> stNoList;

    /**
     * 客诉单状态列�?
     */
    private List<Integer> complaintStatusList;
    /**
     * 幂等id
     */
    private String idempotentId;
}
