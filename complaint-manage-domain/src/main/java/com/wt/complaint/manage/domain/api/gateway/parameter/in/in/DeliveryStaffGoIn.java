package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

/**
 * 交付人员信息查询请求参数
 */
@Data
@Builder
public class DeliveryStaffGoIn {
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 是否主库
     */
    private Boolean forceMaster;
}
