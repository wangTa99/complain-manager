package com.wt.complaint.manage.domain.model;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 批量查询客诉单结�?
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Data
@Builder
public class BatchComplaintQueryResult {
    /**
     * 所有客诉单,包括售后客诉和交付零售客�?
     */
    private List<ComplaintOrderInfoGoIn> allOrderList;
    /**
     * 所有客诉单映射关系,包括售后客诉和交付零售客�?
     */
    private Map<String, ComplaintOrderInfoGoIn> allOrderMap;
    /**
     * 交付零售客诉单列�?
     */
    private List<DeliverComplaintBO> deliverRetailComplaintList;
    /**
     * 售后客诉单号列表
     */
    private List<String> oldComplaintNoList;
}
