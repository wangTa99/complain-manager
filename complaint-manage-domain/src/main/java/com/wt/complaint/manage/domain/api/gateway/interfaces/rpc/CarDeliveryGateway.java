package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.xiaomi.car.delivery.core.api.dto.res.GetDeliveryByOrdersRes;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.DeliveryStaffGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DeliveryStaffGoOut;

import java.util.List;

/**
 * 车辆交付专员信息查询接口
 */
public interface CarDeliveryGateway {

    /**
     * 根据订单信息查询交付专员信息
     * @param goIn 订单信息
     * @return <List>交付专员信息
     */
    List<DeliveryStaffGoOut> listDeliveryStaff(DeliveryStaffGoIn goIn);

    /**
     * 查询交付信息
     * @param tradeOrderIds 订单�?
     * @return 订单�?
     */
    List<GetDeliveryByOrdersRes> getDeliveryByOrderIds(List<String> tradeOrderIds);
}
