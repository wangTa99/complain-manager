package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.OrderInfoGateway;
import com.wt.complaint.manage.domain.exception.BusinessException;
import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.THIRD_SERVICE_ERROR;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.nr.order.api.dto.request.orderinfo.QueryOrderFilterDto;
import com.xiaomi.nr.order.api.dto.request.orderinfobackend.OrderListReq;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderListResp;
import com.xiaomi.nr.order.api.service.orderquery.OrderInfoBackendService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiankang
 * @date 2025/10/14
 */
@Slf4j
@Service
public class OrderInfoGatewayImpl implements OrderInfoGateway {

    /**
     * 订单侧最大分页数�?
     */
    private static final int MAX_ORDER_PAGE_SIZE = 20;

    /**
     * 汽车订单查询appKey 订单中心提供
     */
    private static final String ORDER_LIST_APP_KEY = "car-plan";

    @DubboReference(group = "${order.dubbo.group}", version = "1.0", interfaceClass = OrderInfoBackendService.class,
            timeout = DubboConstant.TIME_OUT)
    private OrderInfoBackendService orderInfoBackendService;


    @Override
    public List<OrderDetailResp> getOrderList(List<String> tradeOrderIdList) {
        log.info("OrderInfoGateway#getOrderList start, tradeOrderIdList:{}", GsonUtil.toJson(tradeOrderIdList));
        if (CollUtil.isEmpty(tradeOrderIdList)) {
            return Collections.emptyList();
        }
        List<List<String>> split = CollUtil.split(tradeOrderIdList, MAX_ORDER_PAGE_SIZE);
        List<OrderDetailResp> list = new ArrayList<>();
        try {
            for (List<String> splitOrderIdList : split) {
                OrderListReq req = new OrderListReq();
                req.setPageSize(MAX_ORDER_PAGE_SIZE);
                req.setPageIndex(1);
                req.setOrderIds(splitOrderIdList.stream().map(Long::parseLong).collect(Collectors.toList()));
                req.setAppKey(ORDER_LIST_APP_KEY);// 查询汽车订单需要传appKey
                QueryOrderFilterDto queryFilterDto = this.getQueryFilter();
                req.setQueryFilterDto(queryFilterDto);
                log.info("OrderInfoGateway#getOrderList req:{}", GsonUtil.toJson(req));
                Result<OrderListResp> result = orderInfoBackendService.list(req);
                log.info("OrderInfoGateway#getOrderList resp:{}", GsonUtil.toJson(result));
                if (result == null || result.getCode() != GeneralCodes.OK.getCode() || result.getData() == null) {
                    log.error("OrderInfoGateway#getOrderItemInfo fail, splitOrderIdList:{}, result:{}",
                            GsonUtil.toJson(splitOrderIdList), GsonUtil.toJson(result));
                    throw new BusinessException(THIRD_SERVICE_ERROR, "批量请求订单信息失败");
                }
                if (result.getData().getOrderDetailList() != null) {
                    list.addAll(result.getData().getOrderDetailList());
                }
            }
            log.info("OrderInfoGateway#getOrderItemInfo success, tradeOrderIdList:{}, list:{}", GsonUtil.toJson(tradeOrderIdList), GsonUtil.toJson(list));
            return list;
        } catch (Exception e) {
            log.error("OrderInfoGateway#getOrderItemInfo err tradeOrderIdList:{}", GsonUtil.toJson(tradeOrderIdList), e);
            return Collections.emptyList();
        }
    }

    private QueryOrderFilterDto getQueryFilter() {
        QueryOrderFilterDto queryOrderFilterDto = new QueryOrderFilterDto();
        queryOrderFilterDto.setOrderItem(Boolean.TRUE);
        queryOrderFilterDto.setOrderItemExtend(Boolean.TRUE);
        queryOrderFilterDto.setDeliverInfo(Boolean.FALSE);
        queryOrderFilterDto.setExtend(Boolean.FALSE);
        queryOrderFilterDto.setExtension(Boolean.FALSE);
        queryOrderFilterDto.setOrderLogInfo(Boolean.FALSE);
        queryOrderFilterDto.setConsigneeInfo(Boolean.FALSE);
        queryOrderFilterDto.setBuyCarInfo(Boolean.FALSE);
        queryOrderFilterDto.setNeedSpec(Boolean.FALSE);
        queryOrderFilterDto.setCarDeliverInfo(Boolean.FALSE);
        queryOrderFilterDto.setOrderHistory(Boolean.FALSE);
        queryOrderFilterDto.setCarInfo(Boolean.FALSE);
        queryOrderFilterDto.setPromotionShare(Boolean.FALSE);
        return queryOrderFilterDto;
    }

}
