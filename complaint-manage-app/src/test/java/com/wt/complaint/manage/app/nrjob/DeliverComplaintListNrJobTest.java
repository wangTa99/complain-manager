package com.wt.complaint.manage.app.nrjob;

import cn.hutool.json.JSONUtil;
import com.wt.complaint.manage.api.model.enums.TimeoutOptionEnum;
import com.wt.complaint.manage.app.util.ExcelExportUtil;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintService;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.bo.FileInfoBO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DeliverComplaintListNrJob 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("交付投诉单列表导出任务单元测�?)
class DeliverComplaintListNrJobTest {

    @InjectMocks
    private DeliverComplaintListNrJob deliverComplaintListNrJob;

    @Mock
    private ExcelExportUtil excelExportUtil;

    @Mock
    private DeliverComplaintService deliverComplaintService;

    @BeforeEach
    void setUp() {
        // 设置配置属�?
        ReflectionTestUtils.setField(deliverComplaintListNrJob, "tempDirectory", "/tmp/");
        ReflectionTestUtils.setField(deliverComplaintListNrJob, "jobProjectId", 2000L);
    }

    @Test
    @DisplayName("测试导出交付投诉单列�?- 正常执行场景")
    void testDeliverComplaintListExportHandler_Success() {
        // 准备测试数据
        DeliverComplaintListGoIn goIn = new DeliverComplaintListGoIn();
        goIn.setTraceId("test-trace-id-789");

        // 模拟查询结果
        List<DeliverComplaintListGoOut> goOuts = new ArrayList<>();
        DeliverComplaintListGoOut goOut = new DeliverComplaintListGoOut();
        goOut.setDrNo("DR202401010001");
        goOut.setLastReminderTime(System.currentTimeMillis());
        goOut.setReminderTimes(3);
        goOut.setTradeOrderId("TO123456789");
        goOut.setCarTypeName("小米SU7");
        goOut.setSaleCarVersion("Max�?);
        goOut.setContactName("李四");
        goOut.setContactPhone("13800138000");
        goOut.setProblemCategory("交付问题");
        goOut.setComplaintScene("交付延期");
        goOut.setRiskLevelName("中风�?);
        goOut.setProblemDesc("交付时间延迟");
        goOut.setCreateTime(System.currentTimeMillis());
        goOut.setOrderStatusName("处理�?);
        goOut.setZoneName("华东大区");
        goOut.setLittleZoneName("上海小区");
        goOut.setCityZoneName("上海�?);
        goOut.setPositionAUserName("王五");
        goOut.setPositionBUserName("赵六");
        goOut.setOrgName("上海交付中心");
        goOut.setOperatorPositionName("交付专员");
        goOut.setOperatorName("孙七");
        goOut.setExpectedFirstResponseTime(System.currentTimeMillis());
        goOut.setRealFirstResponseTime(System.currentTimeMillis());
        goOut.setFirstResponseTag(TimeoutOptionEnum.NO.getCode());
        goOut.setLastFollowDesc("已联系客户，正在处理");
        goOut.setExpectedFinishTime(System.currentTimeMillis());
        goOut.setRealFinishTime(System.currentTimeMillis());
        goOut.setFinishTag(TimeoutOptionEnum.YES.getCode());
        goOut.setResponsibleName("有责");
        goOuts.add(goOut);

        // Mock 服务调用
        when(deliverComplaintService.selectListByCondition(any(DeliverComplaintListGoIn.class)))
                .thenReturn(goOuts);

        // Mock 文件上传
        FileInfoBO fileInfoBO = new FileInfoBO();
        fileInfoBO.setFileUrl("http://example.com/export/deliver_complaint_list.xlsx");
        when(excelExportUtil.uploadExcelFile(anyString(), anyList(), anyLong(), any()))
                .thenReturn(fileInfoBO);

        // Mock JobHelper
        try (MockedStatic<JobHelper> jobHelperMock = mockStatic(JobHelper.class)) {
            jobHelperMock.when(JobHelper::getJobParam).thenReturn(JSONUtil.toJsonStr(goIn));

            // 执行测试
            assertDoesNotThrow(() -> deliverComplaintListNrJob.deliverComplaintListExportHandler());

            // 验证方法调用
            verify(deliverComplaintService, times(1)).selectListByCondition(any(DeliverComplaintListGoIn.class));
            verify(excelExportUtil, times(1)).uploadExcelFile(anyString(), anyList(), anyLong(), any());
            jobHelperMock.verify(() -> JobHelper.handleSuccess(anyString()), times(1));
        }
    }

    @Test
    @DisplayName("测试导出交付投诉单列�?- 文件上传异常场景")
    void testDeliverComplaintListExportHandler_UploadException() {
        // 准备测试数据
        DeliverComplaintListGoIn goIn = new DeliverComplaintListGoIn();
        goIn.setTraceId("test-trace-id-789");

        // 模拟查询结果
        List<DeliverComplaintListGoOut> goOuts = new ArrayList<>();
        DeliverComplaintListGoOut goOut = new DeliverComplaintListGoOut();
        goOut.setDrNo("DR202401010001");
        goOut.setLastReminderTime(System.currentTimeMillis());
        goOut.setReminderTimes(3);
        goOut.setTradeOrderId("TO123456789");
        goOut.setCarTypeName("小米SU7");
        goOut.setSaleCarVersion("Max�?);
        goOut.setContactName("李四");
        goOut.setContactPhone("13800138000");
        goOut.setProblemCategory("交付问题");
        goOut.setComplaintScene("交付延期");
        goOut.setRiskLevelName("中风�?);
        goOut.setProblemDesc("交付时间延迟");
        goOut.setCreateTime(System.currentTimeMillis());
        goOut.setOrderStatusName("处理�?);
        goOut.setZoneName("华东大区");
        goOut.setLittleZoneName("上海小区");
        goOut.setCityZoneName("上海�?);
        goOut.setPositionAUserName("王五");
        goOut.setPositionBUserName("赵六");
        goOut.setOrgName("上海交付中心");
        goOut.setOperatorPositionName("交付专员");
        goOut.setOperatorName("孙七");
        goOut.setExpectedFirstResponseTime(System.currentTimeMillis());
        goOut.setRealFirstResponseTime(System.currentTimeMillis());
        goOut.setFirstResponseTag(TimeoutOptionEnum.NO.getCode());
        goOut.setLastFollowDesc("已联系客户，正在处理");
        goOut.setExpectedFinishTime(System.currentTimeMillis());
        goOut.setRealFinishTime(System.currentTimeMillis());
        goOut.setFinishTag(TimeoutOptionEnum.YES.getCode());
        goOut.setResponsibleName("有责");
        goOuts.add(goOut);

        // Mock 服务调用
        when(deliverComplaintService.selectListByCondition(any(DeliverComplaintListGoIn.class)))
                .thenReturn(goOuts);

        // Mock 文件上传抛出异常
        when(excelExportUtil.uploadExcelFile(anyString(), anyList(), anyLong(), any()))
                .thenThrow(new RuntimeException("文件上传失败"));

        // Mock JobHelper
        try (MockedStatic<JobHelper> jobHelperMock = mockStatic(JobHelper.class)) {
            jobHelperMock.when(JobHelper::getJobParam).thenReturn(JSONUtil.toJsonStr(goIn));

            // 执行测试
            assertDoesNotThrow(() -> deliverComplaintListNrJob.deliverComplaintListExportHandler());

            // 验证方法调用
            verify(deliverComplaintService, times(1)).selectListByCondition(any(DeliverComplaintListGoIn.class));
            verify(excelExportUtil, times(1)).uploadExcelFile(anyString(), anyList(), anyLong(), any());
            // 验证失败处理被调�?
            jobHelperMock.verify(() -> JobHelper.handleFail(), times(1));
        }
    }

    @Test
    @DisplayName("测试导出交付投诉单列�?- 数据转换异常场景")
    void testDeliverComplaintListExportHandler_DataConversionException() {
        // 准备测试数据
        DeliverComplaintListGoIn goIn = new DeliverComplaintListGoIn();
        goIn.setTraceId("test-trace-id-789");

        // 模拟查询结果包含null值，可能引发数据转换异常
        List<DeliverComplaintListGoOut> goOuts = new ArrayList<>();
        DeliverComplaintListGoOut goOut = new DeliverComplaintListGoOut();
        // 设置一些字段为null，测试空值处�?
        goOut.setDrNo(null);
        goOut.setLastReminderTime(null);
        goOut.setReminderTimes(null);
        goOut.setTradeOrderId("TO123456789");
        goOut.setCarTypeName("小米SU7");
        goOut.setSaleCarVersion("Max�?);
        goOut.setContactName("李四");
        goOut.setContactPhone("13800138000");
        goOut.setProblemCategory("交付问题");
        goOut.setComplaintScene("交付延期");
        goOut.setRiskLevelName("中风�?);
        goOut.setProblemDesc("交付时间延迟");
        goOut.setCreateTime(System.currentTimeMillis());
        goOut.setOrderStatusName("处理�?);
        goOut.setZoneName("华东大区");
        goOut.setLittleZoneName("上海小区");
        goOut.setCityZoneName("上海�?);
        goOut.setPositionAUserName("王五");
        goOut.setPositionBUserName("赵六");
        goOut.setOrgName("上海交付中心");
        goOut.setOperatorPositionName("交付专员");
        goOut.setOperatorName("孙七");
        goOut.setExpectedFirstResponseTime(System.currentTimeMillis());
        goOut.setRealFirstResponseTime(System.currentTimeMillis());
        goOut.setFirstResponseTag(TimeoutOptionEnum.NO.getCode());
        goOut.setLastFollowDesc("已联系客户，正在处理");
        goOut.setExpectedFinishTime(System.currentTimeMillis());
        goOut.setRealFinishTime(System.currentTimeMillis());
        goOut.setFinishTag(TimeoutOptionEnum.YES.getCode());
        goOut.setResponsibleName("有责");
        goOuts.add(goOut);

        // Mock 服务调用
        when(deliverComplaintService.selectListByCondition(any(DeliverComplaintListGoIn.class)))
                .thenReturn(goOuts);

        // Mock 文件上传
        FileInfoBO fileInfoBO = new FileInfoBO();
        fileInfoBO.setFileUrl("http://example.com/export/deliver_complaint_list.xlsx");
        when(excelExportUtil.uploadExcelFile(anyString(), anyList(), anyLong(), any()))
                .thenReturn(fileInfoBO);

        // Mock JobHelper
        try (MockedStatic<JobHelper> jobHelperMock = mockStatic(JobHelper.class)) {
            jobHelperMock.when(JobHelper::getJobParam).thenReturn(JSONUtil.toJsonStr(goIn));

            // 执行测试
            assertDoesNotThrow(() -> deliverComplaintListNrJob.deliverComplaintListExportHandler());

            // 验证方法调用
            verify(deliverComplaintService, times(1)).selectListByCondition(any(DeliverComplaintListGoIn.class));
            verify(excelExportUtil, times(1)).uploadExcelFile(anyString(), anyList(), anyLong(), any());
            jobHelperMock.verify(() -> JobHelper.handleSuccess(anyString()), times(1));
        }
    }
}