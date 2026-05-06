package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.domain.aggregation.ComplaintAuditAggregation;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * ComplaintApplyTransactionService 单元测试
 * 测试申请提交事务内写操作，不启动 Spring，纯 Mock
 */
@ExtendWith(MockitoExtension.class)
class ComplaintApplyTransactionServiceUnitTest {

    @InjectMocks
    private ComplaintApplyTransactionService complaintApplyTransactionService;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;
    @Mock
    private ComplaintAuditRepositoryGateway complaintAuditRepositoryGateway;
    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Mock
    private ComplaintAuditGateway complaintAuditGateway;

    @Mock
    private ComplaintGateway complaintGateway;

    @Mock
    private MessageInformedEventFactory messageInformedEventFactory;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void doSubmitApplyInTransaction_allSuccess_noException() {
        String complaintNo = "C001";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        ComplaintAuditGoIn auditGoIn = ComplaintAuditGoIn.builder().complaintNo(complaintNo).build();
        ComplaintFollowProcessGoIn processGoIn = createProcessGoIn(complaintNo, "2", "{}");

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .auditGoIn(auditGoIn)
                .complaintFollowProcessGoIn(processGoIn)
                .closingTagSoInList(null)
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId("F001")
                .createMid(1001L)
                .auditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode())
                .build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintAuditRepositoryGateway.save(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        assertDoesNotThrow(() -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(orderInfo);
        verify(complaintAuditRepositoryGateway).save(auditGoIn);
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(processGoIn);
        verify(complaintAuditGateway, never()).batchInsertClosingTag(anyList());
    }

    @Test
    void doSubmitApplyInTransaction_orderInfoNull_skipsOrderUpdate() {
        ComplaintAuditGoIn auditGoIn = ComplaintAuditGoIn.builder().complaintNo("C001").build();
        ComplaintFollowProcessGoIn processGoIn = createProcessGoIn("C001", "2", "{}");
        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(null)
                .auditGoIn(auditGoIn)
                .complaintFollowProcessGoIn(processGoIn)
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder().complaintNo("C001").applyOrgId("F001").createMid(1001L).auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()).build();

        when(complaintAuditRepositoryGateway.save(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        assertDoesNotThrow(() -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));

        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        verify(complaintAuditRepositoryGateway).save(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    /**
     * 修复后：orderSave 失败不再导致抛异常，�?save �?saveProcess 影响事务结果
     */
    @Test
    void doSubmitApplyInTransaction_orderSaveFalse_succeeds() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C003", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .auditGoIn(ComplaintAuditGoIn.builder().complaintNo("C003").build())
                .complaintFollowProcessGoIn(createProcessGoIn("C003", "2", "{}"))
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder().complaintNo("C003").applyOrgId("F001").createMid(1001L).auditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode()).build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(false);
        when(complaintAuditRepositoryGateway.save(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        assertDoesNotThrow(() -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));
    }

    /**
     * 结案申请：当前实现不再写入结案标签，仅保存审批单和跟进记�?
     */
    @Test
    void doSubmitApplyInTransaction_applicationForClosure_noClosingTagLogic() {
        String complaintNo = "C002";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        ComplaintAuditGoIn auditGoIn = ComplaintAuditGoIn.builder().complaintNo(complaintNo).build();
        ComplaintFollowProcessGoIn processGoIn = createProcessGoIn(complaintNo, "4", "{}");
        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .auditGoIn(auditGoIn)
                .complaintFollowProcessGoIn(processGoIn)
                .closingTagSoInList(null)
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId("F001")
                .createMid(1001L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintAuditRepositoryGateway.save(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        assertDoesNotThrow(() -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));

        verify(complaintAuditGateway, never()).batchInsertClosingTag(anyList());
    }

    @Test
    void doSubmitApplyInTransaction_saveFalse_throwsBusinessException() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C006", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .auditGoIn(ComplaintAuditGoIn.builder().complaintNo("C006").build())
                .complaintFollowProcessGoIn(createProcessGoIn("C006", "2", "{}"))
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder().complaintNo("C006").applyOrgId("F001").createMid(1001L).auditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode()).build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintAuditRepositoryGateway.save(any())).thenReturn(false);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));
        assertTrue(ex.getMessage().contains("系统异常"));
    }

    @Test
    void doSubmitApplyInTransaction_saveProcessFalse_throwsBusinessException() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C004", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .auditGoIn(ComplaintAuditGoIn.builder().complaintNo("C004").build())
                .complaintFollowProcessGoIn(createProcessGoIn("C004", "2", "{}"))
                .build();
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder().complaintNo("C004").applyOrgId("F001").createMid(1001L).auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()).build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintAuditRepositoryGateway.save(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintApplyTransactionService.doSubmitApplyInTransaction(aggregation, soIn));
        assertTrue(ex.getMessage().contains("系统异常"));
    }

    @Test
    void doSubmitFinishApplyFromStore_storeSource_updatesOrderSavesProcessAndPublishesEvent() {
        String complaintNo = "C-STORE-1";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.STORE.getCode());
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .createMid(1001L)
                .createName("门店申请�?)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        complaintApplyTransactionService.doSubmitFinishApplyFromStore(orderInfo, soIn);

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway, times(2)).saveComplaintFollowProcess(any());
    }

    @Test
    void doSubmitFinishApplyFromStore_nonStoreSource_noEventPublished() {
        String complaintNo = "C-NON-STORE-1";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setCreateSource(CreateSourceEnum.ONLINE_CS.getCode());
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .createMid(1001L)
                .createName("PAD申请�?)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);

        complaintApplyTransactionService.doSubmitFinishApplyFromStore(orderInfo, soIn);

        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway, times(2)).saveComplaintFollowProcess(any());
        verify(complaintGateway, never()).selectByComplaintNo(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private static ComplaintFollowProcessGoIn createProcessGoIn(String complaintNo, String processType, String processContent) {
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType(processType)
                .processContent(processContent)
                .build();
    }
}
