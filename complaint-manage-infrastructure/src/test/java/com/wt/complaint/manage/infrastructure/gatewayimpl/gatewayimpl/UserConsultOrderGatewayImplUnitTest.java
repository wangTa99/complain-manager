package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ConsultListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserConsultOrderMainGoOut;
import com.wt.complaint.manage.domain.model.ConsultStatusCountInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.infrastructure.mapper.UserConsultOrderMapper;
import com.wt.complaint.manage.infrastructure.model.UserConsultOrderDO;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserConsultOrderGatewayImplUnitTest {

    // Mock Mapper
    @Mock
    private UserConsultOrderMapper userConsultOrderMapper;

    // 测试目标
    @InjectMocks
    private UserConsultOrderGatewayImpl userConsultOrderGateway;

    // 测试常量
    private static final String TEST_CONSULT_NO = "C202603300001";
    private static final String TEST_ORG_ID = "1001";
    private static final Long TEST_MID = 123456L;
    private static final Long DEFAULT_TIME = 18374400000L;

    // ====================== 1. createUserConsultOrder ======================
    @Test
    void createUserConsultOrderNormalInsert() {
        // given
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        goIn.setConsultNo(TEST_CONSULT_NO);
        when(userConsultOrderMapper.insertSelective(any(UserConsultOrderDO.class))).thenReturn(1);

        // when
        int rows = userConsultOrderGateway.createUserConsultOrder(goIn);

        // then
        assertEquals(1, rows);
        verify(userConsultOrderMapper).insertSelective(any());
    }

    // ====================== 2. updateOrderSelective ======================
    @Test
    void updateOrderSelectiveNormal() {
        // given
        UcConsultOrderUpdateGoIn goIn =   UcConsultOrderUpdateGoIn.builder().build();
        goIn.setConsultNo(TEST_CONSULT_NO);
        when(userConsultOrderMapper.updateByParam(any(), any())).thenReturn(1);

        // when
        int rows = userConsultOrderGateway.updateOrderSelective(goIn);

        // then
        assertEquals(1, rows);
    }

    @Test
    void updateOrderSelectiveReturnZero() {
        assertEquals(0, userConsultOrderGateway.updateOrderSelective(null));

        UcConsultOrderUpdateGoIn goIn =  UcConsultOrderUpdateGoIn.builder().build();
        goIn.setConsultNo(null);
        assertEquals(0, userConsultOrderGateway.updateOrderSelective(goIn));
    }

    // ====================== 3. searchUserConsultMainData ======================
    @Test
    void searchUserConsultMainDataNormal() {
        // given
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        UserConsultOrderDO orderDO = new UserConsultOrderDO();
        orderDO.setConsultNo(TEST_CONSULT_NO);
        when(userConsultOrderMapper.selectByParam(any())).thenReturn(Collections.singletonList(orderDO));

        // when
        UserConsultOrderMainGoOut result = userConsultOrderGateway.searchUserConsultMainData(goIn);

        // then
        assertNotNull(result);
        assertFalse(result.getUserConsultOrderInfoList().isEmpty());
    }

    @Test
    void searchUserConsultMainDataEmpty() {
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        when(userConsultOrderMapper.selectByParam(any())).thenReturn(Collections.emptyList());

        UserConsultOrderMainGoOut result = userConsultOrderGateway.searchUserConsultMainData(goIn);
        assertTrue(result.getUserConsultOrderInfoList().isEmpty());
    }

    // ====================== 4. searchUserConsultOrderInfo ======================
    @Test
    void searchUserConsultOrderInfoSingle() {
        // given
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        UserConsultOrderDO orderDO = new UserConsultOrderDO();
        orderDO.setConsultNo(TEST_CONSULT_NO);
        when(userConsultOrderMapper.selectByParam(any())).thenReturn(Collections.singletonList(orderDO));

        // when
        UserConsultOrderInfo result = userConsultOrderGateway.searchUserConsultOrderInfo(goIn);

        // then
        assertNotNull(result);
        assertEquals(TEST_CONSULT_NO, result.getConsultNo());
    }

    @Test
    void searchUserConsultOrderInfoNull() {
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        when(userConsultOrderMapper.selectByParam(any())).thenReturn(Collections.emptyList());

        assertNull(userConsultOrderGateway.searchUserConsultOrderInfo(goIn));
    }

    // ====================== 5. countConsultStatistics ======================
    @Test
    void countConsultStatisticsNormal() {
        // given
        ConsultStatusCountInfo info = new ConsultStatusCountInfo();
        info.setCnt(10);
        when(userConsultOrderMapper.countStatisticsByOrgId(any(), any())).thenReturn(Collections.singletonList(info));

        // when
        var list = userConsultOrderGateway.countConsultStatistics(TEST_ORG_ID, TEST_MID);

        // then
        assertEquals(1, list.size());
    }

    @Test
    void countConsultStatistics_返回空列�?) {
        when(userConsultOrderMapper.countStatisticsByOrgId(any(), any())).thenReturn(null);
        var list = userConsultOrderGateway.countConsultStatistics(TEST_ORG_ID, TEST_MID);
        assertTrue(list.isEmpty());
    }

    // ====================== 6. countPadConsultPage ======================
    @Test
    void countPadConsultPageNormal() {
        ConsultListGoIn goIn =   ConsultListGoIn.builder().build();
        when(userConsultOrderMapper.countPadPageByParam(any())).thenReturn(100L);

        long total = userConsultOrderGateway.countPadConsultPage(goIn);
        assertEquals(100L, total);
    }

    // ====================== 7. pagePadConsultOrders ======================
    @Test
    void pagePadConsultOrdersNormal() {
        // given
        ConsultListGoIn goIn =  ConsultListGoIn.builder().build();
        UserConsultOrderDO orderDO = new UserConsultOrderDO();
        orderDO.setConsultNo(TEST_CONSULT_NO);
        orderDO.setCreateTime(new Date(DEFAULT_TIME)); // 测试默认时间置空
        orderDO.setExpectingBackTime(new Date(DEFAULT_TIME));
        when(userConsultOrderMapper.padPageByParam(any())).thenReturn(Collections.singletonList(orderDO));

        // when
        var list = userConsultOrderGateway.pagePadConsultOrders(goIn);

        // then
        assertFalse(list.isEmpty());
        assertNull(list.get(0).getCreateTime()); // 验证默认时间被置�?
        assertNull(list.get(0).getExpectingBackTime());
    }

    @Test
    void pagePadConsultOrdersNull() {
        ConsultListGoIn goIn = ConsultListGoIn.builder().build();
        when(userConsultOrderMapper.padPageByParam(any())).thenReturn(null);

        var list = userConsultOrderGateway.pagePadConsultOrders(goIn);
        assertTrue(list.isEmpty());
    }

    // ====================== 8. countWebConsultPage ======================
    @Test
    void countWebConsultPageNormal() {
        ConsultListGoIn goIn =   ConsultListGoIn.builder().build();
        when(userConsultOrderMapper.countWebPageByParam(any())).thenReturn(200L);

        long total = userConsultOrderGateway.countWebConsultPage(goIn);
        assertEquals(200L, total);
    }

    // ====================== 9. pageWebConsultOrders ======================
    @Test
    void pageWebConsultOrdersNormal() {
        // given
        ConsultListGoIn goIn =   ConsultListGoIn.builder().build();
        UserConsultOrderDO orderDO = new UserConsultOrderDO();
        orderDO.setConsultNo(TEST_CONSULT_NO);
        orderDO.setCreateTime(new Date(DEFAULT_TIME));
        orderDO.setExpectingBackTime(new Date(DEFAULT_TIME));
        when(userConsultOrderMapper.webPageByParam(any())).thenReturn(Collections.singletonList(orderDO));

        // when
        var list = userConsultOrderGateway.pageWebConsultOrders(goIn);

        // then
        assertFalse(list.isEmpty());
        assertNull(list.get(0).getCreateTime());
        assertNull(list.get(0).getExpectingBackTime());
    }

    // ====================== 10. findList ======================
    @Test
    void findListNormal() {
        ConsultListGoIn goIn =   ConsultListGoIn.builder().build();
        UserConsultOrderInfo info = new UserConsultOrderInfo();
        info.setConsultNo(TEST_CONSULT_NO);
        when(userConsultOrderMapper.findList(any())).thenReturn(Collections.singletonList(info));

        var list = userConsultOrderGateway.findList(goIn);
        assertFalse(list.isEmpty());
    }
}