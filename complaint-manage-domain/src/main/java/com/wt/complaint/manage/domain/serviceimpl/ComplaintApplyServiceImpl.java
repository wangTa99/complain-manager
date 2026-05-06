package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.domain.aggregation.ComplaintAuditAggregation;
import com.wt.complaint.manage.domain.aggregation.ComplaintAuditAggregationFactory;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.BPMRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintApplyService;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;
import com.wt.complaint.manage.domain.bo.BpmContentBo;
import com.wt.complaint.manage.domain.bo.BpmHtmlBo;
import com.wt.complaint.manage.domain.constant.BPMConst;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.converter.DomainConverter;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.ComplaintApplyUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.constant.PushConstant.*;

@Slf4j
@Service
public class ComplaintApplyServiceImpl implements ComplaintApplyService {
    @Resource
    private ComplaintAuditRepositoryGateway complaintAuditRepositoryGateway;
    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;
    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;
    @Resource
    private ComplaintAuditGateway complaintAuditGateway;
    @Resource
    private StoreRemoteGateway storeRemoteGateway;
    @Resource
    private EiamRemoteGateway eiamRemoteGateway;
    @Resource
    private FileRemoteGateway fileRemoteGateway;
    @Resource
    private MessageInformedEventFactory messageInformedEventFactory;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private MoneThreadPoolExecutor constructMessageEventExecutor;
    @Resource
    private ComplaintApplyTransactionService complaintApplyTransactionService;

    @Resource
    private BPMRemoteGateway bpmRemoteGateway;

