package com.wt.complaint.manage.app.util;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.bo.FileInfoBO;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.param.PicCommitParam;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.BUS_ERROR;

/**
 * @author huxiankang
 * @date 2025/6/16
 */
@Component
@Slf4j
public class ExcelExportUtil {
    @Resource
    private FileRemoteGateway fileGateway;

    /**
     * @param filePath 文件路径
     * @param excelData excel数据(第一行是表头)
     * @param jobProjectId
     * @return
     */
    @NotNull
    public FileInfoBO uploadExcelFile(String filePath, List<List<String>> excelData, Long jobProjectId, Function<String, String> watermarkFunction) {
        if (excelData == null || excelData.isEmpty()) {
            throw new BusinessException(BUS_ERROR, "excelData is empty");
        }
        List<String> exportHead = excelData.get(0);
        ExcelWriter writer = ExcelUtil.getWriter(filePath);
        try {
            // 获取Sheet对象并设置列�?
            Sheet sheet = writer.getSheet();
            int columnWidths = 22; // 每列字符宽度（按需定义�?
            for (int i = 0; i < excelData.get(0).size(); i++) {
                sheet.setColumnWidth(i, columnWidths * 256); // 转换为POI单位
            }
            StyleSet styleSet = writer.getStyleSet();
            DataFormat dataFormat = writer.getWorkbook().createDataFormat();
            // 创建表头样式
            CellStyle headerCellStyle = writer.getWorkbook().createCellStyle();
            // 复制基本样式
            headerCellStyle.cloneStyleFrom(styleSet.getCellStyle());
            // 设置灰色背景
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setDataFormat(dataFormat.getFormat("@"));
            headerCellStyle.setWrapText(true);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置正文样式
            CellStyle bodyCellStyle = styleSet.getCellStyle();
            bodyCellStyle.setDataFormat(dataFormat.getFormat("@"));
            bodyCellStyle.setWrapText(true);
            bodyCellStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置Sheet名称
            writer.renameSheet("投诉单列�?);
            // 写入数据
            writer.write(excelData, true);
            // 单独设置表头样式
            int headerRowIndex = 0; // 第一行是表头
            int columnCount = exportHead.size();
            for (int i = 0; i < columnCount; i++) {
                writer.getSheet().getRow(headerRowIndex).getCell(i).setCellStyle(headerCellStyle);
            }
        } catch (Exception e) {
            log.error("excel export error", e);
            throw new BusinessException(BUS_ERROR, "excel export error", e);
        } finally {
            //关闭writer，释放内�?
            writer.close();
        }
        //上传文件
        FileSystemResource fileSystemResource = new FileSystemResource(watermarkFunction.apply(filePath));
        FileInfoBO fileInfo = fileGateway.uploadPublicFile(fileSystemResource.getPath());
        log.info("export file:{}", GsonUtil.toJson(fileInfo));
        PicCommitParam commitParam = new PicCommitParam();
        commitParam.setProjectId(jobProjectId);
        commitParam.setIds(Collections.singletonList(fileInfo.getFileId()));
        fileGateway.fileCommit(commitParam);
        return fileInfo;
    }
}
