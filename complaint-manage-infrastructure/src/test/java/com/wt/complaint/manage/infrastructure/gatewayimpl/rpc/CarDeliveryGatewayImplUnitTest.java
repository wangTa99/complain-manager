package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.xiaomi.car.delivery.core.api.dto.req.GetDeliveryByOrdersReq;
import com.xiaomi.car.delivery.core.api.dto.res.GetDeliveryByOrdersRes;
import com.xiaomi.car.delivery.core.api.provider.DeliveryProvider;
import com.xiaomi.car.delivery.core.work.dto.req.DeliveryStaffReq;
import com.xiaomi.car.delivery.core.work.dto.res.DeliveryStaffRes;
import com.xiaomi.car.delivery.core.work.provider.DeliveryStaffProvider;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.DeliveryStaffGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DeliveryStaffGoOut;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarDeliveryGatewayImplUnitTest {

    @InjectMocks
    private CarDeliveryGatewayImpl carDeliveryGateway;

    @Mock
    private DeliveryStaffProvider deliveryStaffProvider;

    @Mock
    private DeliveryProvider deliveryProvider;

    @Test
    void testListDeliveryStaff_success() {
        DeliveryStaffGoIn goIn = DeliveryStaffGoIn.builder().build();
        // mock 返回两条数据
        Result<List<DeliveryStaffRes>> res = Result.success(null);
        List<DeliveryStaffRes> data = new ArrayList<>();
        data.add(new DeliveryStaffRes());
        data.add(new DeliveryStaffRes());
        res.setData(data);
        when(deliveryStaffProvider.listDeliveryStaff(any(DeliveryStaffReq.class))).thenReturn(res);

        List<DeliveryStaffGoOut> result = carDeliveryGateway.listDeliveryStaff(goIn);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testListDeliveryStaff_exceptionReturnEmpty() {
        DeliveryStaffGoIn goIn = DeliveryStaffGoIn.builder().build();
        when(deliveryStaffProvider.listDeliveryStaff(any(DeliveryStaffReq.class))).thenThrow(new RuntimeException("err"));
        List<DeliveryStaffGoOut> result = carDeliveryGateway.listDeliveryStaff(goIn);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetDeliveryByOrderIds_success() {
        List<String> orderIds = Arrays.asList("1", "2");
        Result<List<GetDeliveryByOrdersRes>> res = Result.success(null);
        List<GetDeliveryByOrdersRes> list = new ArrayList<>();
        list.add(new GetDeliveryByOrdersRes());
        res.setData(list);
        when(deliveryProvider.getDeliveryByOrderIds(any(GetDeliveryByOrdersReq.class))).thenReturn(res);

        List<GetDeliveryByOrdersRes> result = carDeliveryGateway.getDeliveryByOrderIds(orderIds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetDeliveryByOrderIds_exceptionReturnEmpty() {
        List<String> orderIds = Arrays.asList("1", "2");
        when(deliveryProvider.getDeliveryByOrderIds(any(GetDeliveryByOrdersReq.class))).thenThrow(new RuntimeException("err"));
        List<GetDeliveryByOrdersRes> result = carDeliveryGateway.getDeliveryByOrderIds(orderIds);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}


