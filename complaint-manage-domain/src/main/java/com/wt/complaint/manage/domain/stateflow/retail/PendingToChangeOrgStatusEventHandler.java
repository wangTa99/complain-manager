package com.wt.complaint.manage.domain.stateflow.retail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PropertyEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UpdateRetailOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailSubmitFinishApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.bo.BpmHtmlBo;
import com.wt.complaint.manage.domain.constant.BPMConst;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 待首响到门店改派�?
 * 触发动作：门店申请改�?
 */
@Slf4j
@Component
public class PendingToChangeOrgStatusEventHandler extends BaseRetailUserComplaintStatusHandler implements UserComplaintStatusEventHandler<RetailComplaintApplySoIn, RetailComplaintApplySoOut> {
    @Resource
    RetailComplaintGateway retailComplaintGateway;

    @Resource
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Resource
    BPMRemoteGateway bpmRemoteGateway;

    @Override
    public UcOrderTypeEnum getUcOrderType() {
        return UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER;
    }

    @Override
    public List<Integer> getSourceList() {
        return Collections.singletonList(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode());
    }

    @Override
    public Integer getTarget() {
        return RetailComplaintOrderStatusEnum.WAIT_CHANGE_ORG.getCode();
    }

    @Override
    @Transactional
    public RetailComplaintApplySoOut handle(RetailComplaintApplySoIn param) {
        RetailComplaintApplySoOut soOut = new RetailComplaintApplySoOut();
        // 1. 更新主表
        UpdateRetailOrderGoIn orderGoIn = UpdateRetailOrderGoIn.builder()
            .drNo(param.getDrNo())
            .orderStatus(getTarget())
            .build();
        retailComplaintGateway.updateOrderByDrNo(orderGoIn);
        // 2. 创建 bpm
        // extra组装
        Map<String, Object> extraMap = new HashMap<>();
        Map<String, StoreInfoGoOut> storeMap = param.getStoreMap();
        if (storeMap.containsKey(param.getDesOrgId())) {
            StoreInfoGoOut storeInfoGoOut = storeMap.get(param.getDesOrgId());
            extraMap.put("zoneId", storeInfoGoOut.getZoneId().toString());
            extraMap.put("littleZoneId", storeInfoGoOut.getLittleZoneId().toString());
            extraMap.put("cityId", storeInfoGoOut.getCityId());
        }
        extraMap.put("orgId", param.getDesOrgId());
        extraMap.put("drNo", param.getDrNo());
        BpmContentBo bpmContentBo = buildContentBo(param);
        BpmHtmlBo bpmHtmlBo = buildHtmlBo(param);
        RetailComplaintCreateBPMGoIn bpmParam = RetailComplaintCreateBPMGoIn.builder()
            .key(BPMConst.CHANGE_ORG_INSTANCE_KEY)
            .name(BPMConst.CHANGE_ORG_INSTANCE_NAME)
            .requestId(null)
            .creator(param.getCreateMid().toString())
            .html(GsonUtil.toJson(bpmHtmlBo))
            .extra(extraMap)
            .content(GsonUtil.toJson(bpmContentBo))
            .build();
        String processInstanceId = bpmRemoteGateway.processCreate(bpmParam);
        // 3. 更新记录�?
        ComplaintFollowProcessGoIn reAssignFollowUpProcess = createReAssignFollowUpProcess(param, processInstanceId);
        followProcessGateway.saveComplaintFollowProcess(reAssignFollowUpProcess);
        soOut.setProcessInstanceId(processInstanceId);
        return soOut;
    }

    private ComplaintFollowProcessGoIn createReAssignFollowUpProcess(RetailComplaintApplySoIn soIn, String processInstanceId) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
            .applyTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
            .applyMid(soIn.getCreateMid())
            .applyName(soIn.getCreateName())
            .applyOrgId(soIn.getApplyOrgId())
            .applyOrgName(soIn.getApplyOrgName())
            .applyOrgDisplayName("(" + soIn.getApplyOrgId() + ")" + soIn.getApplyOrgName())
            .reassignOrgId(soIn.getDesOrgId())
            .reassignOrgName(soIn.getDesOrdName())
            .reassignOrgDisplayName("(" + soIn.getDesOrgId() + ")" + soIn.getDesOrdName())
            .applyReason(soIn.getReassignRemark())
            .build();
        return ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getDrNo())
            .processInstanceId(processInstanceId)
            .processType(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
    }

    private static BpmContentBo buildContentBo(RetailComplaintApplySoIn soIn) {
        return BpmContentBo.builder()
            .blocks(Arrays.asList(
                BpmContentBo.BpmBlock.builder()
                    .entities(Arrays.asList(
                        BpmContentBo.BpmEntity.builder()
                            .key("contactName")
                            .showName("联系人姓�?)
                            .showValue(soIn.getContactName())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("contactTel")
                            .showName("联系人电�?)
                            .showValue(soIn.getContactPhone())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("drNo")
                            .showName("投诉工单")
                            .showValue(soIn.getDrNo())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("problemCategory")
                            .showName("问题分类")
                            .showValue(soIn.getProblemCategory())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("orgName")
                            .showName("投诉门店")
                            .showValue(soIn.getApplyOrgName())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("complaintScene")
                            .showName("投诉场景")
                            .showValue(soIn.getComplaintScene())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("questionDesc")
                            .showName("问题详情")
                            .showValue(soIn.getProblemDesc())
                            .property(PropertyEnum.block.toString())
                            .build()
                    ))
                    .build(),
                BpmContentBo.BpmBlock
                    .builder()
                    .entities(Arrays.asList(
                        BpmContentBo.BpmEntity.builder()
                            .key("desOrgName")
                            .showName("申请改派门店")
                            .showValue(soIn.getDesOrdName())
                            .property(PropertyEnum.inline.toString())
                            .build(),
                        BpmContentBo.BpmEntity.builder()
                            .key("applyReason")
                            .showName("申请原因")
                            .showValue(soIn.getReassignRemark())
                            .property(PropertyEnum.block.toString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    private BpmHtmlBo buildHtmlBo(RetailComplaintApplySoIn soIn) {

        List<BpmHtmlBo.BpmHtmlRow> boList = new ArrayList<>();
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("联系人姓�?)
            .show(soIn.getContactName())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("联系人电�?)
            .show(soIn.getContactPhone())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("投诉工单")
            .show(soIn.getDrNo())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("问题分类")
            .show(soIn.getProblemCategory())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("投诉场景")
            .show(soIn.getComplaintScene())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("问题详情")
            .show(soIn.getProblemDesc())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("投诉门店")
            .show(soIn.getApplyOrgName())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("申请改派门店")
            .show(soIn.getDesOrdName())
            .build());
        boList.add(BpmHtmlBo.BpmHtmlRow.builder()
            .key("申请原因")
            .show(soIn.getReassignRemark())
            .build());

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
            .build();
    }
}
