package com.wt.complaint.manage.domain.utils;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.domain.api.enums.FieldTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.bo.BpmHtmlBo;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.xiaomi.newretail.bpm.api.model.dto.ProcessCurrentTaskResponseDTO;
import com.xiaomi.newretail.bpm.api.model.dto.TaskAuditDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComplaintApplyUtil 单元测试
 *
 * @author zhangzheyang
 */
class ComplaintApplyUtilUnitTest {

    // ============ getFileIdFromStruct ============

    @Test
    void getFileIdFromStruct_emptyList_returnsEmpty() {
        List<TemplateStructSoIn> empty = new ArrayList<>();
        List<Long> result = ComplaintApplyUtil.getFileIdFromStruct(empty);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFileIdFromStruct_nullList_returnsEmpty() {
        List<Long> result = ComplaintApplyUtil.getFileIdFromStruct(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFileIdFromStruct_withAttachments_returnsFileIds() {
        AttachmentSoIn a1 = AttachmentSoIn.builder().id(101L).fileName("a.pdf").build();
        AttachmentSoIn a2 = AttachmentSoIn.builder().id(102L).fileName("b.jpg").build();
        TemplateFieldSoIn fieldWithAttach = TemplateFieldSoIn.builder()
                .fieldCode("att")
                .fieldType(FieldTypeEnum.ATTACHMENT.getCode())
                .attachmentList(Lists.newArrayList(a1, a2))
                .value(new ArrayList<>())
                .build();
        TemplateFieldSoIn fieldNoAttach = TemplateFieldSoIn.builder()
                .fieldCode("text")
                .attachmentList(null)
                .value(new ArrayList<>())
                .build();
        TemplateStructSoIn struct = TemplateStructSoIn.builder()
                .fields(Lists.newArrayList(fieldWithAttach, fieldNoAttach))
                .build();
        List<Long> result = ComplaintApplyUtil.getFileIdFromStruct(Lists.newArrayList(struct));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(101L));
        assertTrue(result.contains(102L));
    }

    // ============ buildHtmlBo ============

    @Test
    void buildHtmlBo_normalOrderInfo_withoutAttachments() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setOrgId("F001");
        orderInfo.setOrgName("测试门店");
        orderInfo.setComplaintNo("C001");
        orderInfo.setProblemCategory("问题分类");
        orderInfo.setRiskLevel(1);
        orderInfo.setProblemDesc("问题详情");
        orderInfo.setComplaintContent("场景");
        orderInfo.setAttachments(null);
        ComplaintApplySoIn soIn = new ComplaintApplySoIn();
        soIn.setApplyReason("申请原因示例");

        BpmHtmlBo result = ComplaintApplyUtil.buildHtmlBo(orderInfo, soIn);
        assertNotNull(result);
        assertEquals("table", result.getType());
        assertEquals("申请免责审批", result.getTableName());
        assertNotNull(result.getData());
        assertEquals(8, result.getData().size());
        assertTrue(result.getControlGroup().isEmpty());
    }

    @Test
    void buildHtmlBo_withAttachments_includesControlGroup() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setOrgId("F001");
        orderInfo.setOrgName("测试门店");
        orderInfo.setComplaintNo("C001");
        orderInfo.setProblemCategory("问题分类");
        orderInfo.setRiskLevel(2);
        orderInfo.setProblemDesc("问题详情");
        orderInfo.setComplaintContent("场景");
        com.wt.complaint.manage.api.model.Attachment att = new com.wt.complaint.manage.api.model.Attachment();
        att.setId(1L);
        att.setFileName("file.pdf");
        att.setUrl("http://example.com/file.pdf");
        att.setType(2);
        orderInfo.setAttachments(Lists.newArrayList(att));

        ComplaintApplySoIn soIn = new ComplaintApplySoIn();
        soIn.setApplyReason("申请原因示例");
        BpmHtmlBo result = ComplaintApplyUtil.buildHtmlBo(orderInfo, soIn);
        assertNotNull(result);
        assertFalse(result.getControlGroup().isEmpty());
        assertTrue(result.getControlGroup().stream().anyMatch(g -> "file".equals(g.getType())));
    }

