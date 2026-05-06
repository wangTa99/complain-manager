package com.wt.complaint.manage.backups.gateway;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.OrderInfoGateway;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.nr.order.api.dto.response.orderinfobackend.OrderDetailResp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;

/**
 * @author zhangzheyang
 * @date 2025/1/8
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class OrderInfoGatewayTest {

    @Resource
    private OrderInfoGateway orderInfoGateway;

    @Test
    public void getOrderItemInfoTest() {
        List<OrderDetailResp> result = orderInfoGateway.getOrderList(Collections.singletonList("5255201002040377"));
        log.info("getEmployeeList:{}", RetailJsonUtil.toJson(result));
    }

}
