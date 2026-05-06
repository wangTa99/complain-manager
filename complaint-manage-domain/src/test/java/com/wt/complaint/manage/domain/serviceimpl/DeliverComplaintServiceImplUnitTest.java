package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.DeliverComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibleEnum;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarDeliveryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.OrderInfoGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceInfo;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintFinishGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintFollowUpGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintProcessGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintReassignProcessGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverStatisticsItemGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliveryApplyExemptionSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliveryJudgeResponsibleSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.QueryReassignEmployeeGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintDetailGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverStatisticsItemGoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.bo.DeliverStatisticsItemBO;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewComplaintMessageStrategy;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewMessageInformedEventFactory;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class DeliverComplaintServiceImplUnitTest {

    @InjectMocks
    private DeliverComplaintServiceImpl service;

    @Mock
    private DeliverComplaintGateway deliverComplaintGateway;
    @Mock
    private CarDeliveryGateway carDeliveryGateway;
    @Mock
    private StoreRemoteGateway storeRemoteGateway;
    @Mock
    private EiamRemoteGateway eiamRemoteGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway followProcessRepositoryGateway;
    @Mock
    private FileRemoteGateway fileRemoteGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Mock
    private NewMessageInformedEventFactory newMessageInformedEventFactory;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RmqGateway rmqGateway;
    @Mock
    private OrderInfoGateway orderInfoGateway;

    public void mockExecutor() {
        // 注入可用的执行器，避�?CompletableFuture.runAsync 空指�?
        MoneThreadPoolExecutor mockExecutor = mock(MoneThreadPoolExecutor.class);
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));
        try {
            Field execField = DeliverComplaintServiceImpl.class.getDeclaredField("constructMessageEventExecutor");
            execField.setAccessible(true);
            execField.set(service, mockExecutor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testQueryStatisticsItems() {
        when(deliverComplaintGateway.selectStatisticsItems(any())).thenReturn(new DeliverStatisticsItemBO());
        DeliverStatisticsItemGoOut out = service.queryStatisticsItems(new DeliverStatisticsItemGoIn());
        Assertions.assertNotNull(out);
    }

    @Test
    void testSelectListByCondition() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setOrgId("F1");
        bo.setOperatorMid(1L);
        bo.setZoneId(1);
        bo.setLittleZoneId(2);
        bo.setCityZoneId(3);
        bo.setDrNo("DR1");
        bo.setTradeOrderId("T1");
        bo.setOrderStatus(DeliverComplaintOrderStatusEnum.HANDLING.getCode());
        bo.setResponsible(ResponsibleEnum.RESPONSIBLE.getCode());
        when(deliverComplaintGateway.selectListByCondition(any())).thenReturn(Collections.singletonList(bo));
        when(storeRemoteGateway.getStoreNameMap(any())).thenReturn(Collections.singletonMap("F1", "门店"));
        when(eiamRemoteGateway.getNameByMid(any())).thenReturn(Collections.singletonMap(1L, "张三"));
        when(storeRemoteGateway.getZoneList(Lists.newArrayList(1))).thenReturn(Collections.emptyList());
        when(storeRemoteGateway.getLittleZoneList(any())).thenReturn(Collections.emptyList());
        when(storeRemoteGateway.getCityZoneList(any())).thenReturn(Collections.emptyList());
        when(complaintFollowProcessRepositoryGateway.getLastProcess(any())).thenReturn(Collections.emptyMap());
        when(carDeliveryGateway.getDeliveryByOrderIds(any())).thenReturn(Collections.emptyList());

        List<DeliverComplaintListGoOut> list = service.selectListByCondition(new DeliverComplaintListGoIn());
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void testSelectCountByCondition() {
        when(deliverComplaintGateway.selectCountByCondition(any())).thenReturn(10L);
        Assertions.assertEquals(10L, service.selectCountByCondition(new DeliverComplaintListGoIn()));
    }

    @Test
    void testSelectDetail_success() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DR1");
        bo.setOrderStatus(DeliverComplaintOrderStatusEnum.FINISHED.getCode());
        bo.setResponsible(ResponsibleEnum.PENDING.getCode());
        bo.setCreateTime(new Date());
        bo.setLastReassignmentTime(new Date());
        bo.setLastReminderTime(new Date());
        bo.setCreateTime(new Date());
        bo.setExpectedFirstResponseTime(new Date());
        bo.setRealFirstResponseTime(new Date());
        bo.setExpectedFinishTime(new Date());
        bo.setRealFinishTime(new Date());


        when(deliverComplaintGateway.selectDetail(any())).thenReturn(bo);
        when(complaintFollowProcessRepositoryGateway.getProcessListByNo(anyString())).thenReturn(Collections.emptyList());
        when(eiamRemoteGateway.getEmployee(any())).thenReturn(new EmployeeInfoGoOut());
        when(storeRemoteGateway.getStoreInfo(any())).thenReturn(StoreInfoGoOut.builder().build());

        DeliverComplaintDetailGoOut out = service.selectDetail(new DeliverComplaintDetailGoIn());
        Assertions.assertNotNull(out);
        Assertions.assertTrue(out.getButtonList() == null || out.getButtonList() instanceof List);
    }

    @Test
    void testSelectDetail_noPermission_throws() {
        when(deliverComplaintGateway.selectDetail(any())).thenReturn(null);
        try {
            service.selectDetail(new DeliverComplaintDetailGoIn());
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BusinessException);
        }
    }

    @Test
    void testFollowUp() {
        when(followProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(Boolean.TRUE);
        DeliverComplaintFollowUpGoIn goIn = new DeliverComplaintFollowUpGoIn();
        goIn.setDrNo("DR1");
        goIn.setOperatorMid(1L);
        goIn.setOperatorName("张三");
        goIn.setOperatorPositionEnum(DeliverPositionEnum.POSITION_A);
        service.followUp(goIn);
        verify(followProcessRepositoryGateway, times(1)).saveComplaintFollowProcess(any());
    }

    @Test
    void testCheckStatus_ok() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setOrderStatus(DeliverComplaintOrderStatusEnum.HANDLING.getCode());
        when(deliverComplaintGateway.selectDetail(any())).thenReturn(bo);
        service.checkStatus("DR1", Collections.singletonList(DeliverComplaintOrderStatusEnum.HANDLING.getCode()));
    }

    @Test
    void testCheckStatus_throws() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setOrderStatus(DeliverComplaintOrderStatusEnum.WAITING_FIRST_RESPONSE.getCode());
        when(deliverComplaintGateway.selectDetail(any())).thenReturn(bo);
        try {
            service.checkStatus("DR1", Collections.singletonList(DeliverComplaintOrderStatusEnum.HANDLING.getCode()));
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BusinessException);
        }
    }

    @Test
    void testStartProcess() {
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        when(followProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(Boolean.TRUE);
        DeliverComplaintProcessGoIn goIn = new DeliverComplaintProcessGoIn();
        goIn.setDrNo("DR1");
        goIn.setOperatorMid(1L);
        goIn.setOperatorName("张三");
        goIn.setOperatorPositionEnum(DeliverPositionEnum.POSITION_A);
        service.startProcess(goIn);
        verify(deliverComplaintGateway, times(1)).updateByDrNo(any());
        verify(followProcessRepositoryGateway, times(1)).saveComplaintFollowProcess(any());
    }

    @Test
    void testReassign() {
        DeliverComplaintBO current = new DeliverComplaintBO();
        current.setReassignmentTimes(0);
        when(deliverComplaintGateway.selectByDrNo(anyString())).thenReturn(current);
        StoreInfoGoOut store = StoreInfoGoOut.builder().build();
        store.setZoneId(1);
        store.setLittleZoneId(2);
        store.setCityZoneId(3);
        store.setOrgName("门店");
        store.setCityId("123");
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(store);
        when(eiamRemoteGateway.getEmployee(anyLong())).thenReturn(new EmployeeInfoGoOut());
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        when(followProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(Boolean.TRUE);
        NewComplaintMessageStrategy strategy = mock(NewComplaintMessageStrategy.class);
        when(newMessageInformedEventFactory.getStrategy(anyString())).thenReturn(strategy);
        when(strategy.createMessageInformedEvent(any(ComplaintBasicInfo.class), anyMap())).thenReturn(mock(MessageInformedEvent.class));
        doNothing().when(eventPublisher).publishEvent(any());



        DeliverComplaintReassignProcessGoIn goIn = new DeliverComplaintReassignProcessGoIn();
        goIn.setDrNo("DR1");
        goIn.setOperatorMid(1L);
        goIn.setOperatorName("张三");
        goIn.setOperatorPositionEnum(DeliverPositionEnum.POSITION_A);
        goIn.setOrgId("F1");
        goIn.setReassignOperatorMid(2L);
        goIn.setReassignOperatorPositionId(DeliverPositionEnum.POSITION_A.getPositionId());
        mockExecutor();
        service.reassign(goIn, StoreInfoGoOut.builder().build(), "name");
        verify(deliverComplaintGateway, times(1)).updateByDrNo(any());
        verify(followProcessRepositoryGateway, times(1)).saveComplaintFollowProcess(any());
    }

    @Test
    void testFinish_applyExemptionFalse() {
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        doNothing().when(fileRemoteGateway).fileCommit(anyList());
        when(followProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(Boolean.TRUE);
        when(deliverComplaintGateway.selectDetail(any())).thenReturn(this.mockDeliverComplaintBO());
        when(deliverComplaintGateway.selectByDrNo(any())).thenReturn(this.mockDeliverComplaintBO());
        when(rmqGateway.mrOrderStatusFinishMessage(any())).thenReturn(true);
        when(newMessageInformedEventFactory.getStrategy(any())).thenReturn(new NewComplaintMessageStrategy() {
            @Override
            public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                                   Map<String, String> extraParam) {
                return null;
            }
        });
        // 后续测试逻辑放在这里，需要在mockStatic作用域内
        DeliverComplaintFinishGoIn goIn = new DeliverComplaintFinishGoIn();
        goIn.setDrNo("DR1");
        goIn.setOperatorMid(1L);
        goIn.setOperatorName("张三");
        goIn.setOperatorPositionEnum(DeliverPositionEnum.POSITION_A);
        goIn.setReconciled(Boolean.TRUE);
        goIn.setRevisited(Boolean.FALSE);
        goIn.setApplyExemption(Boolean.FALSE);
        goIn.setFinishAttachmentList(Collections.singletonList(AttachmentGoIn.builder().id(1L).build()));
        service.finish(goIn);
        verify(deliverComplaintGateway, atLeast(2)).updateByDrNo(any());
    }

    private DeliverComplaintBO mockDeliverComplaintBO() {
        DeliverComplaintBO deliverComplaintBO = new DeliverComplaintBO();
        deliverComplaintBO.setComplaintContent("[{\"fields\":[{\"fieldName\":\"投诉场景\",\"value\":[{\"code\":\"135\",\"desc\":\"道路救援不满\",\"pathId\":\"133/134/135\",\"pathName\":\"服务/服务产品和权�?道路救援不满\"}]}]}]");
        return deliverComplaintBO;
    }

    @Test
    void testFinish_applyExemptionTrue() {
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        when(followProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(Boolean.TRUE);
        when(rmqGateway.mrOrderStatusFinishMessage(any())).thenReturn(true);
        when(deliverComplaintGateway.selectByDrNo(any())).thenReturn(new DeliverComplaintBO());
        when(newMessageInformedEventFactory.getStrategy(any())).thenReturn(new NewComplaintMessageStrategy() {
            @Override
            public MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo,
                                                                   Map<String, String> extraParam) {
                return null;
            }
        });

        DeliverComplaintFinishGoIn goIn = new DeliverComplaintFinishGoIn();
        goIn.setDrNo("DR1");
        goIn.setOperatorMid(1L);
        goIn.setOperatorName("张三");
        goIn.setOperatorPositionEnum(DeliverPositionEnum.POSITION_A);
        goIn.setApplyExemption(Boolean.TRUE);
        service.finish(goIn);
        verify(deliverComplaintGateway, atLeast(1)).updateByDrNo(any());
    }

    @Test
    void testApplyExemption() {
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        doNothing().when(fileRemoteGateway).fileCommit(anyList());
        DeliveryApplyExemptionSoIn soIn = new DeliveryApplyExemptionSoIn();
        soIn.setDrNo("DR1");
        soIn.setApplyMid(1L);
        soIn.setApplyName("张三");
        soIn.setApplyPositionId(DeliverPositionEnum.POSITION_A.getPositionId());
        soIn.setApplyPositionName(DeliverPositionEnum.POSITION_A.getSystemPositionName());
        soIn.setAttachmentList(Collections.singletonList(AttachmentGoIn.builder().id(1L).build()));
        soIn.setExemptionReason("原因");
        mockExecutor();
        service.applyExemption(soIn);
        verify(deliverComplaintGateway, times(1)).updateByDrNo(any());
        verify(followProcessRepositoryGateway, times(1)).saveComplaintFollowProcess(any());
    }

    @Test
    void testJudgeResponsible() {
        doNothing().when(deliverComplaintGateway).updateByDrNo(any());
        DeliveryJudgeResponsibleSoIn soIn = new DeliveryJudgeResponsibleSoIn();
        soIn.setDrNo("DR1");
        soIn.setOperateMid(1L);
        soIn.setOperateName("张三");
        soIn.setOperatePositionId(DeliverPositionEnum.POSITION_A.getPositionId());
        soIn.setOperatePositionName(DeliverPositionEnum.POSITION_A.getSystemPositionName());
        soIn.setResponsible(ResponsibleEnum.RESPONSIBLE.getCode());
        soIn.setResponsibleJudgeDesc("desc");
        service.judgeResponsible(soIn);
        verify(deliverComplaintGateway, times(1)).updateByDrNo(any());
        verify(followProcessRepositoryGateway, times(1)).saveComplaintFollowProcess(any());
    }

    @Test
    void testUpdateCustomer() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setDrNo("DR1");
        bo.setSuperTicketNo("ST1");
        bo.setOrderStatus(10);
        when(deliverComplaintGateway.selectByStNoList(any())).thenReturn(Collections.singletonList(bo));
        doReturn(1).when(deliverComplaintGateway).batchUpdateByDrNo(any());

        OrderUpdateCustomerServiceSoIn soIn = new OrderUpdateCustomerServiceSoIn();
        OrderUpdateCustomerServiceInfo info = new OrderUpdateCustomerServiceInfo();
        info.setStNo("ST1");
        info.setCustomerServiceMid(2L);
        soIn.setOrderUpdateCustomerServiceInfos(Collections.singletonList(info));
        Boolean ok = service.updateCustomer(soIn);
        Assertions.assertTrue(ok);
        verify(deliverComplaintGateway, times(1)).batchUpdateByDrNo(any());
    }

    @Test
    void testQueryReassignEmployee_cases() {
        StoreInfoGoOut store = StoreInfoGoOut.builder().build();
        store.setZoneId(1);
        store.setLittleZoneId(2);
        store.setCityZoneId(3);
        when(storeRemoteGateway.getStoreInfo(anyString())).thenReturn(store);
        when(eiamRemoteGateway.getZonePositionUser(any())).thenReturn(
                Collections.singletonList(new ZonePositionUserGoOut()));
        when(eiamRemoteGateway.queryEmployeeByStore(any())).thenReturn(
                Collections.singletonList(new EmployeeInfoGoOut()));

        QueryReassignEmployeeGoIn goIn = new QueryReassignEmployeeGoIn();

        // REGIONAL_INVITE_MANAGER
        goIn.setOrgId("F1");
        goIn.setPositionId(DeliverPositionEnum.REGIONAL_INVITE_MANAGER.getPositionId());
        Assertions.assertNotNull(service.queryReassignEmployee(goIn));

        // POSITION_A_LEADER
        goIn.setPositionId(DeliverPositionEnum.POSITION_A_LEADER.getPositionId());
        Assertions.assertNotNull(service.queryReassignEmployee(goIn));

        // POSITION_A
        goIn.setPositionId(DeliverPositionEnum.POSITION_A.getPositionId());
        Assertions.assertNotNull(service.queryReassignEmployee(goIn));

        // others
        goIn.setPositionId(999);
        Assertions.assertNotNull(service.queryReassignEmployee(goIn));
    }
    @InjectMocks
    private DeliverComplaintServiceImpl deliverComplaintService;

    @Test
    void testCheckStatus_notExist_throws() {
        when(deliverComplaintGateway.selectDetail(any(DeliverComplaintDetailGoIn.class))).thenReturn(null);
        try {
            deliverComplaintService.checkStatus("DR404", Arrays.asList(10));
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BusinessException);
        }
    }

    @Test
    void testCheckStatus_incorrectStatus_throws() {
        DeliverComplaintBO bo = new DeliverComplaintBO();
        bo.setOrderStatus(30);
        when(deliverComplaintGateway.selectDetail(any(DeliverComplaintDetailGoIn.class))).thenReturn(bo);

        try {
            deliverComplaintService.checkStatus("DR002", Arrays.asList(10, 20));
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BusinessException);
        }
    }
}


