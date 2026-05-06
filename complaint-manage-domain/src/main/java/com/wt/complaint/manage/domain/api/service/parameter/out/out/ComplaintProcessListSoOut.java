package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.util.ObjectUtil;
import com.wt.complaint.manage.api.model.enums.JudgeTypeEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.nr.common.utils.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintProcessListSoOut {
    private List<ComplaintProcessSoOut> processList;

    public void fillProcessList(List<ComplaintFollowProcessGoOut> followProcessGoOuts, Map<Long, FileInfoGoOut> recordAttachmentsMap) {
        List<ComplaintProcessSoOut> tempProcessList = new ArrayList<>();
        this.processList = tempProcessList;
        
        for (ComplaintFollowProcessGoOut followProcessGoOut : followProcessGoOuts) {
            RecordInfoGoIn recordInfoGoIn = parseRecordInfo(followProcessGoOut);
            
            // 如果解析失败或内容为空，创建基础记录
            if (recordInfoGoIn == null) {
                tempProcessList.add(buildBasicProcess(followProcessGoOut));
            } else {
                // 不需要过滤，处理完整记录
                fillAttachmentsAndJudgeResult(recordInfoGoIn, recordAttachmentsMap);
                
                // 转换并调整审核人名称
                RecordInfoSoOut infoSoOut = OrderViewConverter.INSTANCE.toRecordInfoSoOut(recordInfoGoIn);
                adjustAuditName(followProcessGoOut.getProcessType(), recordInfoGoIn, infoSoOut);
                
                // 构建完整记录
                tempProcessList.add(buildFullProcess(followProcessGoOut, infoSoOut));
            }
        }
    }

    /**
     * 解析跟进记录内容
     */
    private RecordInfoGoIn parseRecordInfo(ComplaintFollowProcessGoOut followProcessGoOut) {
        if (StringUtils.isEmpty(followProcessGoOut.getProcessContent())) {
            return null;
        }
        return GsonUtil.fromJson(followProcessGoOut.getProcessContent(), RecordInfoGoIn.class);
    }

    /**
     * 构建基础流程记录（无详细信息�?
     */
    private ComplaintProcessSoOut buildBasicProcess(ComplaintFollowProcessGoOut followProcessGoOut) {
        return ComplaintProcessSoOut.builder()
                .processId(followProcessGoOut.getId())
                .processType(followProcessGoOut.getProcessType())
                .complaintNo(followProcessGoOut.getComplaintNo())
                .createTime(followProcessGoOut.getCreateTime())
                .build();
    }

    /**
     * 填充附件信息和判责结果描�?
     */
    private void fillAttachmentsAndJudgeResult(RecordInfoGoIn recordInfoGoIn, Map<Long, FileInfoGoOut> recordAttachmentsMap) {
        List<AttachmentGoIn> attachments = recordInfoGoIn.getAttachments();
        Optional.ofNullable(attachments).orElse(new ArrayList<>()).forEach((AttachmentGoIn e) -> {
            if (recordAttachmentsMap.containsKey(e.getId())) {
                FileInfoGoOut fileInfo = recordAttachmentsMap.get(e.getId());
                e.setFileName(fileInfo.getFileName());
                e.setUrl(fileInfo.getFileUrl());
            } else {
                e.setFileName("");
                e.setUrl("");
            }
        });
        recordInfoGoIn.setAttachments(attachments);
        
        if (ObjectUtil.isNotNull(recordInfoGoIn.getJudgeResult())) {
            recordInfoGoIn.setJudgeResultDesc(JudgeTypeEnum.getDescByCode(recordInfoGoIn.getJudgeResult()));
        }
    }

    /**
     * 调整审核人名称显�?
     */
    private void adjustAuditName(String processType, RecordInfoGoIn recordInfoGoIn, RecordInfoSoOut infoSoOut) {
        // 申请免责-驳回（含分审级）：当审核人为服务满意度管理岗位时，展示为中台判责小组
        if (ProcessTypeEnum.isExemptionRejectProcessCode(processType)
                && PushConstant.POSITION_SERVICE_SATISFACTION_MANAGEMENT.equals(recordInfoGoIn.getOperatePositionId())) {
            infoSoOut.setAuditName(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP);
        }
        
        // 服务投诉判责：判责人默认展示为中台判责小�?
        if (ProcessTypeEnum.COMPLAINT_ADJUDICATION.getProcessCode().equals(processType)
                && StringUtils.isEmpty(infoSoOut.getAuditName())) {
            infoSoOut.setAuditName(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP);
        }
    }

    /**
     * 构建完整流程记录（包含详细信息）
     */
    private ComplaintProcessSoOut buildFullProcess(ComplaintFollowProcessGoOut followProcessGoOut, RecordInfoSoOut infoSoOut) {
        return ComplaintProcessSoOut.builder()
                .processId(followProcessGoOut.getId())
                .processType(followProcessGoOut.getProcessType())
                .complaintNo(followProcessGoOut.getComplaintNo())
                .createTime(followProcessGoOut.getCreateTime())
                .info(infoSoOut)
                .build();
    }
}
