package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.xiaomi.car.delivery.core.api.dto.req.GetDeliveryByOrdersReq;
import com.xiaomi.car.delivery.core.api.dto.res.GetDeliveryByOrdersRes;
import com.xiaomi.car.delivery.core.api.provider.DeliveryProvider;
import com.xiaomi.car.delivery.core.work.dto.req.DeliveryStaffReq;
import com.xiaomi.car.delivery.core.work.dto.res.DeliveryStaffRes;
import com.xiaomi.car.delivery.core.work.provider.DeliveryStaffProvider;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarDeliveryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.DeliveryStaffGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DeliveryStaffGoOut;
import com.wt.maindataapi.api.StoreProvider;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarDeliveryGatewayImpl implements CarDeliveryGateway {

    @DubboReference(check = false, interfaceClass = DeliveryStaffProvider.class, group = "${dubbo.group.delivery}",
            version = "1.0", retries = 0,
            timeout = 5000)
    private DeliveryStaffProvider deliveryStaffProvider;

    @DubboReference(interfaceClass = DeliveryProvider.class,
            group = "${dubbo.group.delivery}",
            version = "1.0",
            timeout = 3000)
    private DeliveryProvider deliveryProvider;


    @Override
    public List<DeliveryStaffGoOut> listDeliveryStaff(DeliveryStaffGoIn goIn) {
        try {
            DeliveryStaffReq req = Convert.convert(DeliveryStaffReq.class, goIn);
            log.info("CarDeliveryGatewayImpl.listDeliveryStaff, req:{}", GsonUtil.toJson(req));
            Result<List<DeliveryStaffRes>> listResult = deliveryStaffProvider.listDeliveryStaff(req);
            log.info("CarDeliveryGatewayImpl.listDeliveryStaff, resp:{}", GsonUtil.toJson(req));
            return Convert.toList(DeliveryStaffGoOut.class, listResult.getData());
        } catch (Exception e) {
            log.error("listDeliveryStaff, req:{}", GsonUtil.toJson(goIn), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<GetDeliveryByOrdersRes> getDeliveryByOrderIds(List<String> tradeOrderIds) {
        log.info("CarDeliveryGatewayImpl.listDeliveryStaff, req:{}", GsonUtil.toJson(tradeOrderIds));
        if (CollUtil.isEmpty(tradeOrderIds)) {
            return Collections.emptyList();
        }
        List<List<String>> split = CollUtil.split(tradeOrderIds, 100);
        List<GetDeliveryByOrdersRes> list = new ArrayList<>();
        for (List<String> splitTradeOrderIds : split) {
            try {
                GetDeliveryByOrdersReq req = new GetDeliveryByOrdersReq();
                req.setOrderIds(splitTradeOrderIds.stream().map(Long::valueOf).collect(Collectors.toList()));
                log.info("CarDeliveryGatewayImpl.splitListDeliveryStaff, req:{}", GsonUtil.toJson(req));
                Result<List<GetDeliveryByOrdersRes>> result = deliveryProvider.getDeliveryByOrderIds(req);
                log.info("CarDeliveryGatewayImpl.splitListDeliveryStaff, resp:{}", GsonUtil.toJson(result));
                if (result != null && result.getCode() == GeneralCodes.OK.getCode() && result.getData() != null) {
                    list.addAll(result.getData());
                }
            } catch (Exception e) {
                log.warn("listDeliveryStaff, req:{}", GsonUtil.toJson(tradeOrderIds), e);
            }
        }
        log.info("CarDeliveryGatewayImpl.listDeliveryStaff, resp:{}", GsonUtil.toJson(list));
        return list;
    }
}
