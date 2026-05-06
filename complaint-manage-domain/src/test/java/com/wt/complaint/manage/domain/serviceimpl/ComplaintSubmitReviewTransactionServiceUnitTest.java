package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ComplaintSubmitReviewTransactionService} 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ComplaintSubmitReviewTransactionServiceUnitTest {

    @InjectMocks
    private ComplaintSubmitReviewTransactionService complaintSubmitReviewTransactionService;

    @Mock
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Test
    void doSubmitReviewInTransaction_Success() {
        String complaintNo = "C030";
        ComplaintFollowProcessGoIn follow = ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType(ProcessTypeEnum.SUBMIT_REVIEW.getProcessCode())
                .processContent("{}")
                .build();
        ComplaintOrderInfoGoIn update = ComplaintOrderInfoGoIn.builder()
                .complaintNo(complaintNo)
                .reviewed(1)
                .build();

        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(true);

        complaintSubmitReviewTransactionService.doSubmitReviewInTransaction(follow, update);

        verify(complaintFollowProcessRepositoryGateway).saveComplaintFollowProcess(follow);
        verify(complaintOrderRepositoryGateway).updateComplaintInfo(update);
    }

    @Test
    void doSubmitReviewInTransaction_SaveFailed_Throws() {
        String complaintNo = "C031";
        ComplaintFollowProcessGoIn follow = ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType(ProcessTypeEnum.SUBMIT_REVIEW.getProcessCode())
                .processContent("{}")
                .build();
        ComplaintOrderInfoGoIn update = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo,
                ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        update.setReviewed(1);

        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintSubmitReviewTransactionService.doSubmitReviewInTransaction(follow, update));
        assertTrue(ex.getMessage().contains("提交复盘失败"));
        verify(complaintOrderRepositoryGateway, never()).updateComplaintInfo(any());
    }

    @Test
    void doSubmitReviewInTransaction_UpdateFailed_Throws() {
        String complaintNo = "C032";
        ComplaintFollowProcessGoIn follow = ComplaintFollowProcessGoIn.builder()
                .complaintNo(complaintNo)
                .processType(ProcessTypeEnum.SUBMIT_REVIEW.getProcessCode())
                .processContent("{}")
                .build();
        ComplaintOrderInfoGoIn update = ComplaintOrderInfoGoIn.builder()
                .complaintNo(complaintNo)
                .reviewed(1)
                .build();

        when(complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(any())).thenReturn(true);
        when(complaintOrderRepositoryGateway.updateComplaintInfo(any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> complaintSubmitReviewTransactionService.doSubmitReviewInTransaction(follow, update));
        assertTrue(ex.getMessage().contains("提交复盘失败"));
    }
}
