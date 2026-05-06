package com.wt.complaint.manage.backups.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListGoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 *  销交客�?Gateway 测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComplaintManageBootstrap.class)
public class RetailDeliverComplaintGatewayTest {

    @Resource
    DeliverComplaintGateway deliverComplaintGateway;

    @Resource
    RetailComplaintGateway retailComplaintGateway;

    @Test
    public void selectListByConditionTest() {
        List<DeliverComplaintBO> deliverComplaintBOS = deliverComplaintGateway.selectListByCondition(DeliverComplaintListGoIn
                .builder()
                .useMaster(true)
                .build());
        assertNotNull(deliverComplaintBOS);
        System.out.println(deliverComplaintBOS);
    }

    @Test
    public void coolRequestDeliverFirstResponseToTimeoutListTest() {
        List<DeliverComplaintListGoOut> res = deliverComplaintGateway.selectFirstResponseToTimeoutList();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        log.info("res:{}", gson.toJson(res));
    }

    @Test
    public void coolRequestDeliverFinishToTimeoutListTest() {
        List<DeliverComplaintListGoOut> res = deliverComplaintGateway.selectFinishToTimeoutList();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        log.info("res:{}", gson.toJson(res));
    }

    @Test
    public void coolRequestRetailFirstResponseToTimeoutListTest() {
        List<RetailComplaintListGoOut> res = retailComplaintGateway.selectFirstResponseToTimeoutList();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        log.info("res:{}", gson.toJson(res));
    }

    @Test
    public void coolRequestRetailFinishToTimeoutListTest() {
        List<RetailComplaintListGoOut> res = retailComplaintGateway.selectFinishToTimeoutList();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        log.info("res:{}", gson.toJson(res));
    }

}
