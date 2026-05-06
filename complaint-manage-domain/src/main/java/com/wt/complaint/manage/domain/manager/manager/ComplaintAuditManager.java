package com.wt.complaint.manage.domain.manager;

import cn.hutool.core.bean.BeanUtil;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FinishOrderStatusMqMessageGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.JudgeResponsibilitySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.enumInfo.WorkFinishTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhangzheyang
 * @date 2025/1/20
 */
@Slf4j
@Component
public class ComplaintAuditManager {

    @Resource
    private ComplaintAuditGateway complaintAuditGateway;
    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Resource
    private ComplaintTagGateway complaintTagGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;
    @Resource
    private StoreRemoteGateway storeRemoteGateway;
    @Resource
    private RmqGateway rmqGateway;
    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;


    /**
     * 通过审批
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveAudit(SubmitForApprovalSoIn soIn,
                             ComplaintAuditSoOut complaintAuditSoOut,
                             ComplaintOrderGoOut complaintOrderGoOut) {

        // 根据不同类型审批单进行更新客诉单
        AuditTypeEnum auditTypeEnum = AuditTypeEnum.getEnumByCode(complaintAuditSoOut.getAuditType());
        if (auditTypeEnum == null) {
            log.error("auditTypeEnum is null, req:{}", RetailJsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "审批单类型是未知�?);
        }
        ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
        updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
        updateInfo.setUpdateTime(new Date());
        updateInfo.setCreateSource(complaintOrderGoOut.getCreateSource());

        ComplaintTagSoIn tagSoIn = new ComplaintTagSoIn();
        tagSoIn.setComplaintNo(complaintOrderGoOut.getComplaintNo());

        switch (auditTypeEnum) {
            case REASSIGNMENT_STORES:
                handleReassignmentStoresApproval(soIn, complaintOrderGoOut, updateInfo);
                break;
            case APPLICATION_72H_CANNOT_BE_CLOSED:
                tagSoIn.setTagType(TagTypeEnum.FINISH_72H_ASSESSMENT_FREE.getCode());
                complaintTagGateway.insertTag(tagSoIn);
                break;
            case APPLICATION_FOR_WAIVER:
                handleApplicationForWaiverApproval(soIn, updateInfo, tagSoIn);
                break;
            case APPLICATION_FOR_CLOSURE:
            case PRODUCT_RISK_CLOSURE_APPLICATION:
                // 主表扭转到已结案（PRODUCT_RISK_CLOSURE_APPLICATION产品风险-申请结案，不需要打结案标签�?
                updateInfo.setStatus(ComplaintStatusEnum.FINISH_COMPLETE.getCode());
                updateInfo.setFinishTime(new Date());
                complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
                // 发送结案完成消息和mq
                sendFinishMessage(complaintOrderGoOut);
                break;
        }

        // 申请免责一�?二审通过：不写审批意见到主表，审批单保持审批中并推进到下一节点（跟进记录需在清空前快照审批意见�?
        String exemptionPassAuditCommentForRecord = null;
        if (auditTypeEnum == AuditTypeEnum.APPLICATION_FOR_WAIVER
                && soIn.getCurrentNode() != null
                && soIn.getCurrentNode() < ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_NODE_ID) {
            exemptionPassAuditCommentForRecord = soIn.getAuditComment();
            soIn.setAuditComment(null);
            soIn.setAuditStatus(AuditStatusEnum.PENDING.getCode());
            soIn.setCurrentNode(soIn.getCurrentNode() + 1);
        }

        // 更新审批�?
        complaintAuditGateway.updateAuditById(soIn);

        // 记录操作日志
        approveAuditSaveProcess(soIn, auditTypeEnum, exemptionPassAuditCommentForRecord);
    }

    /**
     * 处理改派门店审批通过
     */
    private void handleReassignmentStoresApproval(SubmitForApprovalSoIn soIn,
                                                   ComplaintOrderGoOut complaintOrderGoOut,
                                                   ComplaintOrderInfoGoIn updateInfo) {
        if (StringUtils.isBlank(soIn.getTargetOrgId())) {
            log.error("ComplaintAuditService#approveAudit targetOrgId is null, req:{}", RetailJsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "改派门店类型审批,目标店铺id不能为空");
        }

        updateInfo.setOrgId(soIn.getTargetOrgId());
        StoreInfoGoOut carStore = storeRemoteGateway.getStoreInfo(soIn.getTargetOrgId());
        if (carStore == null) {
            log.error("handleReassignmentStoresApproval 目标门店未查询到异常，orgId:{}", soIn.getTargetOrgId());
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "目标门店未查询到,内部异常");
        }
        String zoneId = Objects.nonNull(carStore.getZoneId()) ? carStore.getZoneId().toString() : "";
        String littleZoneId = Objects.nonNull(carStore.getLittleZoneId()) ? carStore.getLittleZoneId().toString() : "";
        String cityId = Objects.nonNull(carStore.getCityId()) ? carStore.getCityId() : "";

