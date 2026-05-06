package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderItemInfo;

import java.util.List;

/**
 * 订单信息查询
 */
public interface OrderInfoGateway {

    /**
     * 根据订单号查询订单信�?
     * @param tradeOrderId 订单�?
     * @return 订单信息
     */
    List<OrderDetailResp> getOrderList(List<String> tradeOrderId);

}
