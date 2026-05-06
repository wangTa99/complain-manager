package com.wt.complaint.manage.domain.stateflow.retail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ReminderFlagEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PropertyEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UpdateRetailOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailSubmitFinishApplySoIn;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.bo.BpmHtmlBo;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING;

@Slf4j
@Component
public class PendingOrProgressToWaitClosureStatusEventHandler extends BaseRetailUserComplaintStatusHandler implements UserComplaintStatusEventHandler<RetailSubmitFinishApplySoIn, String> {
    @Resource
    RetailComplaintGateway retailComplaintGateway;

    @Resource
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Resource
    BPMRemoteGateway bpmRemoteGateway;

    @Resource
    FileRemoteGateway fileRemoteGateway;

    @Override
    public UcOrderTypeEnum getUcOrderType() {
        return UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER;
    }

    @Override
    public List<Integer> getSourceList() {
        return Arrays.asList(RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode(),
                             FIRST_RESPONSE_PENDING.getCode());
    }

    @Override
    public Integer getTarget() {
        return RetailComplaintOrderStatusEnum.APPLICATION_FOR_CLOSURE.getCode();
    }

    @Override
    @Transactional
    @SuppressWarnings("squid:S3599")
    public String handle(RetailSubmitFinishApplySoIn param) {
        // 1. 更新主表
        UpdateRetailOrderGoIn orderGoIn = UpdateRetailOrderGoIn.builder()
                                                               .drNo(param.getDrNo())
                                                               .orderStatus(getTarget())
                                                               .build();
        if (Objects.equals(FIRST_RESPONSE_PENDING.getCode(), param.getOrderStatus())) {
            // 待首�?则更�?实际首响应时�?
            orderGoIn.setRealFirstResponseTime(new Date());
        }
        retailComplaintGateway.updateOrderByDrNo(orderGoIn);

        // 更新对单标识
        if (ReminderFlagEnum.TRUE.getCode().equals(param.getReminderFlag())) {
            retailComplaintGateway.updateOrderByDrNo(
                    UpdateRetailOrderGoIn.builder().drNo(param.getDrNo())
                            .reminderFlag(ReminderFlagEnum.FALSE.getCode()).build());
        }


        // 2. 创建 bpm
        String bpmId = bpmRemoteGateway.processCreate(RetailComplaintCreateBPMGoIn.builder()
                                                                                  .key("complaint_apply_finish_retail")
                                                                                  .name("结案申请")
                                                                                  .creator(param.getOperatorMid().toString())
                                                                                  .extra(new HashMap<String, Object>() {{
                                                                                      put("riskLevel", param.getRiskLevel().getDesc());
                                                                                      put("littleZone_id",String.valueOf(param.getLittleZoneId()));
                                                                                      put("bigZone_id",String.valueOf(param.getZoneId()));
                                                                                  }})
                                                                                  .content(GsonUtil.toJson(buildContentBo(param)))
                                                                                  .html(GsonUtil.toJson(buildHtmlBo(param)))
                                                                                  .build());


        // 3. 更新记录�?
        followProcessGateway.saveComplaintFollowProcess(ComplaintFollowProcessGoIn.builder()
                                                                                  .complaintNo(param.getDrNo())
                                                                                  .processType(ProcessTypeEnum.APPLY_FINISH.getProcessCode())
                                                                                  .processInstanceId(bpmId)
                                                                                  .processContent(JSON.toJSONString(new HashMap<String, Object>() {{
                                                                                      put("applyMid", param.getOperatorMid().toString()); // 申请结案�?
                                                                                      put("attachments", Optional.ofNullable(param.getAttachmentList()).orElse(new ArrayList<>())); // 附件
                                                                                      put("reconciled", param.getIsReconcile().toString()); // 是否和解
                                                                                      put("revisited", param.getCanBeRevisited()); // 是否可回�?
                                                                                      put("solutionDesc", param.getSolutionDesc()); // 解决方案
                                                                                      put("applyName", param.getApplyName()); // 申请人姓�?
                                                                                      put("applyTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 申请时间
                                                                                  }}))
                                                                                  .build());

        return bpmId;
    }

    /**
     *  飞书展示 BPM 审批详情�?
     */
    private  BpmHtmlBo buildHtmlBo(RetailSubmitFinishApplySoIn param) {

        List<BpmHtmlBo.BpmHtmlRow> boList = new ArrayList<>();
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("联系人姓�?)
                                       .show(param.getContactName())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("联系人电�?)
                                       .show(param.getContactTel())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("投诉工单")
                                       .show(param.getDrNo())
                                       .build());
        if (StrUtil.isNotEmpty(param.getComplaintTypeName())) {
            boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                           .key("客诉分类")
                                           .show(param.getComplaintTypeName())
                                           .build());
        }
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("问题分类")
                                       .show(param.getProblemCategory())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("投诉门店")
                                       .show(param.getOrgName())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("问题详情")
                                       .show(param.getQuestionDesc())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("投诉场景")
                                       .show(param.getComplaintScene())
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("是否和解")
                                       .show(param.getIsReconcile() == 1 ? "�? : "�?)
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("是否可回�?)
                                       .show(StrUtil.equals(param.getCanBeRevisited(), "1") ? "�? : "�?)
                                       .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
                                       .key("解决方案")
                                       .show(param.getSolutionDesc())
                                       .build());
        List<BpmHtmlBo.BpmControlGroup> groupList = new ArrayList<>();
        if (CollUtil.isNotEmpty(param.getAttachmentList())) {
            // 获取文件地址
            Map<Long, FileInfoGoOut> fileMap = fileRemoteGateway.getFileList(param.getAttachmentList()
                                                                                  .stream()
                                                                                  .map(Attachment::getId)
                                                                                  .collect(Collectors.toList()), 24 * 60)
                                                                .stream()
                                                                .collect(Collectors.toMap(FileInfoGoOut::getFileId,
                                                                                          Function.identity(),
                                                                                          (a, b) -> a));

            groupList.add(BpmHtmlBo.BpmControlGroup.builder()
                                                   .type("richtext")
                                                   .desc("富文�?)
                                                   .html("<div style=\"color:red;font-weight:bolder;line-height:1.5;padding:10px 0\"><div>附件最长有效期�?4小时，超�?4小时请在零售通审批中查看附件</div></div>")
                                                   .build());
            groupList.addAll(param.getAttachmentList()
                                  .stream()
                                  .map(t -> BpmHtmlBo.BpmControlGroup
                                                     .builder()
                                                     .type(t.getType() == 1 ? "img" : "file")
                                                     .desc(t.getFileName())
                                                     .link(fileMap.getOrDefault(t.getId(), new FileInfoGoOut()).getFileUrl())
                                                     .build())
                                  .collect(Collectors.toList()));
        }
        return BpmHtmlBo.builder()
                        .type("table")
                        .tableName("零售客诉详情")
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

    /**
     *  零售通展�?BPM 审批详情�?
     */
    private static BpmContentBo buildContentBo(RetailSubmitFinishApplySoIn param) {

        List<BpmContentBo.BpmEntity> entityList = new ArrayList<>();
        entityList.add(BpmContentBo.BpmEntity.builder()
                                             .key("contactName")
                                             .showName("联系人姓�?)
                                             .showValue(param.getContactName())
                                             .property(PropertyEnum.inline.toString())
                                             .build());
        entityList.add(BpmContentBo.BpmEntity.builder()
                                              .key("contactTel")
                                              .showName("联系人电�?)
                                              .showValue(param.getContactTel())
                                              .property(PropertyEnum.inline.toString())
                                              .build());
        entityList.add(BpmContentBo.BpmEntity.builder()
                                              .key("drNo")
                                              .showName("投诉工单")
                                              .showValue(param.getDrNo())
                                              .property(PropertyEnum.inline.toString())
                                              .build());
        if (StrUtil.isNotEmpty(param.getComplaintTypeName())) {
            entityList.add(BpmContentBo.BpmEntity.builder()
                                                 .key("complaintTypeName")
                                                 .showName("客诉分类")
                                                 .showValue(param.getComplaintTypeName())
                                                 .property(PropertyEnum.inline.toString())
                                                 .build());
        }
        entityList.add(BpmContentBo.BpmEntity.builder()
                                             .key("problemCategory")
                                             .showName("问题分类")
                                             .showValue(param.getProblemCategory())
                                             .property(PropertyEnum.inline.toString())
                                             .build());
        entityList.add(BpmContentBo.BpmEntity.builder()
                                             .key("complaintScene")
                                             .showName("投诉场景")
                                             .showValue(param.getComplaintScene())
                                             .property(PropertyEnum.inline.toString())
                                             .build());
        entityList.add(BpmContentBo.BpmEntity.builder()
                                             .key("orgName")
                                             .showName("投诉门店")
                                             .showValue(param.getOrgName())
                                             .property(PropertyEnum.inline.toString())
                                             .build());
        entityList.add(BpmContentBo.BpmEntity.builder()
                                             .key("questionDesc")
                                             .showName("问题详情")
                                             .showValue(param.getQuestionDesc())
                                             .property(PropertyEnum.block.toString())
                                             .build());

        return BpmContentBo.builder()
                .blocks(Arrays.asList(
                        BpmContentBo.BpmBlock.builder()
                                .entities(entityList)
                                .build(),
                        BpmContentBo.BpmBlock
                                .builder()
                                .entities(Arrays.asList(
                                        BpmContentBo.BpmEntity.builder()
                                                .key("isReconcile")
                                                .showName("是否和解")
                                                .showValue(param.getIsReconcile() == 1 ? "�? : "�?)
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("canBeRevisited")
                                                .showName("是否可回�?)
                                                .showValue(StrUtil.equals(param.getCanBeRevisited(), "1") ? "�? : "�?)
                                                .property(PropertyEnum.inline.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("solutionDesc")
                                                .showName("解决方案")
                                                .showValue(param.getSolutionDesc())
                                                .property(PropertyEnum.block.toString())
                                                .build(),
                                        BpmContentBo.BpmEntity.builder()
                                                .key("attachmentList")
                                                .showName("附件")
                                                .property(PropertyEnum.attachment.toString())
                                                .attachmentList(param.getAttachmentList())
                                                .build()
                                ))
                                .build()
                ))
                .build();

    }
}
