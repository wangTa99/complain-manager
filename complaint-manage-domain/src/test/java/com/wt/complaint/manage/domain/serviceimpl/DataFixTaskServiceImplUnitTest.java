package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.PageGoIn;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.api.service.parameter.out.RecordInfoSoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.nr.common.utils.GsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataFixTaskServiceImplUnitTest {

    @InjectMocks
    private DataFixTaskServiceImpl dataFixTaskService;

    @Mock
    private DeliverComplaintGateway deliverComplaintGateway;

    @Mock
    private ComplaintFollowProcessRepositoryGateway followProcessRepositoryGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFillComplaintSceneTask() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DR001");
        bo.setComplaintContent("{\"scene\":\"test\"}");
        when(deliverComplaintGateway.selectEmptyComplaintScene()).thenReturn(Lists.newArrayList(bo));

        doNothing().when(deliverComplaintGateway).updateComplaintSceneByDrNo(any());

        dataFixTaskService.fillComplaintSceneTask("req");

        ArgumentCaptor<List<DeliverComplaintBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintGateway, times(1)).updateComplaintSceneByDrNo(captor.capture());
        Assertions.assertEquals(1, captor.getValue().size());
        // 不断言具体解析值，只验证流程到达更�?
    }

    @Test
    void testFixOperatorPosition() {
        // 构造需要修复的数据（命中两个修复规则）
        RecordInfoSoOut record = RecordInfoSoOut.builder()
                .operatePositionId(String.valueOf(DeliverPositionEnum.REGIONAL_INVITE_MANAGER.getPositionId()))
                .reassignOperatorPositionId(86)
                .operatorPositionId(86)
                .build();

        ComplaintFollowProcessGoOut goOut = ComplaintFollowProcessGoOut.builder()
                .id(1L)
                .processContent(GsonUtil.toJson(record))
                .build();
        when(followProcessRepositoryGateway.selectNeedFixDeliverProcessList())
                .thenReturn(Lists.newArrayList(goOut));

        doNothing().when(followProcessRepositoryGateway).batchUpdateProcessContentById(any());

        dataFixTaskService.fixOperatorPosition("req");

        ArgumentCaptor<List<ComplaintFollowProcessGoIn>> captor = ArgumentCaptor.forClass(List.class);
        verify(followProcessRepositoryGateway, times(1)).batchUpdateProcessContentById(captor.capture());
        Assertions.assertEquals(1, captor.getValue().size());
        // 验证修复后的 JSON 已被重新写入（非空）
        Assertions.assertNotNull(captor.getValue().get(0).getProcessContent());
    }

    @Test
    void testUpdateZoneData_Success() {
        // Mock 总数查询
        when(deliverComplaintGateway.selectCountByCondition(any())).thenReturn(100L);
        
        // Mock 分页查询（模�?页数据）
        DeliverComplaintBO bo1 = new DeliverComplaintBO();
        bo1.setDrNo("DR001");
        bo1.setOrgId("F1");
        bo1.setZoneId(1);
        bo1.setLittleZoneId(2);
        bo1.setCityZoneId(3);
        bo1.setCityId(4);
        
        DeliverComplaintBO bo2 = new DeliverComplaintBO();
        bo2.setDrNo("DR002");
        bo2.setOrgId("F2");
        bo2.setZoneId(10);
        bo2.setLittleZoneId(20);
        bo2.setCityZoneId(30);
        bo2.setCityId(40);
        
        when(deliverComplaintGateway.selectByPageGoIn(any(PageGoIn.class)))
                .thenReturn(Lists.newArrayList(bo1, bo2));
        
        // Mock 门店信息查询
        StoreInfoGoOut store1 = StoreInfoGoOut.builder()
                .orgId("F1")
                .zoneId(11)
                .littleZoneId(22)
                .cityZoneId(33)
                .cityId("44")
                .build();
        
        StoreInfoGoOut store2 = StoreInfoGoOut.builder()
                .orgId("F2")
                .zoneId(55)
                .littleZoneId(66)
                .cityZoneId(77)
                .cityId("88")
                .build();
        
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Lists.newArrayList(store1, store2));
        
        // Mock 更新操作
        doNothing().when(deliverComplaintGateway).updateCityZoneIdByDrNo(any());
        
        // 执行
        dataFixTaskService.updateZoneData("req");
        
        // 验证：应该调用更新（因为zoneId等字段有变化�?
        ArgumentCaptor<List<DeliverComplaintBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintGateway, atLeastOnce()).updateCityZoneIdByDrNo(captor.capture());
    }

    @Test
    void testUpdateZoneData_DataExceedsLimit() {
        // Mock 总数超过限制
        when(deliverComplaintGateway.selectCountByCondition(any())).thenReturn(25000L);
        
        // 应该抛出异常
        Assertions.assertThrows(BusinessException.class, () -> dataFixTaskService.updateZoneData("req"));
    }

    @Test
    void testUpdateZoneData_NoStoreInfo() {
        // Mock 总数查询
        when(deliverComplaintGateway.selectCountByCondition(any())).thenReturn(100L);
        
        // Mock 分页查询
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DR001");
        bo.setOrgId("F1");
        when(deliverComplaintGateway.selectByPageGoIn(any(PageGoIn.class)))
                .thenReturn(Lists.newArrayList(bo));
        
        // Mock 门店信息为空
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Collections.emptyList());
        
        // Mock 更新操作
        doNothing().when(deliverComplaintGateway).updateCityZoneIdByDrNo(any());
        
        // 执行
        dataFixTaskService.updateZoneData("req");
        
        // 验证：没有门店信息，会传入空列表更新
        ArgumentCaptor<List<DeliverComplaintBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintGateway, times(1)).updateCityZoneIdByDrNo(captor.capture());
        Assertions.assertTrue(captor.getValue().isEmpty());
    }

    @Test
    void testUpdateZoneData_IncompleteStoreInfo() {
        // Mock 总数查询
        when(deliverComplaintGateway.selectCountByCondition(any())).thenReturn(100L);
        
        // Mock 分页查询
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DR001");
        bo.setOrgId("F1");
        when(deliverComplaintGateway.selectByPageGoIn(any(PageGoIn.class)))
                .thenReturn(Lists.newArrayList(bo));
        
        // Mock 门店信息不完整（缺少字段�?
        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId("F1")
                .zoneId(11)
                .littleZoneId(null) // 缺少字段
                .cityZoneId(33)
                .cityId("44")
                .build();
        
        when(storeRemoteGateway.getStoreListInfo(any())).thenReturn(Lists.newArrayList(store));
        
        // Mock 更新操作
        doNothing().when(deliverComplaintGateway).updateCityZoneIdByDrNo(any());
        
        // 执行
        dataFixTaskService.updateZoneData("req");
        
        // 验证：门店信息不完整，会跳过该记录，传入空列表更�?
        ArgumentCaptor<List<DeliverComplaintBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(deliverComplaintGateway, times(1)).updateCityZoneIdByDrNo(captor.capture());
        Assertions.assertTrue(captor.getValue().isEmpty());
    }

}


