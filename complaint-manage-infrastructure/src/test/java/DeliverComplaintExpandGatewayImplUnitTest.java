import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintExpandListGoIn;
import com.wt.complaint.manage.domain.bo.DeliverComplaintExpandBO;
import com.wt.complaint.manage.infrastructure.gatewayimpl.DeliverComplaintExpandGatewayImpl;
import com.wt.complaint.manage.infrastructure.mapper.DeliverComplaintExpandMapper;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintExpandDO;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintExpandListDO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author p-wangkai95
 * @date 2025/7/31
 */
@ExtendWith(MockitoExtension.class)
public class DeliverComplaintExpandGatewayImplUnitTest {

    // Mock 依赖服务
    @InjectMocks
    private DeliverComplaintExpandGatewayImpl deliverComplaintExpandGatewayImpl;

    // 注入被测试对�?
    @Mock
    private DeliverComplaintExpandMapper deliverComplaintExpandMapper;


    @Test
    void testSelectCount() {
        // mock 方法返回�?
        when(deliverComplaintExpandMapper.selectCount()).thenReturn(1);
        // 调用被测试方�?
        deliverComplaintExpandGatewayImpl.selectCount();
        // 验证方法调用
        verify(deliverComplaintExpandMapper, times(1)).selectCount();
    }

    @Test
    void testSelectList() {
        // mock 方法返回�?
        when(deliverComplaintExpandMapper.selectList(any(DeliverComplaintExpandListDO.class)))
                .thenReturn(Collections.emptyList());
        // 调用被测试方�?
        DeliverComplaintExpandListGoIn goIn = new DeliverComplaintExpandListGoIn();
        goIn.setOffset(0);
        goIn.setPageSize(10);
        // 调用被测试方�?
        deliverComplaintExpandGatewayImpl.selectList(goIn);
        // 验证方法调用
        verify(deliverComplaintExpandMapper, times(1))
                .selectList(argThat(doObj ->
                        doObj.getOffset() == 0 && doObj.getPageSize() == 10
                ));
    }

    @Test
    void testUpdateBatchSelective() {
        // mock 方法返回�?
        List<DeliverComplaintExpandDO> deliverComplaintExpandBOArrayList = new ArrayList<>();
        when(deliverComplaintExpandMapper.updateBatchSelective(deliverComplaintExpandBOArrayList)).thenReturn(1);
        // 调用被测试方�?
        List<DeliverComplaintExpandBO> deliverComplaintExpandBOList = new ArrayList<>();
        deliverComplaintExpandGatewayImpl.updateBatchSelective(deliverComplaintExpandBOList);
        // 验证方法调用
        verify(deliverComplaintExpandMapper, times(1))
                .updateBatchSelective(deliverComplaintExpandBOArrayList);
    }
}
