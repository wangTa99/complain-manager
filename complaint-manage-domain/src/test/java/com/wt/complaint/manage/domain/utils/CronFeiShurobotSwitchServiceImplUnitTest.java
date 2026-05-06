package com.wt.complaint.manage.domain.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.DeliverComplaintExpandGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintExpandService;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintExpandListGoIn;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.serviceimpl.CronFeiShuRobotSwitchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CronFeiShurobotSwitchServiceImplUnitTest {

    @InjectMocks
    private CronFeiShuRobotSwitchServiceImpl cronFeiShuRobotSwitchServiceImpl;

    @Mock
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    @Mock
    private DeliverComplaintExpandService deliverComplaintExpandService;

    @BeforeEach
    void setUp() {
        // 通过反射设置appId属�?
        try {
            Field appIdField = CronFeiShuRobotSwitchServiceImpl.class.getDeclaredField("appId");
            appIdField.setAccessible(true);
            appIdField.set(cronFeiShuRobotSwitchServiceImpl, "test_app_id");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRobotSwitchTask_empty_data() {
        when(deliverComplaintExpandGateway.selectCount()).thenReturn(0);
        cronFeiShuRobotSwitchServiceImpl.robotSwitch("test");
        verify(deliverComplaintExpandGateway, times(1)).selectCount();
    }

    @Test
    void testRobotSwitchTask_success() {
        // 1. 准备测试数据
        List<DeliverComplaintExpandGoOut> deliverComplaintExpandGoOutList = new ArrayList<>();
        DeliverComplaintExpandGoOut deliverComplaintExpandGoOut = new DeliverComplaintExpandGoOut();
        deliverComplaintExpandGoOut.setChatId("chat123");
        deliverComplaintExpandGoOut.setChatName("测试�?);
        deliverComplaintExpandGoOutList.add(deliverComplaintExpandGoOut);
        String mockResponse = "{\"code\":0,\"data\":[{\"source\":\"chat123\",\"target\":\"open_456\"}]}";
        HttpResponse mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.body()).thenReturn(mockResponse);
        // 2、模拟静态HTTP调用�?
        mockStatic(HttpRequest.class);
        HttpRequest mockRequest = mock(HttpRequest.class);
        when(HttpRequest.post(CommonConst.CHAT_ID_MAPPING_URL)).thenReturn(mockRequest);
        when(mockRequest.body(anyString())).thenReturn(mockRequest);
        when(mockRequest.header(eq("Content-Type"), eq("application/json"))).thenReturn(mockRequest);
        when(mockRequest.header(eq("APP_ID"), anyString())).thenReturn(mockRequest);
        when(mockRequest.timeout(anyInt())).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(mockHttpResponse);
        // 3、mock模拟
        when(deliverComplaintExpandGateway.selectCount()).thenReturn(1);
        when(deliverComplaintExpandGateway.selectList(any(DeliverComplaintExpandListGoIn.class))).thenReturn(deliverComplaintExpandGoOutList);
        when(deliverComplaintExpandService.updateBatchSelective(any())).thenReturn(1);
        // 4、执行测�?
        cronFeiShuRobotSwitchServiceImpl.robotSwitch("test");
        // 5、验证结�?
        assertEquals(1, deliverComplaintExpandGoOutList.size());
    }
}
