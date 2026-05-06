package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.strategy.ComplaintListFactory;
import com.wt.complaint.manage.domain.strategy.complaintlist.ComplaintListStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ComplaintViewServiceImpl#searchComplaintList 单测（覆盖耗时统计日志相关逻辑�?
 */
@ExtendWith(MockitoExtension.class)
class ComplaintViewServiceImplUnitTest {

    @InjectMocks
    private ComplaintViewServiceImpl complaintViewService;

    @Mock
    private ComplaintListFactory complaintListFactory;

    @Test
    void searchComplaintList_strategyNull_returnsEmptyOut() {
        ComplaintListSearchGoIn param = ComplaintListSearchGoIn.builder().source("pad").build();
        when(complaintListFactory.getStrategy(same(param))).thenReturn(null);

        ComplaintListSearchSoOut result = complaintViewService.searchComplaintList(param);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getTotal());
        Assertions.assertNull(result.getDataList());
        verify(complaintListFactory).getStrategy(param);
    }

    @Test
    void searchComplaintList_strategyReturnsResult_returnsStrategyResult() {
        ComplaintListSearchGoIn param = ComplaintListSearchGoIn.builder().source("pad").build();
        ComplaintListSearchSoOut expectedOut = ComplaintListSearchSoOut.builder()
                .total(10)
                .dataList(Collections.emptyList())
                .build();
        ComplaintListStrategy mockStrategy = param1 -> expectedOut;
        when(complaintListFactory.getStrategy(same(param))).thenReturn(mockStrategy);

        ComplaintListSearchSoOut result = complaintViewService.searchComplaintList(param);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(10, result.getTotal());
        Assertions.assertSame(expectedOut.getDataList(), result.getDataList());
        verify(complaintListFactory).getStrategy(param);
    }

    /**
     * ComplaintViewServiceImpl#initParam 单测，校验字段复制正�?
     */
    @Test
    void initParam_shouldCopyExpectedFields() {
        ComplaintListSearchGoIn source = ComplaintListSearchGoIn.builder()
                .source("pad")
                .orgId("org-001")
                .roleList(Collections.singletonList("roleA"))
                .mid(12345L)
                .currRole("roleA")
                .mediaInvolved(1)
                .onlyShowMyCompositeOrder(Boolean.TRUE)
                .build();

        ComplaintListSearchGoIn target = new ComplaintListSearchGoIn();

        complaintViewService.initParam(target, source);

        Assertions.assertEquals(source.getSource(), target.getSource());
        Assertions.assertEquals(source.getOrgId(), target.getOrgId());
        Assertions.assertEquals(source.getRoleList(), target.getRoleList());
        Assertions.assertEquals(source.getMid(), target.getMid());
        Assertions.assertEquals(source.getCurrRole(), target.getCurrRole());
        Assertions.assertEquals(source.getMediaInvolved(), target.getMediaInvolved());
        Assertions.assertEquals(source.getOnlyShowMyCompositeOrder(), target.getOnlyShowMyCompositeOrder());
    }
}
