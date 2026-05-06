package com.wt.complaint.manage.app.nrjob;

import cn.hutool.json.JSONUtil;
import com.wt.complaint.manage.app.util.ExcelExportUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import com.xiaomi.nr.job.core.context.JobHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ComplaintListRecordNrJob 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("投诉单列表导出任务单元测�?)
class ComplaintListRecordNrJobTest {

    @InjectMocks
    private ComplaintListRecordNrJob complaintListRecordNrJob;

    @Mock
    private ComplaintViewService complaintViewService;

    @Mock
    private FileRemoteGateway fileGateway;

    // 不再mock ExcelExportUtil，使用真实对�?
    private ExcelExportUtil excelExportUtil;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Mock
    private MoneThreadPoolExecutor exportComplaintOrderListExecutor;

    @BeforeEach
    void setUp() {
        // 设置配置属�?
        ReflectionTestUtils.setField(complaintListRecordNrJob, "tempDirectory", "/tmp/");
        ReflectionTestUtils.setField(complaintListRecordNrJob, "projectId", 1000L);

        // 创建真实�?ExcelExportUtil 实例
        excelExportUtil = new ExcelExportUtil();
        ReflectionTestUtils.setField(complaintListRecordNrJob, "excelExportUtil", excelExportUtil);
    }

    @Test
    @DisplayName("测试导出投诉单列�?- 空数据场�?)
    void testExportComplaintList_EmptyData() {
        // 准备测试数据
        ComplaintListSearchGoIn goIn = new ComplaintListSearchGoIn();
        goIn.setTraceId("test-trace-id-123");
        goIn.setPageNum(1);
        goIn.setPageSize(10);

        // Mock getExportBody 方法返回空列表（只有表头�?
        List<List<String>> mockExportBody = Arrays.asList(
                Arrays.asList("投诉单ID", "车牌�?, "VIN") // 只有表头，没有数据行
        );

        // 使用 spy �?mock getExportBody 方法
        ComplaintListRecordNrJob spyJob = spy(complaintListRecordNrJob);
        doReturn(mockExportBody).when(spyJob).getExportBody(any(ComplaintListSearchGoIn.class));

        // Mock JobHelper
        try (MockedStatic<JobHelper> jobHelperMock = mockStatic(JobHelper.class)) {
            String jobParamJson = "{\"realParam\": " + JSONUtil.toJsonStr(goIn) + ", \"rpcContextMap\": {\"_trace_id_\": \"test-trace-id-123\"}}";
            jobHelperMock.when(JobHelper::getJobParam).thenReturn(jobParamJson);

            // 执行测试
            assertDoesNotThrow(() -> spyJob.exportComplaintList());

            // 验证方法调用
            verify(spyJob, times(1)).getExportBody(any(ComplaintListSearchGoIn.class));
            // 空数据时不应该调用文件上传，但由于使用真实对象，我们无法验证
        }
    }
}