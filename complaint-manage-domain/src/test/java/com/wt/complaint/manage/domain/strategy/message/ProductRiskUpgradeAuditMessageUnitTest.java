package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PushEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * ProductRiskUpgradeStoreAuditMessage 单元测试（产品风险升级门店推送）
 */
@ExtendWith(MockitoExtension.class)
public class ProductRiskUpgradeAuditMessageUnitTest {

    @InjectMocks
    private ProductRiskUpgradeAuditMessage productRiskUpgradeAuditMessage;

    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway storeRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway carRemoteGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productRiskUpgradeAuditMessage, "pcMainCarMaintenanceUrl", "http://test/");
    }

    @Test
    void createMessageInformedEvent_ProductComplaint_ReturnsEvent() {
        ComplaintOrderGoOut order = TestDataBuilder.buildComplaintOrderGoOut("C001", 1);
        order.setComplaintType(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        order.setId(1001L);
        order.setOperatorMid(1001L);
        Map<String, String> extraParam = new HashMap<>();

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder().orgName("测试门店").build();
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(storeInfo);
        EmployeeInfoGoOut employee = TestDataBuilder.buildEmployeeInfoGoOut(2001L, "店长");
        when(eiamRemoteGateway.queryEmployeeByStore(any())).thenReturn(Collections.singletonList(employee));

        MessageInformedEvent event = productRiskUpgradeAuditMessage.createMessageInformedEvent(order, extraParam);

        assertNotNull(event);
        assertEquals(PushEnum.PRODUCT_RISK_UPGRADE_STORE_AUDIT, event.getPushEnum());
        assertNotNull(event.getNrBoxPayload());
        assertTrue(event.getMidSet().contains(1001L));
    }
}