    /**
     * 发起审批接口
     * @param soIn 发起免责审批入参
     * @return 发起免责审批出参
     */
    @Override
    public ComplaintApplySoOut submitApply(ComplaintApplySoIn soIn) {
        ComplaintApplySoOut soOut = new ComplaintApplySoOut();
        soOut.setId(0L);
        soIn.checkApplySoIn();
        // 记录原始审批类型
        Integer auditType = soIn.getAuditType();

        // 查询客诉�?
        OrderListGoIn listGoIn = new OrderListGoIn();
        listGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        if (CollUtil.isEmpty(orderList)) {
            log.error("客诉单不存在，soIn:{}", GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该客诉单" + soIn.getComplaintNo() + "不存�?);
        }
        ComplaintOrderInfoGoIn orderInfo = orderList.get(0);
        log.info("ComplaintApplyServiceImpl#submitApply origin complaint order:{}", GsonUtil.toJson(orderInfo));

        // 校验免责审批被驳回次数是否超过限制（空值按0次处理）
        int exemptionTimes = orderInfo.getExemptionApplyTimes() == null ? 0 : orderInfo.getExemptionApplyTimes();
        validateExemptionApplyTimes(auditType, exemptionTimes, soIn);

        // 改派门店申请专项校验
        if (Objects.equals(soIn.getAuditType(), AuditTypeEnum.REASSIGNMENT_STORES.getCode())) {
            if (Objects.equals(soIn.getDesOrgId(), orderInfo.getOrgId())) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "改派门店不能与当前门店相�?);
            }
            // 创建来源 1-服务门店（客诉三期），来源于服务门店的客诉单不能改派
            if (CreateSourceEnum.STORE.getCode().equals(orderInfo.getCreateSource())) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "来源于服务门店的客诉单，不能进行改派");
            }
        }

        // V2 结案申请：根�?complaintType/是否升级 设置 auditType（含 DB �?getProcessListByNo�?
        resolveAuditTypeForFinishApplyV2(soIn, orderInfo);

        // RPC：完善登陆人信息
        EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Collections.singletonList(soIn.getCreateMid())).build();
        Map<Long, EmployeeInfoGoOut> employeeMap = eiamRemoteGateway.getEmployeeList(eiamGoIn).stream()
                .collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, Function.identity()));
        EmployeeInfoGoOut createEmployee = employeeMap.get(soIn.getCreateMid());
        soIn.setCreateName(createEmployee != null ? createEmployee.getName() : null);

        // RPC：文件持久化
        if (CollUtil.isNotEmpty(soIn.getAttachmentSoInList())) {
            List<Long> fileIdList = soIn.getAttachmentSoInList().stream().map(AttachmentSoIn::getId).collect(Collectors.toList());
            fileRemoteGateway.fileCommit(fileIdList);
        }

        // 特殊操作：来源于服务门店的结案申请，直接通过，不需要审批流程，并写�?条跟进记�?
        if (CreateSourceEnum.STORE.getCode().equals(orderInfo.getCreateSource())) {
            complaintApplyTransactionService.doSubmitFinishApplyFromStore(orderInfo, soIn);
            // 将门店报备投诉单结案完成消息移动到事务提交之�?
            ComplaintOrderGoOut complaintOrderGoOut = new ComplaintOrderGoOut();
            BeanUtil.copyProperties(orderInfo, complaintOrderGoOut);
            MessageInformedStrategy messageStrategy =
                    messageInformedEventFactory.getStrategy(PushConstant.STORE_REPORT_CLOSURE);
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintOrderGoOut,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
            return soOut;
        }

        // RPC：完善门店信�?
        List<String> orgIdList = new ArrayList<>();
        orgIdList.add(orderInfo.getOrgId());
        if (StringUtils.isNotEmpty(soIn.getDesOrgId())) {
            orgIdList.add(soIn.getDesOrgId());
        }
        List<StoreInfoGoOut> storeListInfo = storeRemoteGateway.getStoreListInfo(orgIdList);

        // 创建审批流程审批节点必须查询的岗位的参数
        if (Objects.equals(auditType, AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())) {
            startResponsibilityExemptionBpmProcess(soIn, orderInfo);
        }

        // 领域：构建申请聚合并执行 createApply
        ComplaintAuditAggregation complaintAuditAggregation = ComplaintAuditAggregationFactory.getComplaintAuditAggregation(orderInfo, storeListInfo, employeeMap);
        complaintAuditAggregation.createApply(soIn);

        // �?DB 写放入事�?
        complaintApplyTransactionService.doSubmitApplyInTransaction(complaintAuditAggregation, soIn);

        asyncSubmitApplySendMsg(orderInfo, soIn);
        return soOut;
    }

    /**
     * 启动免责审批bpm流程
     * @param soIn 申请免责审批入参
     * @param orderInfo 客诉记录
     */
    private void startResponsibilityExemptionBpmProcess(ComplaintApplySoIn soIn, ComplaintOrderInfoGoIn orderInfo) {
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(ComplaintInfoConstant.BPM_LITTLE_ZONE_ID_KEY, orderInfo.getLittleZoneId());
        extraMap.put(ComplaintInfoConstant.BPM_BIG_ZONE_ID_KEY, orderInfo.getZoneId());
        extraMap.put(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY, orderInfo.getComplaintNo());
        extraMap.put(ComplaintInfoConstant.BPM_SHOP_ID_KEY, orderInfo.getOrgId());

        // 解析客诉详情中的json并设置到客诉记录
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = buildComplaintOrderInfoGoIn(soIn, orderInfo);
        // 构建bpm表单参数
        BpmHtmlBo bpmHtmlBo = ComplaintApplyUtil.buildHtmlBo(complaintOrderInfoGoIn, soIn);
        log.info("startResponsibilityExemptionBpmProcess bpmHtmlBo={}", GsonUtil.toJson(bpmHtmlBo));

        BpmContentBo bpmContentBo = ComplaintApplyUtil.buildContentBo(complaintOrderInfoGoIn, soIn);
        RetailComplaintCreateBPMGoIn createGoIn = RetailComplaintCreateBPMGoIn.builder()
                .key(BPMConst.RESPONSIBILITY_EXEMPTION_INSTANCE_KEY)
                .name(BPMConst.RESPONSIBILITY_EXEMPTION_INSTANCE_NAME)
                .requestId(null)
                .creator(soIn.getCreateMid() == null ? null : soIn.getCreateMid().toString())
                .html(GsonUtil.toJson(bpmHtmlBo))
                .extra(extraMap)
                .content(GsonUtil.toJson(bpmContentBo))
                .build();
        // 启动bpm流程并将bpm流程ID记录到申请跟进记录上
        String processInstanceId = bpmRemoteGateway.processCreate(createGoIn);
        soIn.setProcessInstanceId(processInstanceId);
    }

    /**
     * 构建客诉审批记录
     * @param soIn 审批申请入参
     * @param orderInfo 客诉记录
     * @return 附加了json中字段的客诉记录
     */
    private ComplaintOrderInfoGoIn buildComplaintOrderInfoGoIn(ComplaintApplySoIn soIn, ComplaintOrderInfoGoIn orderInfo) {
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = ComplaintApplyUtil.parseComplaintContent(orderInfo);
        if (CollUtil.isEmpty(soIn.getAttachmentSoInList())) {
            return complaintOrderInfoGoIn;
        }
        // 查询文件ID并构建附件列�?
        List<Long> fileIds = soIn.getAttachmentSoInList().stream().map(AttachmentSoIn::getId).collect(Collectors.toList());
        List<FileInfoGoOut> fileList = fileRemoteGateway.getFileList(fileIds, null);

        // 构建文件ID到URL的映�?
        Map<Long, String> fileUrlMap = fileList.stream()
                .collect(Collectors.toMap(FileInfoGoOut::getFileId, FileInfoGoOut::getFileUrl, (a, b) -> a));

        // 将AttachmentSoIn转换为Attachment，并填充URL
        List<Attachment> attachments = soIn.getAttachmentSoInList().stream()
                .map(attachmentSoIn -> Attachment.builder()
                        .id(attachmentSoIn.getId())
                        .fileName(attachmentSoIn.getFileName())
                        .url(fileUrlMap.getOrDefault(attachmentSoIn.getId(), attachmentSoIn.getUrl()))
                        .type(attachmentSoIn.getType())
                        .build())
                .collect(Collectors.toList());
        complaintOrderInfoGoIn.setAttachments(attachments);
        return complaintOrderInfoGoIn;
    }

    /**
     * V2 结案申请时根据客诉类型及是否升级设置 auditType
     */
    private void resolveAuditTypeForFinishApplyV2(ComplaintApplySoIn soIn, ComplaintOrderInfoGoIn orderInfo) {
        if (!Boolean.TRUE.equals(soIn.getFinishApplyV2())) {
            return;
        }
        List<ComplaintFollowProcessGoOut> processList = complaintFollowProcessRepositoryGateway.getProcessListByNo(soIn.getComplaintNo());
        boolean isUpgrade = CollUtil.isNotEmpty(processList) && processList.stream()
                .anyMatch(p -> ProcessTypeEnum.UPGRADE_COMPLAINT.getProcessCode().equals(p.getProcessType()));
        if (ComplaintTypeEnum.PRODUCT_RISK.getCode().equals(orderInfo.getComplaintType()) || isUpgrade) {
            soIn.setAuditType(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode());
        } else {
            soIn.setAuditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
        }
    }

    /**
     * 校验免责审批次数是否超过上限，超过则抛业务异�?
     */
    private void validateExemptionApplyTimes(Integer auditType, int exemptionTimes, ComplaintApplySoIn soIn) {
        if (Objects.equals(auditType, AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                && exemptionTimes >= ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_APPLY_TIMES) {
            log.warn("当前客诉单免责审批次数超过最大次数：{}，soIn:{}", ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_APPLY_TIMES, GsonUtil.toJson(soIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "当前客诉单免责审批次数超�? + ComplaintInfoConstant.RESPONSIBILITY_EXEMPTION_MAX_APPLY_TIMES + "�?);
        }
    }

    private void asyncSubmitApplySendMsg(ComplaintOrderInfoGoIn orderInfo, ComplaintApplySoIn soIn) {
        CompletableFuture.runAsync(() -> {
            // 发消息，在子线程，延�?00ms执行，因为有概率出现审批单未创建就进入发消息阶段
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.info("延迟执行被中�?, e);
                // 恢复中断状�?
                Thread.currentThread().interrupt();
            }
            submitApplySendMsg(orderInfo, soIn);
        }, constructMessageEventExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞主流程
            log.error("asyncSubmitApplySendMsg error, 门店申请操作发送消息失�? orderInfo:{}, soIn:{}", RetailJsonUtil.toJson(orderInfo),
                    RetailJsonUtil.toJson(soIn), e);
            return null;
        });
    }

    private void submitApplySendMsg(ComplaintOrderInfoGoIn orderInfo, ComplaintApplySoIn soIn) {
        AuditTypeEnum auditType = AuditTypeEnum.getEnumByCode(soIn.getAuditType());
        if (auditType == null) {
            log.error("auditType is null, soIn:{}", GsonUtil.toJson(soIn));
            return;
        }
        MessageInformedEvent messageInformedEvent = null;
        ComplaintOrderGoOut goOut = DomainConverter.INSTANCE.toGoOut(orderInfo);
        switch (auditType) {
            case REASSIGNMENT_STORES:
                Map<String, String> extParams = new HashMap<>();
                extParams.put("targetOrgId", soIn.getDesOrgId());
                messageInformedEvent = messageInformedEventFactory
                        .getStrategy(REASSIGNMENT_STORE_AUDIT)
                        .createMessageInformedEvent(goOut, extParams);
                break;
            case APPLICATION_FOR_WAIVER:
                // 若需恢复自研消息，取消下方注释即可。messageInformedEvent = messageInformedEventFactory.getStrategy(APPLICATION_FOR_WAIVER_AUDIT).createMessageInformedEvent(goOut, new HashMap<>());
                break;
            case APPLICATION_72H_CANNOT_BE_CLOSED:
                messageInformedEvent = messageInformedEventFactory
                        .getStrategy(APPLICATION_72H_CANNOT_BE_CLOSED_AUDIT)
                        .createMessageInformedEvent(goOut, new HashMap<>());
                break;
            case APPLICATION_FOR_CLOSURE:
                messageInformedEvent = messageInformedEventFactory
                        .getStrategy(APPLICATION_FOR_CLOSURE_AUDIT)
                        .createMessageInformedEvent(goOut, new HashMap<>());
                break;
            case PRODUCT_RISK_CLOSURE_APPLICATION:
                messageInformedEvent = messageInformedEventFactory
                        .getStrategy(PRODUCT_RISK_CLOSURE_APPLICATION_AUDIT)
                        .createMessageInformedEvent(goOut, new HashMap<>());
                break;
            case JUDGE_RESPONSIBILITY:
                messageInformedEvent = messageInformedEventFactory
                        .getStrategy(JUDGE_RESPONSIBILITY_AUDIT)
                        .createMessageInformedEvent(goOut, new HashMap<>());
                break;
        }
        if (messageInformedEvent != null) {
            log.info("submitApplySendMsg start publishEvent, auditType:{}, goOut:{}", auditType.getDesc(),
                    RetailJsonUtil.toJson(goOut));
            eventPublisher.publishEvent(messageInformedEvent);
        }
    }

    /**
     * 根据条件持久化服务投诉判责申请记�?
     *
     * @param orderInfoGoIn 客诉记录
     * @param carStoreName  门店名称
     * @return 服务投诉判责申请出参
     */
    @Override
    public ComplaintAuditGoIn persistComplaintAdjudicationApplyRecord(ComplaintOrderInfoGoIn orderInfoGoIn, String carStoreName) {
        // 数据库已写入记录
        ComplaintOrderInfoGoIn orderInfo = new ComplaintOrderInfoGoIn();
        BeanUtil.copyProperties(orderInfoGoIn, orderInfo);

        ComplaintAuditGoIn complaintAuditGoIn = ComplaintApplyUtil.createComplaintAdjudicationApply(orderInfo, carStoreName);

        ComplaintApplySoIn complaintApplySoIn = new ComplaintApplySoIn();
        complaintApplySoIn.setComplaintNo(orderInfoGoIn.getComplaintNo());
        complaintApplySoIn.setAuditType(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode());

        log.info("persistComplaintAdjudicationApplyRecord满足自动创建判责审批条件，orderInfoGoIn:{}",
                GsonUtil.toJson(orderInfoGoIn));
        // 保存申请记录
        Boolean save = complaintAuditRepositoryGateway.save(complaintAuditGoIn);
        if (!save) {
            log.error("persistComplaintAdjudicationApplyRecord 自动创建判责审批失败, orderInfoGoIn:{}",
                    GsonUtil.toJson(orderInfoGoIn));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "系统异常");
        }
        // 异步发送消�?
        asyncSubmitApplySendMsg(orderInfo, complaintApplySoIn);
        return complaintAuditGoIn;
    }


}
