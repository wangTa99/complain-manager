package com.wt.complaint.manage.domain.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PropertyEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.bo.BpmHtmlBo;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.newretail.bpm.api.model.dto.AuditOperate;
import com.xiaomi.newretail.bpm.api.model.dto.ProcessCurrentTaskResponseDTO;
import com.xiaomi.newretail.bpm.api.model.dto.TaskAuditDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ComplaintApplyUtil {

    public static ComplaintOrderInfoGoIn parseComplaintContent(ComplaintOrderInfoGoIn orderInfo) {
        // 查询投诉内容
        String complaintContent = orderInfo.getComplaintContent();
        List<TemplateStructSoIn> complaintStructList = new ArrayList<>();
        if (StringUtils.isNotBlank(complaintContent)) {
            complaintStructList = GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {
            }.getType());
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = extractExpandInfo(orderInfo, complaintStructList);
        complaintOrderInfoGoIn.setComplaintContent(ParseComplaintContentUtil.parseComplaintScene(complaintContent));
        return complaintOrderInfoGoIn;
    }

    public static ComplaintOrderInfoGoIn extractExpandInfo(ComplaintOrderInfoGoIn orderInfo, List<TemplateStructSoIn> complaintInfo) {
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = new ComplaintOrderInfoGoIn();
        BeanUtil.copyProperties(orderInfo, complaintOrderInfoGoIn);
        for (TemplateStructSoIn templateStructSoIn : complaintInfo) {
            for (TemplateFieldSoIn field : templateStructSoIn.getFields()) {
                switch (field.getFieldCode()) {
                    case ComplaintInfoConstant.COMPLAINT_TYPE:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            Integer complaintType = Integer.valueOf(valueCode);
                            complaintOrderInfoGoIn.setComplaintType(complaintType);
                        } else {
                            log.error("complaintType is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "complaintType is null");
                        }
                        break;
                    case ComplaintInfoConstant.ORG_ID:
                        if (Objects.nonNull(field.getValueCode())) {
                            String orgId = field.getValue().get(0).getCode();
                            complaintOrderInfoGoIn.setOrgId(orgId);
                            complaintOrderInfoGoIn.setOrgName(field.getValue().get(0).getDesc());
                        } else {
                            log.error("orgId is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "orgId is null");
                        }
                        break;
                    case ComplaintInfoConstant.RISK_LEVEL:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            Integer riskLevel = Integer.valueOf(valueCode);
                            complaintOrderInfoGoIn.setRiskLevel(riskLevel);
                        } else {
                            log.error("riskLevel is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "riskLevel is null");
                        }
                        break;
                    case ComplaintInfoConstant.RESPONSIBILITY:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            Integer responsibility = Integer.valueOf(valueCode);
                            complaintOrderInfoGoIn.setResponsibility(responsibility);
                        } else {
                            log.error("responsibility is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "responsibility is null");
                        }
                        break;
                    case ComplaintInfoConstant.PROBLEM_CATEGORY:
                        if (Objects.nonNull(field.getValueCode())) {
                            String pathId = field.getValue().get(0).getPathId();
                            String pathName = field.getValue().get(0).getPathName();
                            complaintOrderInfoGoIn.setProblemCategory(pathName);
                        } else {
                            log.error("problemCategory is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "problemCategory is null");
                        }
                        break;
                    case ComplaintInfoConstant.ORG_FOLLOW_TAG:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            Integer orgFollowTag = Integer.valueOf(valueCode);
                            complaintOrderInfoGoIn.setOnlyView(orgFollowTag == 1 ? 0 : 1);
                        } else {
                            log.error("onlyView is null, complaintInfo:{}", GsonUtil.toJson(complaintInfo));
                            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "onlyView is null");
                        }
                        break;
                    case ComplaintInfoConstant.USER_DEMAND:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            complaintOrderInfoGoIn.setUserDemand(valueCode);
                        }
                        break;
                    case ComplaintInfoConstant.PROBLEM_DESC:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            complaintOrderInfoGoIn.setProblemDesc(valueCode);
                        }
                        break;
                    case ComplaintInfoConstant.MEDIA_INVOLVED:
                        if (Objects.nonNull(field.getValueCode())) {
                            String valueCode = (String) field.getValueCode();
                            Integer mediaInvolved = Integer.valueOf(valueCode);
                            complaintOrderInfoGoIn.setMediaInvolved(mediaInvolved);
                        }
                        break;
                    case ComplaintInfoConstant.MEDIA_LINK:
                        if (CollUtil.isNotEmpty(field.getValue())) {
                            String link = field.getValue().get(0).getDesc();
                            complaintOrderInfoGoIn.setMediaLink(link);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return complaintOrderInfoGoIn;
    }

    /**
     * 获取举报信息中的文件id
     *
     * @param complaintStructList 文件信息
     * @return 文件id列表
     */
    public static List<Long> getFileIdFromStruct(List<TemplateStructSoIn> complaintStructList) {
        List<Long> fileIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(complaintStructList)) {
            for (TemplateStructSoIn templateStructSoIn : complaintStructList) {
                List<Long> tempFileIdList =
                        templateStructSoIn.getFields().stream().filter(e -> CollUtil.isNotEmpty(e.getAttachmentList()))
                                .flatMap(e -> e.getAttachmentList().stream()).map(AttachmentSoIn::getId)
                                .collect(Collectors.toList());
                fileIdList.addAll(tempFileIdList);
            }
        }
        return fileIdList;
    }

    public static BpmHtmlBo buildHtmlBo(ComplaintOrderInfoGoIn complaintOrderInfoGoIn, ComplaintApplySoIn soIn) {
        List<BpmHtmlBo.BpmHtmlRow> boList = new ArrayList<>();
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("门店ID")
                .show(complaintOrderInfoGoIn.getOrgId())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("门店名称")
                .show(complaintOrderInfoGoIn.getOrgName())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("投诉单号")
                .show(complaintOrderInfoGoIn.getComplaintNo())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("问题分类")
                .show(complaintOrderInfoGoIn.getProblemCategory())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("风险等级")
                .show("L" + complaintOrderInfoGoIn.getRiskLevel())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("问题详情")
                .show(complaintOrderInfoGoIn.getProblemDesc())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("投诉场景")
                .show(complaintOrderInfoGoIn.getComplaintContent())
                .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                .key("申请原因")
                .show(soIn.getApplyReason())
                .build());
        List<BpmHtmlBo.BpmControlGroup> groupList = new ArrayList<>();
        if (CollUtil.isNotEmpty(complaintOrderInfoGoIn.getAttachments())) {
            groupList.add(BpmHtmlBo.BpmControlGroup.builder()
                    .type("richtext")
                    .desc("富文�?)
                    .html("<div style=\"color:red;font-weight:bolder;font-size:12px;line-height:1.5;padding:10px 0\"><div>附件最长有效期�?4小时，超�?4小时请在售后工作台投诉单审批中查看附�?/div></div>")
                    .build());
            groupList.addAll(complaintOrderInfoGoIn.getAttachments()
                    .stream()
                    .map(t -> BpmHtmlBo.BpmControlGroup
                            .builder()
                            .type(t.getType() != null && t.getType() == 1 ? "img" : "file")
                            .link(t.getUrl())
                            .build())
                    .collect(Collectors.toList()));
        }
        return BpmHtmlBo.builder()
                .type("table")
                .tableName("申请免责审批")
                .column(Arrays.asList(BpmHtmlBo.BpmHtmlColumn.builder()
                                .key("key")
                                .show("事项")
                                .build(),
                        BpmHtmlBo.BpmHtmlColumn.builder()
                                .key("show")
                                .show("内容")
                                .build()))
                .data(boList)
                .controlGroup(groupList)
                .build();
    }

    public static BpmContentBo buildContentBo(ComplaintOrderInfoGoIn complaintOrderInfoGoIn, ComplaintApplySoIn soIn) {
        return BpmContentBo.builder()
                .blocks(Arrays.asList(
                        BpmContentBo.BpmBlock.builder()
                                .entities(Arrays.asList(
                                        BpmContentBo.BpmEntity.builder()
                                                .key("orgId")
                                                .showName("门店ID")
                                                .showValue(complaintOrderInfoGoIn.getOrgId())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("orgName")
                                                .showName("门店名称")
                                                .showValue(complaintOrderInfoGoIn.getOrgName())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("complaintNo")
                                                .showName("投诉单号")
                                                .showValue(complaintOrderInfoGoIn.getComplaintNo())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("issueType")
                                                .showName("问题分类")
                                                .showValue(complaintOrderInfoGoIn.getProblemCategory())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("riskLevel")
                                                .showName("风险等级")
                                                .showValue("L" + complaintOrderInfoGoIn.getRiskLevel())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("problemDesc")
                                                .showName("问题详情")
                                                .showValue(complaintOrderInfoGoIn.getProblemDesc())
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("complaint")
                                                .showName("投诉场景")
                                                .showValue(complaintOrderInfoGoIn.getComplaintContent())
                                                .property(PropertyEnum.block.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("applyReason")
                                                .showName("申请原因")
                                                .showValue(soIn.getApplyReason())
                                                .property(PropertyEnum.block.toString())
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    /**
     * 创建服务投诉判责审批�?
     *
     * @param orderInfoGoIn 客诉记录
     * @param storeName     门店列表
     * @return 审批单参�?
     */
    public static ComplaintAuditGoIn createComplaintAdjudicationApply(ComplaintOrderInfoGoIn orderInfoGoIn,
                                                                      String storeName) {
        ComplaintApplySoIn complaintApplySoIn = new ComplaintApplySoIn();
        complaintApplySoIn.setComplaintNo(orderInfoGoIn.getComplaintNo());
        complaintApplySoIn.setAuditType(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());
        complaintApplySoIn.setApplyOrgId(orderInfoGoIn.getOrgId());
        complaintApplySoIn.setApplyOrgName(storeName);
        return ComplaintAuditGoIn.builder()
                .complaintNo(orderInfoGoIn.getComplaintNo())
                .vid(orderInfoGoIn.getVid())
                .carNo(orderInfoGoIn.getCarNo())
                .contactNameC(orderInfoGoIn.getContactNameC())
                .contactPhoneC(orderInfoGoIn.getContactPhoneC())
                .contactPhoneMd5(orderInfoGoIn.getContactPhoneMd5())
                .orgId(orderInfoGoIn.getOrgId())
                .orgName(storeName)
                .zoneId(orderInfoGoIn.getZoneId())
                .littleZoneId(orderInfoGoIn.getLittleZoneId())
                .auditType(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode())
                .applyContent(GsonUtil.toJson(complaintApplySoIn))
                .auditStatus(AuditStatusEnum.PENDING.getCode())
                .createMid(orderInfoGoIn.getCreateMid())
                .auditMid(0L)
                .auditComment("")
                .build();
    }

    /**
     * 构建bpm审批任务请求入参
     * @param req 审批请求入参
     * @param latestTaskInfo 最近一次任�?
     * @param email 操作人邮�?
     * @param complaintAuditSoOut 客诉记录
     * @return 任务参数
     */
    public static TaskAuditDTO buildBpmTaskAuditDTO(SubmitForApprovalSoIn req, ProcessCurrentTaskResponseDTO.TaskInfo latestTaskInfo, String email, ComplaintAuditSoOut complaintAuditSoOut) {
        TaskAuditDTO taskAuditDTO = new TaskAuditDTO();
        taskAuditDTO.setTaskId(latestTaskInfo.getTaskId());
        taskAuditDTO.setUser(email.replace("@xiaomi.com", ""));
        // 创建审批流程审批节点必须查询的岗位的参数
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY, complaintAuditSoOut.getComplaintNo());
        extraMap.put(ComplaintInfoConstant.BPM_LITTLE_ZONE_ID_KEY, complaintAuditSoOut.getLittleZoneId());
        extraMap.put(ComplaintInfoConstant.BPM_BIG_ZONE_ID_KEY, complaintAuditSoOut.getZoneId());
        extraMap.put(ComplaintInfoConstant.BPM_SHOP_ID_KEY, complaintAuditSoOut.getOrgId());
        taskAuditDTO.setExtra(extraMap);
        // bmp仅支持通过/驳回
        if (AuditStatusEnum.APPROVED.getCode().equals(req.getAuditStatus())) {
            taskAuditDTO.setOperate(AuditOperate.Accept);
        } else if (AuditStatusEnum.REJECTED.getCode().equals(req.getAuditStatus())) {
            taskAuditDTO.setOperate(AuditOperate.Refuse);
            taskAuditDTO.setRefuseReason(req.getAuditComment());
        } else {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "auditStatus only support 2 or 3");
        }
        return taskAuditDTO;
    }

}
