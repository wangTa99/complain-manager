package com.wt.complaint.manage.domain.aggregation;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.wt.complaint.manage.api.model.ClosingTag;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.MediaInfoEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UserAgreementEnum;
import com.wt.complaint.manage.api.model.enums.VehicleRepairedEnum;
import com.wt.complaint.manage.domain.api.enums.PropertyEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.converter.AttachmentConvert;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintRelationClosingTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.UpdateRetailOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.ChangeOrgCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.constant.BPMConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@Slf4j
public class ComplaintAuditAggregation {
    private ComplaintAuditGoIn auditGoIn;

    private ComplaintOrderInfoGoIn orderInfo;

    private UpdateRetailOrderSoIn updateRetailOrderSoIn;

    private ComplaintFollowProcessGoIn complaintFollowProcessGoIn;

    /**
     * BPM创建申请
     */
    private RetailComplaintCreateBPMGoIn retailComplaintCreateBPMGoIn;

    // 命名有误，实际上应该是goIn
    private List<ComplaintRelationClosingTagSoIn> closingTagSoInList;

    private List<StoreInfoGoOut> carStoreList;

    private Map<Long, EmployeeInfoGoOut> employeeMap;

    public void createApply(ComplaintApplySoIn soIn) {
        validateCreateApplyContext(soIn);
        Map<String, StoreInfoGoOut> storeMap = carStoreList.stream().collect(Collectors.toMap(StoreInfoGoOut::getOrgId, e -> e, (e1, e2) -> e1));
        
        // 构建审批单基础信息、填充申请人和门店信�?
        buildAuditGoIn(soIn, storeMap);
        
        // 根据审批类型处理不同的业务逻辑
        handleAuditTypeProcess(soIn, storeMap);
        
        logInfo();
    }

    /**
     * 构建审批单基础信息
     */
    private void buildAuditGoIn(ComplaintApplySoIn soIn, Map<String, StoreInfoGoOut> storeMap) {
        StoreInfoGoOut orderStore = storeMap.get(orderInfo.getOrgId());
        this.auditGoIn = ComplaintAuditGoIn.builder()
            .complaintNo(soIn.getComplaintNo())
            .vid(orderInfo.getVid())
            .carNo(orderInfo.getCarNo())
            .contactNameC(orderInfo.getContactNameC())
            .contactPhoneC(orderInfo.getContactPhoneC())
            .contactPhoneMd5(orderInfo.getContactPhoneMd5())
            .orgId(orderInfo.getOrgId())
            .orgName(orderStore != null ? orderStore.getOrgName() : "")
            .zoneId(orderStore != null && orderStore.getZoneId() != null ? orderStore.getZoneId().toString() : "")
            .littleZoneId(orderStore != null && orderStore.getLittleZoneId() != null ? orderStore.getLittleZoneId().toString() : "")
            .auditType(soIn.getAuditType())
            .applyContent(soIn.getApplyContent())
            .auditStatus(AuditStatusEnum.PENDING.getCode())
            .createMid(soIn.getCreateMid())
            .userAgreement(soIn.getUserAgreement())
            .vehicleRepaired(soIn.getVehicleRepaired())
            .mediaInfo(soIn.getMediaInfo())
            .currentNode(Objects.equals(soIn.getAuditType(), AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode()) ? 1 : null)
            .build();
        EmployeeInfoGoOut employee = employeeMap.get(soIn.getCreateMid());
        soIn.setCreateName(employee != null ? employee.getName() : "");

        StoreInfoGoOut applyStore = storeMap.get(soIn.getApplyOrgId());
        soIn.setApplyOrgName(applyStore != null ? applyStore.getOrgName() : "");

        StoreInfoGoOut desStore = storeMap.get(soIn.getDesOrgId());
        soIn.setDesOrdName(desStore != null ? desStore.getOrgName() : "");
    }

