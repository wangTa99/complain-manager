package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintFollowProcessMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintFollowProcessDO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintFollowProcessRepositoryGatewayImplUnitTest {

    @InjectMocks
    private ComplaintFollowProcessRepositoryGatewayImpl repositoryGateway;

    @Mock
    private ComplaintFollowProcessMapper followProcessMapper;

    @Test
    void testSelectNeedFixDeliverProcessList() {
        ComplaintFollowProcessDO d1 = new ComplaintFollowProcessDO();
        d1.setId(1L);
        d1.setComplaintNo("DR1");
        when(followProcessMapper.selectNeedFixDeliverProcessList()).thenReturn(Lists.newArrayList(d1));

        List<ComplaintFollowProcessGoOut> result = repositoryGateway.selectNeedFixDeliverProcessList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DR1", result.get(0).getComplaintNo());
    }

    @Test
    void testBatchUpdateProcessContentById_empty_noInvoke() {
        repositoryGateway.batchUpdateProcessContentById(new ArrayList<>());
        verify(followProcessMapper, times(0)).batchUpdateProcessContentById(any());
    }

    @Test
    void testBatchUpdateProcessContentById_filterInvalid_noInvoke() {
        List<ComplaintFollowProcessGoIn> list = new ArrayList<>();
        ComplaintFollowProcessGoIn invalid = ComplaintFollowProcessGoIn.builder().build();
        invalid.setId(null); // 无效记录
        list.add(invalid);

        repositoryGateway.batchUpdateProcessContentById(list);
        verify(followProcessMapper, times(0)).batchUpdateProcessContentById(any());
    }

    @Test
    void testBatchUpdateProcessContentById_batching_success() {
        // 构�?120 条有效数据，期望�?50/50/20 三批
        List<ComplaintFollowProcessGoIn> list = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            ComplaintFollowProcessGoIn goIn = ComplaintFollowProcessGoIn.builder().build();
            goIn.setId((long) i);
            goIn.setComplaintNo("DR" + i);
            goIn.setProcessContent("{}");
            list.add(goIn);
        }

        repositoryGateway.batchUpdateProcessContentById(list);

        ArgumentCaptor<List<ComplaintFollowProcessDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(followProcessMapper, times(3)).batchUpdateProcessContentById(captor.capture());
        List<List<ComplaintFollowProcessDO>> batches = captor.getAllValues();
        Assertions.assertEquals(3, batches.size());
        Assertions.assertEquals(50, batches.get(0).size());
        Assertions.assertEquals(50, batches.get(1).size());
        Assertions.assertEquals(20, batches.get(2).size());
    }

    @Test
    void testBatchUpdateProcessContentById_throwBusinessException() {
        List<ComplaintFollowProcessGoIn> list = new ArrayList<>();
        ComplaintFollowProcessGoIn goIn = ComplaintFollowProcessGoIn.builder().build();
        goIn.setId(1L);
        goIn.setComplaintNo("DRX");
        goIn.setProcessContent("{}");
        list.add(goIn);

        doThrow(new RuntimeException("db error")).when(followProcessMapper).batchUpdateProcessContentById(any());
        Assertions.assertThrows(BusinessException.class, () -> repositoryGateway.batchUpdateProcessContentById(list));
    }
}


