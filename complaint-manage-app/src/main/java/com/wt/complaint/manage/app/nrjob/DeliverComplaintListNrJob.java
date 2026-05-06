package com.wt.complaint.manage.app.nrjob;

import cn.hutool.json.JSONUtil;
import com.wt.car.common.watermark.util.ExcelWatermarkUtil;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.api.model.enums.TimeoutOptionEnum;
import com.wt.complaint.manage.app.util.ExcelExportUtil;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintService;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.bo.FileInfoBO;
import com.wt.complaint.manage.domain.model.JobSuccessDto;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.nr.job.core.context.JobHelper;
import com.xiaomi.nr.job.core.handler.annotation.NrJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeliverComplaintListNrJob {

    public static final String TRACE_ID_NAME = "tid";

    public static final String TRACE_ID_ATTACHMENT = "_trace_id_";

    @Resource
    private ExcelExportUtil excelExportUtil;
    @Resource
    private DeliverComplaintService deliverComplaintService;

    @Value("${temp.directory}")
    private String tempDirectory;

    @Value("${job.upc.delivery.project.id}")
    private Long jobProjectId;


    @NrJob("deliverComplaintListExportHandler")
    public void deliverComplaintListExportHandler() {
        JobHelper.log("deliverComplaintListExportHandler begin execute");
        long startTime = System.currentTimeMillis(); // 记录开始时�?

        String params = JobHelper.getJobParam();
        JobHelper.log("deliverComplaintListExportHandler: JobHelper.getJobParam请求参数-{}", params);
        DeliverComplaintListGoIn goIn = JSONUtil.toBean(params, DeliverComplaintListGoIn.class);

        RpcContext.getContext().setAttachment(TRACE_ID_ATTACHMENT, goIn.getTraceId());
        MDC.put(TRACE_ID_NAME, goIn.getTraceId());
        log.info("deliverComplaintListExportHandler: JobHelper.getJobParam请求参数-{}", params);
        try {
            // 组装数据
            List<List<String>> excelData = new ArrayList<>();
            List<String> exportHead = getExportHead();
            List<List<String>> exportBody = getExportBody(goIn);
            excelData.add(exportHead);
            excelData.addAll(exportBody);

            String fileName = "交付投诉单列表导出_" + DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis(), DateUtil.DATE_FORMAT_STR);
            String filePath = tempDirectory + fileName + ".xlsx";
            FileInfoBO fileInfo = excelExportUtil.uploadExcelFile(filePath, excelData, jobProjectId, path ->
                    ExcelWatermarkUtil.addWatermark(filePath, "交付投诉单列表导出_", "售后工作�?交付投诉单列表导�?));
            JobHelper.log("deliverComplaintListExportHandler end execute: fileInfo");
            JobSuccessDto dto = JobSuccessDto.builder().fileUrl(fileInfo.getFileUrl()).build();
            JobHelper.handleSuccess(GsonUtil.toJson(dto));

            JobHelper.log("deliverComplaintListExportHandler cost: {} ms", (System.currentTimeMillis() - startTime)); // 记录耗时
            JobHelper.log("deliverComplaintListExportHandler end execute");
        } catch (Exception e) {
            log.error("导出失败", e);
            JobHelper.handleFail();
        } finally {
            long endTime = System.currentTimeMillis(); // 记录结束时间
            log.info("导出交付投诉单列表耗时: {} ms", (endTime - startTime)); // 记录耗时
            RpcContext.getContext().removeAttachment(DubboConstant.TRACE_ID_ATTACHMENT);
            MDC.remove(TRACE_ID_NAME);
        }
    }

    /**
     * 获取导出表头
     */
    private List<String> getExportHead() {
        List<String> head = new ArrayList<>();
        head.add("投诉单号");
        head.add("最近催单时�?);
        head.add("累计催单次数");
        head.add("订单�?);
        head.add("车型");
        head.add("车型版本");
        head.add("联系�?);
        head.add("联系电话");
        head.add("问题分类");
        head.add("投诉场景");
        head.add("风险等级");
        head.add("投诉详情");
        head.add("投诉单创建时�?);
        head.add("投诉单状�?);
        head.add("交付大区");
        head.add("交付小区");
        head.add("交付城市");
        head.add("交付邀约专�?);
        head.add("交付接待专员");
        head.add("跟进门店");
        head.add("跟进岗位");
        head.add("跟进人员");
        head.add("首响截止时间");
        head.add("实际首响时间");
        head.add("首响超时状�?);
        head.add("最新工单跟进描�?);
        head.add("结案截止时间");
        head.add("实际结案时间");
        head.add("结案超时状�?);
        head.add("责任情况");
        return head;
    }

    /**
     * 获取导出数据
     */
    private List<List<String>> getExportBody(DeliverComplaintListGoIn goIn) {
        List<List<String>> bodyList = new ArrayList<>();
        List<DeliverComplaintListGoOut> goOuts = deliverComplaintService.selectListByCondition(goIn);
        for (DeliverComplaintListGoOut goOut : goOuts) {
            List<String> row = new ArrayList<>();
            // 投诉单号
            row.add(goOut.getDrNo());
            // 最近催单时�?
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getLastReminderTime()));
            // 累计催单次数
            row.add(String.valueOf(goOut.getReminderTimes()));
            // 订单�?
            row.add(goOut.getTradeOrderId());
            // 车型
            row.add(goOut.getCarTypeName());
            // 车型版本
            row.add(goOut.getSaleCarVersion());
            // 联系�?
            row.add(goOut.getContactName());
            // 联系电话
            row.add(goOut.getContactPhone());
            // 问题分类
            row.add(goOut.getProblemCategory());
            // 投诉场景
            row.add(goOut.getComplaintScene());
            // 风险等级
            row.add(goOut.getRiskLevelName());
            // 投诉详情
            row.add(goOut.getProblemDesc());
            // 投诉单创建时�?
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getCreateTime()));
            // 投诉单状�?
            row.add(goOut.getOrderStatusName());
            // 交付大区
            row.add(goOut.getZoneName());
            // 交付小区
            row.add(goOut.getLittleZoneName());
            // 交付城市
            row.add(goOut.getCityZoneName());
            // 交付邀约专�?
            row.add(goOut.getPositionAUserName());
            // 交付接待专员
            row.add(goOut.getPositionBUserName());
            // 跟进门店
            row.add(goOut.getOrgName());
            // 跟进岗位
            row.add(goOut.getOperatorPositionName());
            // 跟进人员
            row.add(goOut.getOperatorName());
            // 首响截止时间
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getExpectedFirstResponseTime()));
            // 实际首响时间
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getRealFirstResponseTime()));
            // 首响超时状�?
            row.add(TimeoutOptionEnum.getDescByCode(goOut.getFirstResponseTag()));
            // 最新工单跟进描�?
            row.add(goOut.getLastFollowDesc());
            // 结案截止时间
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getExpectedFinishTime()));
            // 实际结案时间
            row.add(DateUtil.getTimeStrByTimeStampMS(goOut.getRealFinishTime()));
            // 结案超时状�?
            row.add(TimeoutOptionEnum.getDescByCode(goOut.getFinishTag()));
            // 责任情况
            row.add(goOut.getResponsibleName());
            bodyList.add(row);
        }
        return bodyList;
    }

}