    /**
     * 根据审批类型处理不同的业务逻辑
     */
    private void handleAuditTypeProcess(ComplaintApplySoIn soIn, Map<String, StoreInfoGoOut> storeMap) {
        if (Objects.equals(soIn.getAuditType(), AuditTypeEnum.REASSIGNMENT_STORES.getCode())) {
            handleReassignmentStores(soIn, storeMap);
        } else if (Objects.equals(soIn.getAuditType(), AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode())) {
            create72HNOFinishProcess(soIn);
        } else if (Objects.equals(soIn.getAuditType(), AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())) {
            handleApplicationForWaiver(soIn);
        } else if (Objects.equals(soIn.getAuditType(), AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                || Objects.equals(soIn.getAuditType(), AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode())) {
            createFinishProcess(soIn);
        } else {
            log.error("auditType is error");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉申请类型错误");
        }
    }

    /**
     * 处理改派门店申请
     */
    private void handleReassignmentStores(ComplaintApplySoIn soIn, Map<String, StoreInfoGoOut> storeMap) {
        StoreInfoGoOut desStore = storeMap.get(soIn.getDesOrgId());
        if (desStore != null && desStore.getLittleZoneId() != null) {
            this.auditGoIn.setLittleZoneId(desStore.getLittleZoneId().toString());
        }
        createReAssignFollowUpProcess(soIn);
    }

    /**
     * 处理申请免责
     */
    private void handleApplicationForWaiver(ComplaintApplySoIn soIn) {
        createNoDutyProcess(soIn);
        // 如果当前申请需要更新主表的免责申请次数，累�?
        if (Objects.nonNull(orderInfo)) {
            orderInfo.setExemptionApplyTimes(orderInfo.getExemptionApplyTimes() == null ? 1 : orderInfo.getExemptionApplyTimes() + 1);
        }
    }

    private void validateCreateApplyContext(ComplaintApplySoIn soIn) {
        soIn.checkApplySoIn();
        if (Objects.isNull(this.orderInfo)) {
            log.error("orderInfo is null");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单为�?);
        }
        if (CollUtil.isEmpty(this.employeeMap)) {
            log.error("employeeMap is null");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "员工信息不存�?);
        }
        if (CollUtil.isEmpty(this.carStoreList)) {
            log.error("carStore is null");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "门店信息不存�?);
        }
    }

