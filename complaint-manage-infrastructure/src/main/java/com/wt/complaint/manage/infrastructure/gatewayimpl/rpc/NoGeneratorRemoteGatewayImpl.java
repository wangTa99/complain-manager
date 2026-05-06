package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NoGeneratorRemoteGateway;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.nr.order.id.generator.api.req.OrderIdReq;
import com.xiaomi.nr.order.id.generator.api.rsp.OrderIdRsp;
import com.xiaomi.nr.order.id.generator.api.service.OrderIdService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class NoGeneratorRemoteGatewayImpl implements NoGeneratorRemoteGateway {

    @DubboReference(interfaceClass = OrderIdService.class, group = "${dubbo.group.orderId}", version = "1.0", retries = 0, timeout = DubboConstant.TIME_OUT)
    private OrderIdService orderIdService;

    private static final String COMPLAINT_ORDER_PREFIX = "TS";
    private static final String CONSULT_ORDER_PREFIX = "ZX";

    @Override
    public String generateComplaintNo() {
        return COMPLAINT_ORDER_PREFIX + generateUcNo();
    }

    @Override
    public String generateUcNoWithPrefix(String prefix) {
        return prefix + generateUcNo();
    }

    @Override
    public String generateConsultNo() {
        return CONSULT_ORDER_PREFIX + generateUcNo();
    }

    public String generateUcNo() {
        OrderIdReq req = new OrderIdReq();
        req.setNum(1);
        try {
            Result<OrderIdRsp> orderIdResp = orderIdService.getOrderIds(req);
            log.info("orderIdService.getOrderIds resp:{}", GsonUtil.toJson(orderIdResp));
            if (orderIdResp.getCode() != GeneralCodes.OK.getCode() || Objects.isNull(orderIdResp.getData())) {
                log.error("NoGeneratorRemoteGatewayImpl.generateComplaintNo result is not ok, result:{}", GsonUtil.toJson(orderIdResp));
                throw new BusinessException(ErrorCodeEnums.GENERATE_ID_ERROR, "生成订单编号失败");
            }
            Long no = Optional.ofNullable(orderIdResp.getData().getOrderIds()).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
            if (Objects.isNull(no)) {
                log.error("NoGeneratorRemoteGatewayImpl.generateComplaintNo orderId is null");
                throw new BusinessException(ErrorCodeEnums.GENERATE_ID_ERROR, "生成订单编号为空");
            }
            return no.toString();
        } catch (Exception e) {
            log.error("NoGeneratorRemoteGatewayImpl.generateComplaintNo error", e.toString());
            throw new BusinessException(ErrorCodeEnums.GENERATE_ID_ERROR, "生成客诉单号异常");
        }
    }
}