    // ============ buildContentBo ============

    @Test
    void buildContentBo_normalOrderInfo() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setOrgId("F001");
        orderInfo.setOrgName("测试门店");
        orderInfo.setComplaintNo("C001");
        orderInfo.setProblemCategory("问题分类");
        orderInfo.setRiskLevel(1);
        orderInfo.setProblemDesc("问题详情");
        orderInfo.setComplaintContent("场景");
        ComplaintApplySoIn soIn = new ComplaintApplySoIn();
        soIn.setApplyReason("申请原因示例");

        BpmContentBo result = ComplaintApplyUtil.buildContentBo(orderInfo, soIn);
        assertNotNull(result);
        assertNotNull(result.getBlocks());
        assertEquals(1, result.getBlocks().size());
        assertNotNull(result.getBlocks().get(0).getEntities());
        assertEquals(8, result.getBlocks().get(0).getEntities().size());
    }

    // ============ createComplaintAdjudicationApply ============

    @Test
    void createComplaintAdjudicationApply_withStoreName_returnsCorrectAuditGoIn() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setComplaintNo("C001");
        orderInfo.setVid("V001");
        orderInfo.setOrgId("F001");
        orderInfo.setZoneId("1");
        orderInfo.setLittleZoneId("10");
        orderInfo.setCreateMid(1001L);

        ComplaintAuditGoIn result = ComplaintApplyUtil.createComplaintAdjudicationApply(orderInfo, "测试门店");
        assertNotNull(result);
        assertEquals("C001", result.getComplaintNo());
        assertEquals("F001", result.getOrgId());
        assertEquals("测试门店", result.getOrgName());
        assertEquals(com.wt.complaint.manage.api.model.enums.AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode(), result.getAuditType());
        assertEquals(AuditStatusEnum.PENDING.getCode(), result.getAuditStatus());
        assertEquals("V001", result.getVid());
        assertEquals("1", result.getZoneId());
        assertEquals("10", result.getLittleZoneId());
        assertEquals(1001L, result.getCreateMid());
    }

    @Test
    void createComplaintAdjudicationApply_emptyStoreName_orgNameIsEmpty() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setComplaintNo("C002");
        orderInfo.setOrgId("F002");
        orderInfo.setCreateMid(1002L);

        ComplaintAuditGoIn result = ComplaintApplyUtil.createComplaintAdjudicationApply(orderInfo, "");
        assertNotNull(result);
        assertEquals("C002", result.getComplaintNo());
        assertEquals("F002", result.getOrgId());
        assertEquals("", result.getOrgName());
    }

    // ============ buildBpmTaskAuditDTO ============

    @Test
    void buildBpmTaskAuditDTO_approved_currentNode1_hasLittleZoneIdInExtra() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setAuditStatus(AuditStatusEnum.APPROVED.getCode());
        ProcessCurrentTaskResponseDTO.TaskInfo taskInfo = new ProcessCurrentTaskResponseDTO.TaskInfo();
        taskInfo.setTaskId("task-1");
        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setComplaintNo("C001");
        auditSoOut.setCurrentNode(1);
        auditSoOut.setLittleZoneId("10");
        auditSoOut.setZoneId("1");

        TaskAuditDTO result = ComplaintApplyUtil.buildBpmTaskAuditDTO(req, taskInfo, "user@xiaomi.com", auditSoOut);
        assertNotNull(result);
        assertEquals("task-1", result.getTaskId());
        assertEquals("user", result.getUser());
        assertEquals(com.xiaomi.newretail.bpm.api.model.dto.AuditOperate.Accept, result.getOperate());
        assertNotNull(result.getExtra());
        assertEquals("C001", result.getExtra().get(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY));
        assertEquals("10", result.getExtra().get(ComplaintInfoConstant.BPM_LITTLE_ZONE_ID_KEY));
    }

    @Test
    void buildBpmTaskAuditDTO_rejected_currentNode2_hasBigZoneIdInExtra() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setAuditStatus(AuditStatusEnum.REJECTED.getCode());
        ProcessCurrentTaskResponseDTO.TaskInfo taskInfo = new ProcessCurrentTaskResponseDTO.TaskInfo();
        taskInfo.setTaskId("task-2");
        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setComplaintNo("C002");
        auditSoOut.setCurrentNode(2);
        auditSoOut.setZoneId("2");

        TaskAuditDTO result = ComplaintApplyUtil.buildBpmTaskAuditDTO(req, taskInfo, "admin@xiaomi.com", auditSoOut);
        assertNotNull(result);
        assertEquals(com.xiaomi.newretail.bpm.api.model.dto.AuditOperate.Refuse, result.getOperate());
        assertEquals("2", result.getExtra().get(ComplaintInfoConstant.BPM_BIG_ZONE_ID_KEY));
    }

    @Test
    void buildBpmTaskAuditDTO_invalidAuditStatus_throws() {
        SubmitForApprovalSoIn req = new SubmitForApprovalSoIn();
        req.setAuditStatus(99);
        ProcessCurrentTaskResponseDTO.TaskInfo taskInfo = new ProcessCurrentTaskResponseDTO.TaskInfo();
        taskInfo.setTaskId("t");
        ComplaintAuditSoOut auditSoOut = new ComplaintAuditSoOut();
        auditSoOut.setComplaintNo("C001");
        auditSoOut.setCurrentNode(1);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                ComplaintApplyUtil.buildBpmTaskAuditDTO(req, taskInfo, "u@xiaomi.com", auditSoOut));
        assertTrue(ex.getMessage().contains("auditStatus only support 2 or 3"));
    }

    // ============ extractExpandInfo ============

    @Test
    void extractExpandInfo_complaintType_setsComplaintType() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setComplaintNo("C001");
        FieldValueSoIn value = FieldValueSoIn.builder().code("2").desc("服务投诉").build();
        TemplateFieldSoIn field = TemplateFieldSoIn.builder()
                .fieldCode(ComplaintInfoConstant.COMPLAINT_TYPE)
                .fieldType(FieldTypeEnum.OPTION.getCode())
                .value(Lists.newArrayList(value))
                .build();
        TemplateStructSoIn struct = TemplateStructSoIn.builder()
                .fields(Lists.newArrayList(field))
                .build();

        ComplaintOrderInfoGoIn result = ComplaintApplyUtil.extractExpandInfo(orderInfo, Lists.newArrayList(struct));
        assertNotNull(result);
        assertEquals(2, result.getComplaintType());
    }

    @Test
    void extractExpandInfo_complaintType_nullValueCode_throws() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        TemplateFieldSoIn field = TemplateFieldSoIn.builder()
                .fieldCode(ComplaintInfoConstant.COMPLAINT_TYPE)
                .fieldType(FieldTypeEnum.OPTION.getCode())
                .value(new ArrayList<>())
                .build();
        TemplateStructSoIn struct = TemplateStructSoIn.builder()
                .fields(Lists.newArrayList(field))
                .build();

        List<TemplateStructSoIn> structs = Lists.newArrayList(struct);
        assertThrows(BusinessException.class, () ->
                ComplaintApplyUtil.extractExpandInfo(orderInfo, structs));
    }

    @Test
    void extractExpandInfo_orgId_setsOrgIdAndOrgName() {
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        orderInfo.setComplaintNo("C001");
        FieldValueSoIn value = FieldValueSoIn.builder().code("F001").desc("测试门店").build();
        TemplateFieldSoIn field = TemplateFieldSoIn.builder()
                .fieldCode(ComplaintInfoConstant.ORG_ID)
                .fieldType(FieldTypeEnum.OPTION.getCode())
                .value(Lists.newArrayList(value))
                .build();
        TemplateStructSoIn struct = TemplateStructSoIn.builder()
                .fields(Lists.newArrayList(field))
                .build();

        ComplaintOrderInfoGoIn result = ComplaintApplyUtil.extractExpandInfo(orderInfo, Lists.newArrayList(struct));
        assertNotNull(result);
        assertEquals("F001", result.getOrgId());
        assertEquals("测试门店", result.getOrgName());
    }
}