    public void createChangeOrgBPMApply(RetailComplaintApplySoIn soIn) {
        Map<String, StoreInfoGoOut> storeMap = carStoreList.stream().collect(Collectors.toMap(e -> e.getOrgId(), e -> e, (e1, e2) -> e1));
        // soIn的填�?
        soIn.setCreateName(employeeMap.containsKey(soIn.getCreateMid()) ? employeeMap.get(soIn.getCreateMid()).getName() : "");
        soIn.setContactPhone(employeeMap.containsKey(soIn.getCreateMid()) ? employeeMap.get(soIn.getCreateMid()).getPhone() : "");
        soIn.setApplyOrgName(storeMap.containsKey(soIn.getApplyOrgId()) ? storeMap.get(soIn.getApplyOrgId()).getOrgName() : "");
        soIn.setDesOrdName(storeMap.containsKey(soIn.getDesOrgId()) ? storeMap.get(soIn.getDesOrgId()).getOrgName() : "");
        // extra组装
        Map<String, Object> extraMap = new HashMap<>();
        if (storeMap.containsKey(soIn.getDesOrgId())) {
            StoreInfoGoOut storeInfoGoOut = storeMap.get(soIn.getDesOrgId());
            extraMap.put("zoneId", storeInfoGoOut.getZoneId().toString());
            extraMap.put("littleZoneId", storeInfoGoOut.getLittleZoneId().toString());
            extraMap.put("cityId", storeInfoGoOut.getCityId());
        }
        extraMap.put("orgId", soIn.getDesOrgId());
        extraMap.put("drNo", soIn.getDrNo());
        BpmContentBo bpmContentBo = buildContentBo(soIn);
        this.retailComplaintCreateBPMGoIn = RetailComplaintCreateBPMGoIn.builder()
            .key(BPMConst.CHANGE_ORG_INSTANCE_KEY)
            .name(BPMConst.CHANGE_ORG_INSTANCE_NAME)
            .requestId(null)
            .creator(soIn.getCreateMid().toString())
            .html(StringUtils.EMPTY)
            .extra(extraMap)
            .content(GsonUtil.toJson(bpmContentBo))
            .build();
        createReAssignFollowUpProcess(soIn);
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
                            .key("complaintTypeName")
                            .showName("客诉分类")
                            .showValue(soIn.getComplaintTypeName())
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

    /**
     * 改派门店审核通过
     */
    public void acceptOrgChangeAudit(ChangeOrgCallBackSoIn soIn, ComplaintFollowProcessGoOut followProcessGoIn) {
        Map<String, Object> extraMap = soIn.getExtra();
        String drNo = (String)extraMap.getOrDefault("drNo", "");
        Integer zoneId = Integer.valueOf((String)extraMap.getOrDefault("zoneId", "0"));
        Integer littleZoneId = Integer.valueOf((String)extraMap.getOrDefault("littleZoneId", "0"));
        Integer cityId = Integer.valueOf((String) extraMap.getOrDefault("cityId", "0"));
        String orgId = (String) extraMap.getOrDefault("orgId", "");
        if (!drNo.equals(followProcessGoIn.getComplaintNo())) {
            log.error("drNo doesnt match");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不一�?);
        }
        String auditName = employeeMap.containsKey(soIn.getOperatorMid()) ? employeeMap.get(soIn.getOperatorMid()).getName() : "";

        RecordInfoGoIn recordInfoGoIn =
            RecordInfoGoIn.builder()
                .applyType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .auditTime(DateUtil.getTimeStrByDate(new Date()))
                .auditMid(soIn.getOperatorMid())
                .auditName(auditName)
                .auditResult("审核通过")
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(drNo)
            .processInstanceId(soIn.getProcessInstanceId())
            .processType(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.updateRetailOrderSoIn = UpdateRetailOrderSoIn.builder()
            .drNo(drNo)
            .zoneId(zoneId)
            .littleZoneId(littleZoneId)
            .cityId(cityId)
            .orgId(orgId)
            .reassignmentTimes(1)
            // todo-cwk 确认时间
            //.expectedResponseTime(new Date())
            //.expectedFinishTime(new Date())
            .orderStatus(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode())
            .build();
    }

    /**
     * 改派门店审核驳回
     */
    public void refuseOrgChangeAudit(ChangeOrgCallBackSoIn soIn, ComplaintFollowProcessGoOut followProcessGoIn) {
        Map<String, Object> extraMap = soIn.getExtra();
        String drNo = (String)extraMap.getOrDefault("drNo", "");
        if (!drNo.equals(followProcessGoIn.getComplaintNo())) {
            log.error("drNo doesnt match");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不一�?);
        }
        String auditName = employeeMap.containsKey(soIn.getOperatorMid()) ? employeeMap.get(soIn.getOperatorMid()).getName() : "";

        RecordInfoGoIn recordInfoGoIn =
            RecordInfoGoIn.builder()
                .applyType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .auditTime(DateUtil.getTimeStrByDate(new Date()))
                .auditMid(Long.valueOf(soIn.getOperatorMid()))
                .auditName(auditName)
                .auditResult("审核驳回")
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(drNo)
            .processInstanceId(soIn.getProcessInstanceId())
            .processType(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.updateRetailOrderSoIn = UpdateRetailOrderSoIn.builder()
            .drNo(drNo)
            .orderStatus(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode())
            .build();
    }

    /**
     * 创建申请改派门店跟进记录
     *
     * @param soIn
     */
    private void createReAssignFollowUpProcess(ComplaintApplySoIn soIn) {
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
            .applyReason(soIn.getApplyReason())
            .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getComplaintNo())
            .processType(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.orderInfo = ComplaintOrderInfoGoIn.builder().complaintNo(soIn.getComplaintNo()).status(ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode()).build();
    }

    private void createReAssignFollowUpProcess(RetailComplaintApplySoIn soIn) {
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
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getDrNo())
            .processType(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.updateRetailOrderSoIn = UpdateRetailOrderSoIn.builder().drNo(soIn.getDrNo()).orderStatus(RetailComplaintOrderStatusEnum.WAIT_CHANGE_ORG.getCode()).build();
    }

    private void create72HNOFinishProcess(ComplaintApplySoIn soIn) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
            .applyTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
            .applyMid(soIn.getCreateMid())
            .applyName(soIn.getCreateName())
            .deliveryTime(soIn.getDeliveryTime())
            .mileage(soIn.getMileage())
            .applyReason(soIn.getApplyReason())
            .attachments(AttachmentConvert.INSTANCE.toAttachmentGoIn(soIn.getAttachmentSoInList()))
            .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getComplaintNo())
            .processType(ProcessTypeEnum.APPLY_72H_CANNOT_FINISH.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.orderInfo = null;
    }

