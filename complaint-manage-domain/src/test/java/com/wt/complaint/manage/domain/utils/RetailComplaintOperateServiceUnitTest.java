package com.wt.complaint.manage.domain.utils;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;
import com.wt.complaint.manage.domain.serviceimpl.RetailComplaintOperateServiceImpl;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventFactory;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import com.wt.complaint.manage.domain.stateflow.retail.PendingToChangeOrgStatusEventHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RetailComplaintOperateServiceUnitTest {

    @InjectMocks
    private RetailComplaintOperateServiceImpl retailComplaintOperateService;

    @Mock
    private RetailComplaintGateway retailComplaintGateway;

    @Mock
    BPMRemoteGateway bpmRemoteGateway;

    @Mock
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Mock
    private UserComplaintStatusEventFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void submitChangeOrgApplyTest() throws IllegalAccessException, NoSuchFieldException {
        // 构建请求参数
        RetailComplaintApplySoIn soIn =
                RetailComplaintApplySoIn.builder().drNo("RC256701001026680").applyOrgId("F1031").desOrgId("X5999")
                        .reassignRemark("申请改派测试").orderStatus(10).storeMap(Collections.emptyMap()).createMid(123L).build();

        // 创建 handler 实例
        UserComplaintStatusEventHandler<RetailComplaintApplySoIn, RetailComplaintApplySoOut> handler =
                new PendingToChangeOrgStatusEventHandler();

        // 反射注入 retailComplaintGateway
        Field retailGatewayField = PendingToChangeOrgStatusEventHandler.class.getDeclaredField("retailComplaintGateway");
        retailGatewayField.setAccessible(true);
        retailGatewayField.set(handler, retailComplaintGateway); // 注入测试类中 @Mock �?retailComplaintGateway

        // 反射注入 bpmRemoteGateway
        Field bpmGatewayField = PendingToChangeOrgStatusEventHandler.class.getDeclaredField("bpmRemoteGateway");
        bpmGatewayField.setAccessible(true);
        bpmGatewayField.set(handler, bpmRemoteGateway); // 注入测试类中 @Mock �?bpmRemoteGateway

        // 反射注入 followProcessGateway
        Field followGatewayField = PendingToChangeOrgStatusEventHandler.class.getDeclaredField("followProcessGateway");
        followGatewayField.setAccessible(true);
        followGatewayField.set(handler, followProcessGateway); // 注入测试类中 @Mock �?followProcessGateway

        // 工厂返回注入后的 handler
        when(factory.getStatusEventHandler(anyString(), anyInt(), anyInt())).thenReturn(handler);
        RetailComplaintApplySoOut result = retailComplaintOperateService.submitChangeOrgApply(soIn);
        Assertions.assertNotNull(result);
    }
}