        updateInfo.setZoneId(zoneId);
        updateInfo.setLittleZoneId(littleZoneId);
        updateInfo.setCityId(cityId);
        soIn.setTargetOrgName(carStore.getOrgName());
        soIn.setTargetZoneId(zoneId);
        soIn.setTargetLittleZoneId(littleZoneId);

        // 改派门店,客诉单主表待审核 -> 待接�?
        if (!ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode().equals(complaintOrderGoOut.getStatus())) {
            log.error("approveAudit, 改派门店,当前客诉单状态不符合预期,不是申请改派门店待审�?请排查原�?complaintOrderGoOut={}",
                    RetailJsonUtil.toJson(complaintOrderGoOut));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前客诉单状态不符合预期,不是申请改派门店待审�?请排查原�?);
        }

        updateInfo.setStatus(ComplaintStatusEnum.PENDING_ORDER.getCode());
        complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
        // 改派成功后，给新门店发送通知
        sendReassignmentMsg(complaintOrderGoOut, updateInfo);
    }

    /**
     * 处理申请免责审批通过
     */
    private void handleApplicationForWaiverApproval(SubmitForApprovalSoIn soIn,
                                                     ComplaintOrderInfoGoIn updateInfo,
                                                     ComplaintTagSoIn tagSoIn) {
        // 主表responsibility,设置为免责（仅当三审通过时给打上免责标签�?
        boolean addTag = shouldAddWaiverTag(soIn);

        if (addTag) {
            // 与判责无责一致：先软删门店有责标签（无则影响行数 0），再主表免责、打投诉率免考核标签
            complaintTagGateway.deleteTag(tagSoIn.getComplaintNo(), TagTypeEnum.STORE_RESPONSIBLE.getCode());
            updateInfo.setResponsibility(ResponsibilityEnum.NO.getCode());
            complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
            tagSoIn.setTagType(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode());
            complaintTagGateway.insertTag(tagSoIn);
        }
    }

    /**
     * 判断是否应该添加免责标签
     */
    private boolean shouldAddWaiverTag(SubmitForApprovalSoIn soIn) {
        if (Objects.isNull(soIn.getCurrentNode()) || StringUtils.isEmpty(soIn.getProcessInstanceId())) {
            // 历史单据上线后只需审批一次即可打上标�?
            log.warn("approveAudit, 当前为旧申请免责审批直接打上无责标签");
            return true;
        }
        // 新数据判断currentNode是否达到最大值才打标
        return soIn.getCurrentNode() >= ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_NODE_ID;
    }

    /**
     * 结案时触发发送消息和mq
     * @param complaintOrderGoOut 客诉单对�?
     */
    private void sendFinishMessage(ComplaintOrderGoOut complaintOrderGoOut) {
        if (CreateSourceEnum.STORE.getCode().equals(complaintOrderGoOut.getCreateSource())) {
            MessageInformedStrategy messageStrategy =
                    messageInformedEventFactory.getStrategy(PushConstant.STORE_REPORT_CLOSURE);
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
        }
        //客诉工单侧workType�?0
        FinishOrderStatusMqMessageGoIn finishMrOrderStatusMqMessageBO = FinishOrderStatusMqMessageGoIn
                .builder()
                .operateType(WorkFinishTypeEnum.COMPLETED.getCode())
                .workNo(complaintOrderGoOut.getComplaintNo())
                .workType(20)
                .build();
        boolean sendFinishMq = rmqGateway.mrOrderStatusFinishMessage(finishMrOrderStatusMqMessageBO);
        if (!sendFinishMq) {
            log.error("onStatusChangeTransactionCommitAfter 发送mq失败");
        }
    }

    private void approveAuditSaveProcess(SubmitForApprovalSoIn soIn, AuditTypeEnum auditTypeEnum,
                                         String exemptionPassAuditCommentSnapshot) {
        // 查询审核人姓�?
        Map<Long, String> midToNameMap = eiamRemoteGateway.getNameByMid(Collections.singletonList(soIn.getAuditMid()));
        String auditName = midToNameMap.get(soIn.getAuditMid());

        RecordInfoGoIn.RecordInfoGoInBuilder recordBuilder = RecordInfoGoIn.builder()
                .applyType(auditTypeEnum.getCode())
                .auditTime(DateUtil.getTimeStrByDate(new Date()))
                .auditMid(soIn.getAuditMid())
                .auditName(auditName)
                .auditResult("审核通过");
        if (auditTypeEnum == AuditTypeEnum.APPLICATION_FOR_WAIVER) {
            String passAuditReason = exemptionPassAuditCommentSnapshot != null
                    ? exemptionPassAuditCommentSnapshot
                    : soIn.getAuditComment();
            recordBuilder.auditReason(passAuditReason);
        }
        RecordInfoGoIn recordInfoGoIn = recordBuilder.build();
        if (soIn.getOperatePositionId() != null) {
            // 审批人主岗位id，仅用于免责审批，需要写入到跟进记录，用于特殊过滤逻辑
            recordInfoGoIn.setOperatePositionId(soIn.getOperatePositionId());
        }
        fillExemptionFollowRecordCurrentNode(auditTypeEnum, soIn, recordInfoGoIn, true);
        ComplaintFollowProcessGoIn processGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .build();
        switch (auditTypeEnum) {
            case REASSIGNMENT_STORES:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_CHANGE_STORE_PASS.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_72H_CANNOT_BE_CLOSED:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_72H_CANNOT_FINISH_PASS.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_FOR_WAIVER:
                processGoIn.setProcessType(resolveExemptionApproveProcessType(soIn, recordInfoGoIn));
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_FOR_CLOSURE:
            case PRODUCT_RISK_CLOSURE_APPLICATION:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
        }
        log.info("approveAuditSaveProcess soIn={}, processGoIn={}", RetailJsonUtil.toJson(soIn),
                RetailJsonUtil.toJson(processGoIn));
        complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(processGoIn);
    }


    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public void refuseAudit(SubmitForApprovalSoIn soIn, ComplaintAuditSoOut complaintAuditSoOut,
                             ComplaintOrderGoOut complaintOrderGoOut) {

        AuditTypeEnum auditTypeEnum = AuditTypeEnum.getEnumByCode(complaintAuditSoOut.getAuditType());
        if (auditTypeEnum == null) {
            log.error("refuseAudit auditTypeEnum is null, req:{}", RetailJsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "审批单类型是未知�?);
        }

        // 更新审批�?
        if (AuditTypeEnum.REASSIGNMENT_STORES == auditTypeEnum) {
            // 改派门店被驳�?不能修改审批单导目标门店
            soIn.setTargetOrgId(null);
            soIn.setTargetOrgName(null);
        }
        complaintAuditGateway.updateAuditById(soIn);

        if (AuditTypeEnum.APPLICATION_FOR_CLOSURE == auditTypeEnum) {
            // 如果是结案申请被驳回,需要回滚状态到待申请结�?
            if (ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode().equals(complaintOrderGoOut.getStatus())) {
                ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
                updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
                updateInfo.setUpdateTime(new Date());
                updateInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
                complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
            } else {
                log.error("refuseAudit, 结案申请驳回,当前状态不符合预期,无法操作,soIn={}, complaintOrderGoOut={}",
                        RetailJsonUtil.toJson(soIn), RetailJsonUtil.toJson(complaintOrderGoOut));
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "结案申请驳回,当前状态不是待结案评估");
            }
        } else if (AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION == auditTypeEnum) {
            // 如果是产品风�?申请结案被驳�?需要回滚状态到待申请结�?
            if (ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode().equals(complaintOrderGoOut.getStatus())) {
                ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
                updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
                updateInfo.setUpdateTime(new Date());
                updateInfo.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
                complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
            } else {
                log.error("refuseAudit, 产品风险-申请结案驳回,当前状态不符合预期,无法操作,soIn={}, complaintOrderGoOut={}",
                        RetailJsonUtil.toJson(soIn), RetailJsonUtil.toJson(complaintOrderGoOut));
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "产品风险-申请结案驳回,当前状态不是待结案评估");
            }
        } else if (AuditTypeEnum.REASSIGNMENT_STORES == auditTypeEnum) {
            // 改派门店审核通过或驳回都需�?修正状态到待接�?
            if (ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode().equals(complaintOrderGoOut.getStatus())) {
                ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
                updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
                updateInfo.setUpdateTime(new Date());
                updateInfo.setStatus(ComplaintStatusEnum.PENDING_ORDER.getCode());
                complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
            } else {
                log.error("REASSIGNMENT_STORES, 改派门店,当前主表状态不符合预期, soIn={}, complaintOrderGoOut={}",
                        RetailJsonUtil.toJson(soIn), RetailJsonUtil.toJson(complaintOrderGoOut));
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前状态不是申请改派门店待审核");
            }
        }

        // 记录操作日志
        refuseAuditSaveProcess(soIn, auditTypeEnum);

        // 发送驳回相关消�?
        asyncSendRefuseMsg(complaintOrderGoOut, auditTypeEnum);
    }

    private void refuseAuditSaveProcess(SubmitForApprovalSoIn soIn, AuditTypeEnum auditTypeEnum) {
        // 查询审核人姓�?
        Map<Long, String> midToNameMap = eiamRemoteGateway.getNameByMid(Collections.singletonList(soIn.getAuditMid()));
        String auditName = midToNameMap.get(soIn.getAuditMid());
        // 申请免责-审核驳回且审核人为服务满意度管理岗位时，操作记录写入展示名：中台判责小组
        if (auditTypeEnum == AuditTypeEnum.APPLICATION_FOR_WAIVER && PushConstant.POSITION_SERVICE_SATISFACTION_MANAGEMENT.equals(soIn.getOperatePositionId())) {
            auditName = PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP;
        }
        RecordInfoGoIn recordInfoGoIn =
                RecordInfoGoIn.builder()
                        .applyType(auditTypeEnum.getCode())
                        .auditTime(DateUtil.getTimeStrByDate(new Date()))
                        .auditMid(soIn.getAuditMid())
                        .auditName(auditName)
                        .auditReason(soIn.getAuditComment())
                        .auditResult("审核驳回")
                        .operatePositionId(soIn.getOperatePositionId())
                        .build();
        fillExemptionFollowRecordCurrentNode(auditTypeEnum, soIn, recordInfoGoIn, false);
        ComplaintFollowProcessGoIn processGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .build();
        switch (auditTypeEnum) {
            case REASSIGNMENT_STORES:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_CHANGE_STORE_REJECT.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_72H_CANNOT_BE_CLOSED:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_72H_CANNOT_FINISH_REJECT.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_FOR_WAIVER:
                processGoIn.setProcessType(resolveExemptionRejectProcessType(soIn, recordInfoGoIn));
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
            case APPLICATION_FOR_CLOSURE:
            case PRODUCT_RISK_CLOSURE_APPLICATION:
                processGoIn.setProcessType(ProcessTypeEnum.AUDIT_FINISH_REJECT.getProcessCode());
                processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
                break;
        }
        log.info("refuseAuditSaveProcess soIn={}, processGoIn={}", RetailJsonUtil.toJson(soIn),
                RetailJsonUtil.toJson(processGoIn));
        complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(processGoIn);
    }

    /**
     * 免责审批通过/驳回的跟进记录写入本轮审批节点（1-一�?2-二审 3-三审）�?
     * 一�?二审通过后会�?currentNode 递增且状态置为审批中，记录中应展示递增前的节点�?
     */


    private String resolveExemptionApproveProcessType(SubmitForApprovalSoIn soIn, RecordInfoGoIn recordInfoGoIn) {
        Integer node = recordInfoGoIn != null ? recordInfoGoIn.getCurrentNode() : null;
        String complaintNo = soIn != null ? soIn.getComplaintNo() : null;
        String processCode;
        if (node == null) {
            log.warn("resolveExemptionApproveProcessType 新免责单缺少 currentNode，降级为 AUDIT_EXEMPTION_PASS, complaintNo={}", complaintNo);
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_PASS.getProcessCode();
        } else if (node == 1) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_FIRST_PASS.getProcessCode();
        } else if (node == 2) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_SECOND_PASS.getProcessCode();
        } else if (node == 3) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_THIRD_PASS.getProcessCode();
        } else {
            log.warn("resolveExemptionApproveProcessType 异常 currentNode={}，降级为 AUDIT_EXEMPTION_PASS, complaintNo={}", node, complaintNo);
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_PASS.getProcessCode();
        }
        return processCode;
    }

    private String resolveExemptionRejectProcessType(SubmitForApprovalSoIn soIn, RecordInfoGoIn recordInfoGoIn) {
        Integer node = recordInfoGoIn != null ? recordInfoGoIn.getCurrentNode() : null;
        String complaintNo = soIn != null ? soIn.getComplaintNo() : null;
        String processCode;
        if (node == null) {
            log.warn("resolveExemptionRejectProcessType 新免责单缺少 currentNode，降级为 AUDIT_EXEMPTION_REJECT, complaintNo={}", complaintNo);
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_REJECT.getProcessCode();
        } else if (node == 1) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_FIRST_REJECT.getProcessCode();
        } else if (node == 2) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_SECOND_REJECT.getProcessCode();
        } else if (node == 3) {
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_THIRD_REJECT.getProcessCode();
        } else {
            log.warn("resolveExemptionRejectProcessType 异常 currentNode={}，降级为 AUDIT_EXEMPTION_REJECT, complaintNo={}", node, complaintNo);
            processCode = ProcessTypeEnum.AUDIT_EXEMPTION_REJECT.getProcessCode();
        }
        return processCode;
    }

    private void fillExemptionFollowRecordCurrentNode(AuditTypeEnum auditTypeEnum, SubmitForApprovalSoIn soIn,
                                                      RecordInfoGoIn recordInfoGoIn, boolean approvePass) {
        if (auditTypeEnum != AuditTypeEnum.APPLICATION_FOR_WAIVER || soIn == null || recordInfoGoIn == null) {
            return;
        }
        if (soIn.getCurrentNode() == null) {
            return;
        }
        if (approvePass) {
            if (AuditStatusEnum.PENDING.getCode().equals(soIn.getAuditStatus())) {
                int completed = soIn.getCurrentNode() - 1;
                if (completed >= 1 && completed <= ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_NODE_ID) {
                    recordInfoGoIn.setCurrentNode(completed);
                }
            } else {
                recordInfoGoIn.setCurrentNode(soIn.getCurrentNode());
            }
        } else {
            recordInfoGoIn.setCurrentNode(soIn.getCurrentNode());
        }
    }

    private void asyncSendRefuseMsg(ComplaintOrderGoOut complaintOrderGoOut, AuditTypeEnum auditTypeEnum) {
        CompletableFuture.runAsync(() -> {
            sendRefuseMsg(complaintOrderGoOut, auditTypeEnum);
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞主流程
            log.error("asyncSendRefuseMsg error, 审批拒绝相关消息发送失�? complaintOrderGoOut:{}, auditTypeEnum:{}",
                    RetailJsonUtil.toJson(complaintOrderGoOut),
                    auditTypeEnum.getDesc(), e);
            return null;
        });
    }

    private void sendRefuseMsg(ComplaintOrderGoOut complaintOrderGoOut, AuditTypeEnum auditTypeEnum) {
        MessageInformedStrategy messageStrategy = null;
        switch (auditTypeEnum) {
            case REASSIGNMENT_STORES:
                messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.REASSIGNMENT_STORE_REFUSE);
                break;
            case APPLICATION_FOR_WAIVER:
                // 若需恢复自研消息，取消下方注释即可。messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.APPLICATION_FOR_WAIVER_REFUSE);
                break;
            case APPLICATION_72H_CANNOT_BE_CLOSED:
                messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.APPLICATION_72H_CANNOT_BE_CLOSED_REFUSE);
                break;
            case APPLICATION_FOR_CLOSURE:
            case PRODUCT_RISK_CLOSURE_APPLICATION:
                // 结案申请和产品风�?申请结案驳回不需要发送消息通知
                break;
        }
        if (messageStrategy != null) {
            log.info("start publishEvent,  auditTypeEnum:{}, complaintOrderGoOut:{}", auditTypeEnum.getDesc(),
                    RetailJsonUtil.toJson(complaintOrderGoOut));
            eventPublisher.publishEvent(messageStrategy.createMessageInformedEvent(complaintOrderGoOut, new HashMap<>()));
        }
    }

    /**
     * 服务投诉判责：更新审批状态、打标、新增跟进记录，有责时触发消�?
     */
    @Transactional(rollbackFor = Exception.class)
    public void judgeResponsibility(JudgeResponsibilitySoIn req, ComplaintAuditSoOut auditSoOut,
                                    ComplaintOrderGoOut complaintOrderGoOut) {
        // 4. 更新审批任务状态为已通过
        SubmitForApprovalSoIn soIn = SubmitForApprovalSoIn.builder()
                .id(auditSoOut.getId())
                .complaintNo(req.getComplaintNo())
                .auditStatus(AuditStatusEnum.APPROVED.getCode())
                .auditComment(req.getResponsibleJudgeDesc())
                .auditMid(req.getAuditMid())
                .build();
        complaintAuditGateway.updateAuditById(soIn);

        // 5. 打标：有�?>门店有责（并删除门店免考核标签），无责->投诉率免考核（并删除门店有责标签、主表改无责�?
        // 删除为防御性操作：软删除且 where is_deleted=0，原本无该标签时影响行数�?0，不抛异�?
        ComplaintTagSoIn tagSoIn = new ComplaintTagSoIn();
        tagSoIn.setComplaintNo(req.getComplaintNo());
        if (Integer.valueOf(1).equals(req.getResponsible())) {
            // 有责：先删除免考核标签（防御性，无则忽略），再插入门店有责标签，主表更新为有�?
            complaintTagGateway.deleteTag(req.getComplaintNo(), TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode());
            tagSoIn.setTagType(TagTypeEnum.STORE_RESPONSIBLE.getCode());
            ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
            updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
            updateInfo.setUpdateTime(new Date());
            updateInfo.setResponsibility(ResponsibilityEnum.YES.getCode());
            complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
        } else {
            // 无责：先删除有责标签（防御性，无则忽略），主表更新为无责，再插入投诉率免考核标签
            complaintTagGateway.deleteTag(req.getComplaintNo(), TagTypeEnum.STORE_RESPONSIBLE.getCode());
            ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
            updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
            updateInfo.setUpdateTime(new Date());
            updateInfo.setResponsibility(ResponsibilityEnum.NO.getCode());
            complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
            tagSoIn.setTagType(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode());
        }
        complaintTagGateway.insertTag(tagSoIn);

        // 6. 新增"服务投诉判责"跟进记录（判责人展示为中台判责小组）
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .applyType(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode())
                .auditTime(DateUtil.getTimeStrByDate(new Date()))
                .auditMid(req.getAuditMid())
                .auditName(PushConstant.DISPLAY_NAME_CENTER_JUDGE_GROUP)
                .auditResult("审核通过")
                .auditReason(req.getResponsibleJudgeDesc())
                .responsible(Integer.valueOf(1).equals(req.getResponsible()) ? "有责" : "无责")
                .responsibleJudgeDesc(req.getResponsibleJudgeDesc())
                .build();
        ComplaintFollowProcessGoIn processGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(req.getComplaintNo())
                .processType(ProcessTypeEnum.COMPLAINT_ADJUDICATION.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(processGoIn);
    }

    /**
     * 撤销门店免责申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelAudit(SubmitForApprovalSoIn soIn, ComplaintAuditSoOut complaintAuditSoOut, ComplaintOrderGoOut complaintOrderGoOut) {
        AuditTypeEnum auditTypeEnum = AuditTypeEnum.getEnumByCode(complaintAuditSoOut.getAuditType());
        if (AuditTypeEnum.APPLICATION_FOR_WAIVER != auditTypeEnum) {
            log.error("cancelAudit auditTypeEnum not match, req:{}", RetailJsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前审批单类型仅支持免责审批");
        }
        // 撤销申请不计算申请次数，撤销需要将主表免责申请次数-1(对于上线前发起免责审批的客诉单exemptionApplyTimes可能�?)
        if (complaintOrderGoOut.getExemptionApplyTimes() != null
                && complaintOrderGoOut.getExemptionApplyTimes() > 0) {
            complaintOrderGoOut.setExemptionApplyTimes(complaintOrderGoOut.getExemptionApplyTimes() - 1);
            // 更新主表免责申请次数-1
            ComplaintOrderInfoGoIn updateInfo = new ComplaintOrderInfoGoIn();
            updateInfo.setComplaintNo(complaintOrderGoOut.getComplaintNo());
            updateInfo.setUpdateTime(new Date());
            updateInfo.setExemptionApplyTimes(complaintOrderGoOut.getExemptionApplyTimes());
            complaintOrderRepositoryGateway.updateComplaintInfo(updateInfo);
        }

        // 更新审批�?
        complaintAuditGateway.updateAuditById(soIn);
        // 记录操作日志
        cancelAuditSaveProcess(soIn, auditTypeEnum);
    }

    /**
     * 保存撤销审批跟进记录
     * @param soIn           申请入参
     * @param auditTypeEnum  审批类型
     */
    private void cancelAuditSaveProcess(SubmitForApprovalSoIn soIn, AuditTypeEnum auditTypeEnum) {
        // 查询审核人姓�?
        Map<Long, String> midToNameMap = eiamRemoteGateway.getNameByMid(Collections.singletonList(soIn.getAuditMid()));
        String auditName = midToNameMap.get(soIn.getAuditMid());
        RecordInfoGoIn recordInfoGoIn =
                RecordInfoGoIn.builder()
                        .applyType(auditTypeEnum.getCode())
                        .auditTime(DateUtil.getTimeStrByDate(new Date()))
                        .auditMid(soIn.getAuditMid())
                        .auditName(auditName)
                        .auditReason(soIn.getAuditComment())
                        .auditResult("审核撤销")
                        .operatePositionId(soIn.getOperatePositionId())
                        .build();
        ComplaintFollowProcessGoIn processGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .build();
        processGoIn.setProcessType(ProcessTypeEnum.AUDIT_EXEMPTION_WITHDRAW.getProcessCode());
        processGoIn.setProcessContent(GsonUtil.toJson(recordInfoGoIn));
        log.info("cancelAuditSaveProcess soIn={}, processGoIn={}", RetailJsonUtil.toJson(soIn),
                RetailJsonUtil.toJson(processGoIn));
        complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(processGoIn);
    }

    /**
     * 改派审批通过后给新门店发送通知
     * 通知渠道、通知人、通知内容与创建投诉单时相�?
     *
     * @param complaintOrderGoOut 原客诉单信息
     * @param updateInfo 更新后的客诉单信息（包含新门店信息）
     */
    private void sendReassignmentMsg(ComplaintOrderGoOut complaintOrderGoOut, ComplaintOrderInfoGoIn updateInfo) {
        log.info("改派审批通过，开始发送消息通知, complaintNo:{}, newOrgId:{}", 
                complaintOrderGoOut.getComplaintNo(), updateInfo.getOrgId());
        
        CompletableFuture.runAsync(() -> {
            try {
                // 拷贝原客诉单信息，然后更新新门店相关字段
                ComplaintOrderGoOut updatedOrder = new ComplaintOrderGoOut();
                BeanUtil.copyProperties(complaintOrderGoOut, updatedOrder);
                updatedOrder.setOrgId(updateInfo.getOrgId());
                updatedOrder.setZoneId(updateInfo.getZoneId());
                updatedOrder.setLittleZoneId(updateInfo.getLittleZoneId());
                updatedOrder.setCityId(updateInfo.getCityId());
                
                // 判断订单是否仅查阅，选择对应的推送策�?
                MessageInformedStrategy messageStrategy;
                if (Objects.equals(updatedOrder.getOnlyView(), 1)) {
                    messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_VIEW);
                } else {
                    messageStrategy = messageInformedEventFactory.getStrategy(PushConstant.NEW_COMPLAINT_TO_DEAL);
                }
                
                if (messageStrategy != null) {
                    eventPublisher.publishEvent(messageStrategy.createMessageInformedEvent(updatedOrder, new HashMap<>()));
                }
                
                // 如果是涉媒投诉，额外发送涉媒通知
                if (Objects.equals(updatedOrder.getMediaInvolved(), 1)) {
                    MessageInformedStrategy mediaInvolvedStrategy = 
                            messageInformedEventFactory.getStrategy(PushConstant.MEDIA_INVOLVED_AUDIT);
                    if (mediaInvolvedStrategy != null) {
                        eventPublisher.publishEvent(mediaInvolvedStrategy.createMessageInformedEvent(updatedOrder, new HashMap<>()));
                    }
                }
                
                log.info("改派审批通过，消息通知发送成�? complaintNo:{}", complaintOrderGoOut.getComplaintNo());
            } catch (Exception e) {
                // 发消息失败不要阻塞改派审批主流程
                log.error("sendReassignmentMsg error, 改派审批通过后发送消息失�? complaintNo:{}", 
                        complaintOrderGoOut.getComplaintNo(), e);
            }
        }, constructMessageEventExecutor);
    }

}