    private void createNoDutyProcess(ComplaintApplySoIn soIn) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
            .applyTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
            .applyMid(soIn.getCreateMid())
            .applyName(soIn.getCreateName())
            .applyReason(soIn.getApplyReason())
            .attachments(AttachmentConvert.INSTANCE.toAttachmentGoIn(soIn.getAttachmentSoInList()))
            .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getComplaintNo())
            .processType(ProcessTypeEnum.APPLY_EXEMPTION.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .processInstanceId(soIn.getProcessInstanceId())
            .build();
    }

    private void createFinishProcess(ComplaintApplySoIn soIn) {
        List<ComplaintRelationClosingTagSoIn> closingTagList = new ArrayList<>();
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
            .applyTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
            .applyMid(soIn.getCreateMid())
            .applyName(soIn.getCreateName())
            .finishTabList(constructFinishTag(soIn.getClosingTagList()))
            .solutionDesc(soIn.getSolutionDesc())
            .attachments(AttachmentConvert.INSTANCE.toAttachmentGoIn(soIn.getAttachmentSoInList()))
            .userAgreementDesc(UserAgreementEnum.getDescByCode(soIn.getUserAgreement()))
            .vehicleRepairedDesc(VehicleRepairedEnum.getDescByCode(soIn.getVehicleRepaired()))
            .mediaInfoDesc(MediaInfoEnum.getDescByCode(soIn.getMediaInfo()))
            .build();
        

        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
            .complaintNo(soIn.getComplaintNo())
            .processType(ProcessTypeEnum.APPLY_FINISH.getProcessCode())
            .processContent(GsonUtil.toJson(recordInfoGoIn))
            .build();
        this.orderInfo = ComplaintOrderInfoGoIn.builder().complaintNo(soIn.getComplaintNo()).status(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()).build();
    }

    private List<String> constructFinishTag(List<ClosingTag> source) {
        List<String> tagList = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return tagList;
        }
        for (ClosingTag closingTag : source) {
            // 获取最后一个�?”后面的内容
            String tagName = closingTag.getTagName().substring(closingTag.getTagName().lastIndexOf("/") + 1);
            tagList.add(tagName);
        }
        return tagList;
    }

    private void logInfo() {
        log.info("complaintApply auditGoIn:{}", GsonUtil.toJson(this.auditGoIn));
        log.info("complaintApply complaintFollowProcessGoIn:{}", GsonUtil.toJson(this.complaintFollowProcessGoIn));
        log.info("complaintApply orderInfo:{}", GsonUtil.toJson(this.orderInfo));
        log.info("complaintApply tagList:{}", GsonUtil.toJson(this.closingTagSoInList));
    }

    /**
     * 创建服务投诉判责审批单和跟进记录
     *
     * @param complaintAuditGoIn    审批单参�?
     */
    public void createComplaintAdjudicationApply(ComplaintAuditGoIn complaintAuditGoIn) {
        this.auditGoIn = complaintAuditGoIn;
        
        // 创建服务投诉判责不需要跟进记�?
        this.complaintFollowProcessGoIn = null;

        // 创建判责审批单不需要更新主表状态，保持原状�?
        this.orderInfo = null;
        logInfo();
    }

}
