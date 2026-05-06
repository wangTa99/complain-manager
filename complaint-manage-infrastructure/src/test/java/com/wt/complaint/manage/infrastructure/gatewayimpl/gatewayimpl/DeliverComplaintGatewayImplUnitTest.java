package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.infrastructure.mapper.DeliverComplaintMapper;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintDO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliverComplaintGatewayImplUnitTest {

    @InjectMocks
    private DeliverComplaintGatewayImpl deliverComplaintGateway;

    @Mock
    private DeliverComplaintMapper deliverComplaintMapper;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSelectEmptyComplaintScene() {
        DeliverComplaintDO d1 = new DeliverComplaintDO();
        d1.setDrNo("DR1");
        DeliverComplaintDO d2 = new DeliverComplaintDO();
        d2.setDrNo("DR2");
        when(deliverComplaintMapper.selectEmptyComplaintScene()).thenReturn(Lists.newArrayList(d1, d2));

        List<DeliverComplaintBO> result = deliverComplaintGateway.selectEmptyComplaintScene();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testUpdateComplaintSceneByDrNo_emptyList_noInvoke() {
        deliverComplaintGateway.updateComplaintSceneByDrNo(new ArrayList<>());
        verify(deliverComplaintMapper, times(0)).batchUpdateComplaintSceneByDrNo(any());
    }

    @Test
    void testUpdateComplaintSceneByDrNo_filterInvalid_noInvoke() {
        List<DeliverComplaintBO> list = new ArrayList<>();
        DeliverComplaintBO invalid = new DeliverComplaintBO();
        invalid.setDrNo(null); // 无效
        list.add(invalid);

        deliverComplaintGateway.updateComplaintSceneByDrNo(list);
        verify(deliverComplaintMapper, times(0)).batchUpdateComplaintSceneByDrNo(any());
    }

    @Test
    void testUpdateComplaintSceneByDrNo_batching_success() {
        // 构�?120 条有效数据，期望�?50/50/20 三批调用
        List<DeliverComplaintBO> list = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            DeliverComplaintBO bo = new DeliverComplaintBO();
            bo.setDrNo("DR" + i);
            bo.setComplaintScene("scene");
            bo.setLastComplaintSceneId(1);
            list.add(bo);
        }

        deliverComplaintGateway.updateComplaintSceneByDrNo(list);

        ArgumentCaptor<List<DeliverComplaintDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintMapper, times(3)).batchUpdateComplaintSceneByDrNo(captor.capture());
        List<List<DeliverComplaintDO>> batches = captor.getAllValues();
        Assertions.assertEquals(3, batches.size());
        Assertions.assertEquals(50, batches.get(0).size());
        Assertions.assertEquals(50, batches.get(1).size());
        Assertions.assertEquals(20, batches.get(2).size());
    }

    @Test
    void testUpdateComplaintSceneByDrNo_throwBusinessException() {
        List<DeliverComplaintBO> list = new ArrayList<>();
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DRX");
        bo.setComplaintScene("scene");
        bo.setLastComplaintSceneId(1);
        list.add(bo);

        doThrow(new RuntimeException("db error")).when(deliverComplaintMapper).batchUpdateComplaintSceneByDrNo(any());
        Assertions.assertThrows(BusinessException.class, () -> deliverComplaintGateway.updateComplaintSceneByDrNo(list));
    }

    @Test
    void testUpdateCityZoneIdByDrNo_emptyList_noInvoke() {
        deliverComplaintGateway.updateCityZoneIdByDrNo(new ArrayList<>());
        verify(deliverComplaintMapper, times(0)).batchUpdateCityZoneIdByDrNo(any());
    }


    @Test
    void testUpdateCityZoneIdByDrNo_batching_success() {
        // 构�?55 条有效数据，期望�?50/5 两批调用
        List<DeliverComplaintBO> list = new ArrayList<>();
        for (int i = 1; i <= 55; i++) {
            DeliverComplaintBO bo = new DeliverComplaintBO();
            bo.setDrNo("DRZ" + i);
            bo.setCityZoneId(100);
            list.add(bo);
        }

        deliverComplaintGateway.updateCityZoneIdByDrNo(list);

        ArgumentCaptor<List<DeliverComplaintDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintMapper, times(2)).batchUpdateCityZoneIdByDrNo(captor.capture());
        List<List<DeliverComplaintDO>> batches = captor.getAllValues();
        Assertions.assertEquals(2, batches.size());
        Assertions.assertEquals(50, batches.get(0).size());
        Assertions.assertEquals(5, batches.get(1).size());
    }

    @Test
    void testUpdateCityZoneIdByDrNo_throwBusinessException() {
        List<DeliverComplaintBO> list = new ArrayList<>();
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DRY");
        bo.setCityZoneId(101);
        list.add(bo);

        doThrow(new RuntimeException("db error")).when(deliverComplaintMapper).batchUpdateCityZoneIdByDrNo(any());
        Assertions.assertThrows(BusinessException.class, () -> deliverComplaintGateway.updateCityZoneIdByDrNo(list));
    }
}


