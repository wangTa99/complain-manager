package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.xiaomi.nr.order.api.dto.request.orderinfobackend.OrderListReq;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderListResp;
import com.xiaomi.nr.order.api.service.orderquery.OrderInfoBackendService;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderInfoGatewayImplUnitTest {

    @InjectMocks
    private OrderInfoGatewayImpl orderInfoGateway;

    @Mock
    private OrderInfoBackendService orderInfoBackendService;

    @Test
    void testGetOrderList_emptyInput() {
        List<OrderDetailResp> result = orderInfoGateway.getOrderList(new ArrayList<>());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrderList_success_splitBatches() {
        // 构�?25 个订单ID，按 20/5 分批
        List<String> orderIds = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            orderIds.add(String.valueOf(i));
        }

        // 第一批返�?20 �?
        Result<OrderListResp> r1 = Result.success(null);
        OrderListResp resp1 = new OrderListResp();
        List<OrderDetailResp> list1 = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            OrderDetailResp d = new OrderDetailResp();
            d.setOrderId((long) i);
            list1.add(d);
        }
        resp1.setOrderDetailList(list1);
        r1.setData(resp1);

        // 第二批返�?5 �?
        Result<OrderListResp> r2 = Result.success(null);
        OrderListResp resp2 = new OrderListResp();
        List<OrderDetailResp> list2 = new ArrayList<>();
        for (int i = 21; i <= 25; i++) {
            OrderDetailResp d = new OrderDetailResp();
            d.setOrderId((long) i);
            list2.add(d);
        }
        resp2.setOrderDetailList(list2);
        r2.setData(resp2);

        when(orderInfoBackendService.list(any(OrderListReq.class)))
                .thenReturn(r1)
                .thenReturn(r2);

        List<OrderDetailResp> result = orderInfoGateway.getOrderList(orderIds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(25, result.size());
        verify(orderInfoBackendService, times(2)).list(any(OrderListReq.class));
    }

    @Test
    void testGetOrderList_serviceFail_returnEmpty() {
        List<String> orderIds = Arrays.asList("1", "2");

        Result<OrderListResp> bad = Result.success(null);
        bad.setData(null);
        when(orderInfoBackendService.list(any(OrderListReq.class))).thenReturn(bad);

        List<OrderDetailResp> result = orderInfoGateway.getOrderList(orderIds);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}


