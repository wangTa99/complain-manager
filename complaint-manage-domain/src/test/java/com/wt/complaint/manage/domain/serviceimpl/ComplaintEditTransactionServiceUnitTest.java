package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.aggregation.ComplaintOrderAggregation;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderEditComplaintSoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ComplaintEditTransactionService单元测试
 * 测试编辑客诉单事务服�?
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
@ExtendWith(MockitoExtension.class)
public class ComplaintEditTransactionServiceUnitTest {

    @InjectMocks
    private ComplaintEditTransactionService complaintEditTransactionService;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @BeforeEach
    void setUp() {
        // 初始化操�?
    }

    /**
     * 测试事务执行成功
     */
    @Test
    void testDoEditComplaintInTransaction_Success() {
        // 准备数据
        String complaintNo = "C001";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(orderInfo)
                .complaintFollowProcessGoIn(ComplaintFollowProcessGoIn.builder()
                        .complaintNo(complaintNo)
                        .processType("32")
                        .processContent("{}")
                        .build())
                .build();
        
        // Mock 数据库操作成�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行 - 不应抛异�?
        assertDoesNotThrow(() -> {
            complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        });
        
        // 验证数据库操作被调用
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(any());
    }

    /**
     * 测试保存跟进记录
     */
    @Test
    void testDoEditComplaintInTransaction_SaveFollowRecord() {
        // 准备数据
        String complaintNo = "C002";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        
        ComplaintFollowProcessGoIn followProcess = ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType("32")
                .processContent("{\"operateMid\":\"1001\",\"riskLevelChange\":\"由L1更新为L2\"}")
                .build();
        
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(orderInfo)
                .complaintFollowProcessGoIn(followProcess)
                .build();
        
        // Mock 数据库操作成�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行
        complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        
        // 验证跟进记录保存被调�?
        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(followProcess);
    }

    /**
     * 测试更新订单信息
     */
    @Test
    void testDoEditComplaintInTransaction_UpdateOrder() {
        // 准备数据
        String complaintNo = "C003";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        orderInfo.setRiskLevel(2); // 更新风险等级
        
        ComplaintFollowProcessGoIn followProcess = ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType("32")
                .processContent("{}")
                .build();
        
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(orderInfo)
                .complaintFollowProcessGoIn(followProcess)
                .build();
        
        // Mock 数据库操作成�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行
        complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        
        // 验证订单更新被调�?
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(orderInfo);
    }

    /**
     * 测试无变更项拦截
     */
    @Test
    void testDoEditComplaintInTransaction_NoChange_Skip() {
        // 准备数据 - 无变更项
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(null)
                .complaintFollowProcessGoIn(null)
                .build();
        
        // 执行
        complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        
        // 验证数据库操作未被调�?
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
        verify(complaintFollowProcessRepositoryGateway, never()).saveComplaintFollowProcess(any());
    }

    /**
     * 测试更新失败抛异�?
     */
    @Test
    void testDoEditComplaintInTransaction_UpdateFailed_ThrowException() {
        // 准备数据
        String complaintNo = "C004";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(orderInfo)
                .complaintFollowProcessGoIn(ComplaintFollowProcessGoIn.builder()
                        .complaintNo(complaintNo)
                        .processType("32")
                        .processContent("{}")
                        .build())
                .build();
        
        // Mock 数据库操作失�?
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(false);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        
        // 执行并验�?- 应抛出异�?
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        });
        
        assertTrue(exception.getMessage().contains("编辑客诉单失�?));
    }

    /**
     * 测试保存跟进记录失败抛异�?
     */
    @Test
    void testDoEditComplaintInTransaction_SaveRecordFailed_ThrowException() {
        // 准备数据
        String complaintNo = "C005";
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(
                complaintNo, ComplaintTypeEnum.PRODUCT_RISK.getCode());
        
        ComplaintOrderAggregation aggregation = ComplaintOrderAggregation.builder()
                .complaintOrderInfoGoIn(orderInfo)
                .complaintFollowProcessGoIn(ComplaintFollowProcessGoIn.builder()
                        .complaintNo(complaintNo)
                        .processType("32")
                        .processContent("{}")
                        .build())
                .build();
        
        // Mock 数据库操�?- 跟进记录保存失败
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);
        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(false);
        
        // 执行并验�?- 应抛出异�?
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            complaintEditTransactionService.doEditComplaintInTransaction(aggregation);
        });
        
        assertTrue(exception.getMessage().contains("编辑客诉单失�?));
    }
}
