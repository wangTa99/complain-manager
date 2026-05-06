package com.wt.complaint.manage.backups.gateway;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.infrastructure.gatewayimpl.rpc.EiamRemoteGatewayImpl;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangzheyang
 * @date 2025/1/8
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap.class)
public class EiamRemoteGatewayTest {

    @Resource
    private EiamRemoteGatewayImpl eiamRemoteGateway;

    @Test
    public void getEmployeeList() {
        EmployeeListGoIn goIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(3150270561L,3150433150L)).build();
        List<EmployeeInfoGoOut> result = eiamRemoteGateway.getEmployeeList(goIn);
        log.info("getEmployeeList:{}", RetailJsonUtil.toJson(result));
    }

}
