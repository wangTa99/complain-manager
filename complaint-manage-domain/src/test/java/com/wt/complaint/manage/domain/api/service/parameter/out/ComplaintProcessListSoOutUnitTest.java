package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.enums.JudgeTypeEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.nr.common.utils.GsonUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComplaintProcessListSoOut#fillProcessList 单测
 * 覆盖：空内容基础记录、历史申请免责通过过滤、新分审级通过不过滤、审核人名称展示、附件与判责结果填充
 */
class ComplaintProcessListSoOutUnitTest {

    @Test
    void fillProcessList_emptyList_processListEmpty() {
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.emptyList(), Collections.emptyMap());
        assertNotNull(soOut.getProcessList());
        assertTrue(soOut.getProcessList().isEmpty());
    }

    @Test
    void fillProcessList_emptyProcessContent_basicProcessNoInfo() {
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(1L)
                .complaintNo("C001")
                .processType(ProcessTypeEnum.APPLY_FINISH.getProcessCode())
                .processContent("")
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        ComplaintProcessSoOut process = soOut.getProcessList().get(0);
        assertEquals(1L, process.getProcessId());
        assertEquals("C001", process.getComplaintNo());
        assertEquals(ProcessTypeEnum.APPLY_FINISH.getProcessCode(), process.getProcessType());
        assertNull(process.getInfo());
    }

    @Test
    void fillProcessList_validContentNonExemption_fullProcessWithInfo() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .auditName("张三")
                .auditResult("审核通过")
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(2L)
                .complaintNo("C002")
                .processType(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        ComplaintProcessSoOut process = soOut.getProcessList().get(0);
        assertNotNull(process.getInfo());
        assertEquals("张三", process.getInfo().getAuditName());
    }

    /**
     * 申请免责-审核通过，操作人岗位为服务满意度管理(174)时保留（三审通过�?
     */
    @Test
    void fillProcessList_auditExemptionPass_position174_notFiltered() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .operatePositionId(PushConstant.POSITION_SERVICE_SATISFACTION_MANAGEMENT)
                .auditName("中台")
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(3L)
                .complaintNo("C003")
                .processType(ProcessTypeEnum.AUDIT_EXEMPTION_PASS.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertNotNull(soOut.getProcessList().get(0).getInfo());
    }

    /**
     * 申请免责-审核通过（旧枚举 AUDIT_EXEMPTION_PASS），操作人岗位非174时不再过滤，全量展示
     * （fillProcessList 重构后移除了按岗位过滤逻辑，所有跟进记录均正常展示�?
     */
    @Test
    void fillProcessList_auditExemptionPass_otherPosition_notFiltered() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .operatePositionId("100")
                .auditName("一�?)
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(4L)
                .complaintNo("C004")
                .processType(ProcessTypeEnum.AUDIT_EXEMPTION_PASS.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertNotNull(soOut.getProcessList().get(0).getInfo());
        assertEquals("一�?, soOut.getProcessList().get(0).getInfo().getAuditName());
    }

    /**
     * 新枚举「申请免�?一审通过」：�?174 岗位也不过滤，跟进记录全量展�?
     */
    @Test
    void fillProcessList_auditExemptionFirstPass_non174_notFiltered() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .operatePositionId("100")
                .auditName("一�?)
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(41L)
                .complaintNo("C041")
                .processType(ProcessTypeEnum.AUDIT_EXEMPTION_FIRST_PASS.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertEquals("一�?, soOut.getProcessList().get(0).getInfo().getAuditName());
    }

    /**
     * 申请免责-审核驳回，岗�?74时审核人展示为中台判责小�?
     */
    @Test
    void fillProcessList_auditExemptionReject_position174_auditNameDisplayAsCenterGroup() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .operatePositionId(PushConstant.POSITION_SERVICE_SATISFACTION_MANAGEMENT)
                .auditName("原审核人")
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(5L)
                .complaintNo("C005")
                .processType(ProcessTypeEnum.AUDIT_EXEMPTION_REJECT.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertEquals(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP, soOut.getProcessList().get(0).getInfo().getAuditName());
    }

    /**
     * 申请免责-三审驳回，岗�?74时审核人展示为中台判责小�?
     */
    @Test
    void fillProcessList_auditExemptionThirdReject_position174_auditNameDisplayAsCenterGroup() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .operatePositionId(PushConstant.POSITION_SERVICE_SATISFACTION_MANAGEMENT)
                .auditName("原审核人")
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(51L)
                .complaintNo("C051")
                .processType(ProcessTypeEnum.AUDIT_EXEMPTION_THIRD_REJECT.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertEquals(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP, soOut.getProcessList().get(0).getInfo().getAuditName());
    }

    /**
     * 服务投诉判责，审核人为空时默认展示为中台判责小组
     */
    @Test
    void fillProcessList_complaintAdjudication_emptyAuditName_displayAsCenterGroup() {
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .auditName("")
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(6L)
                .complaintNo("C006")
                .processType(ProcessTypeEnum.COMPLAINT_ADJUDICATION.getProcessCode())
                .processContent(content)
                .createTime(new Date())
                .build();
        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), Collections.emptyMap());

        assertEquals(1, soOut.getProcessList().size());
        assertEquals(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP, soOut.getProcessList().get(0).getInfo().getAuditName());
    }

    @Test
    void fillProcessList_attachmentsAndJudgeResult_filled() {
        AttachmentGoIn att = new AttachmentGoIn();
        att.setId(100L);
        att.setFileName("");
        att.setUrl("");
        RecordInfoGoIn recordInfo = RecordInfoGoIn.builder()
                .auditName("李四")
                .judgeResult(JudgeTypeEnum.JUDGE_VALID.getCode())
                .attachments(Collections.singletonList(att))
                .build();
        String content = GsonUtil.toJson(recordInfo);
        ComplaintFollowProcessGoOut follow = ComplaintFollowProcessGoOut.builder()
                .id(7L)
                .complaintNo("C007")
                .processType("REPORT_JUDGE")
                .processContent(content)
                .createTime(new Date())
                .build();
        Map<Long, FileInfoGoOut> attachmentMap = new HashMap<>();
        attachmentMap.put(100L, FileInfoGoOut.builder().fileId(100L).fileName("a.pdf").fileUrl("http://u").build());

        ComplaintProcessListSoOut soOut = ComplaintProcessListSoOut.builder().build();
        soOut.fillProcessList(Collections.singletonList(follow), attachmentMap);

        assertEquals(1, soOut.getProcessList().size());
        RecordInfoSoOut info = soOut.getProcessList().get(0).getInfo();
        assertNotNull(info);
        assertNotNull(info.getJudgeResultDesc());
        assertNotNull(info.getAttachments());
        assertEquals(1, info.getAttachments().size());
        assertEquals("a.pdf", info.getAttachments().get(0).getFileName());
        assertEquals("http://u", info.getAttachments().get(0).getUrl());
    }
}
