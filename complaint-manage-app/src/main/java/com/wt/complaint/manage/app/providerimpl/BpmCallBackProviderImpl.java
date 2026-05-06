package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.wt.car.soc.api.constant.WorkTypeEnum;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.provider.BpmCallBackProvider;
import com.wt.complaint.manage.app.convert.ComplaintAuditConvert;
import com.wt.complaint.manage.app.util.FeishuBotHookUtil;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintAuditService;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailApplyRetailCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.ChangeOrgCallBackSoIn;

import static com.wt.complaint.manage.domain.constant.PushConstant.RETAIL_FINISH_TO_CUSTOMER_SERVICE;

import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.enumInfo.WorkFinishTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewComplaintMessageStrategy;
import com.wt.complaint.manage.domain.strategy.deliver.message.NewMessageInformedEventFactory;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedRequest;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedResponse;
import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import com.xiaomi.newretail.bpm.api.model.callback.StatusChangeAction;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@DubboService(group = "${dubbo.group}", interfaceClass = BpmCallBackProvider.class, version = "1.0")
@Slf4j
public class BpmCallBackProviderImpl implements BpmCallBackProvider {

    @Resource
    private RetailComplaintOperateService operateService;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Autowired
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private RmqGateway rmqGateway;

    @Resource
    private ComplaintAuditService complaintAuditService;

    @Resource
    private NewMessageInformedEventFactory newMessageInformedEventFactory;

    @Value("${server.type}")
    String env;

    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    private ComplaintAuditGateway complaintAuditGateway;

    @Override
    public Result<OnStatusChangedResponse> changeOrgAuditCallback(OnStatusChangedRequest request) {
        try {
            log.info("Bpm auditCallback request:{}", GsonUtil.toJson(request));
            OnStatusChangedResponse bpmAuditResult = new OnStatusChangedResponse();
            bpmAuditResult.setAction(StatusChangeAction.CONTINUE);
            if (request.getFinished() == null || !request.getFinished()) {
                log.info("bpm 非结案通过, request:{}", request.getProcessInstanceId());
                return Result.success(bpmAuditResult);
            }
            if (!Arrays.asList(ProcessAction.Accept, ProcessAction.Refuse).contains(request.getAction())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "bpm 收到了意料之外的结案动作, bpm id: " + request.getProcessInstanceId());
            }
            List<ComplaintFollowProcessGoOut> processList = processRepositoryGateway.getProcessListByProcessInstanceId(request.getProcessInstanceId());
            if (CollUtil.isEmpty(processList) || processList.size() != 1) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "申请结案记录不唯一, bpm id: " + request.getProcessInstanceId());
            }
            String drNo = processList.get(0).getComplaintNo();
            if (!drNo.startsWith("RC")) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取到了非零售客诉单�? drNo: " + drNo);
            }
            // 数据准备，获取审批人信息，跟进记录（�?
            RetailComplaintDetaiGoOut complaintDetail =
                    retailComplaintGateway.getRetailComplaintDetail(RetailComplaintDetailGoIn.builder()
                .useMaster(true).drNo(drNo)
                .build());
            EmployeeInfoGoOut employeeInfoGoOut = eiamRemoteGateway.getEmployeeInfoByEmail(request.getOperator() + ComplaintInfoConstant.XIAOMI_EMAIL_SUFFIX);

            ChangeOrgCallBackSoIn bpmAudit = ComplaintAuditConvert.INSTANCE.toBpmAudit(request);
            bpmAudit.setOperatorName(employeeInfoGoOut.getName());
            bpmAudit.setDrNo(complaintDetail.getDrNo());
            bpmAudit.setOrderStatus(complaintDetail.getOrderStatus());
            bpmAudit.setRiskLevel(complaintDetail.getRiskLevel());
            bpmAudit.setReassignmentTimes(complaintDetail.getReassignmentTimes());
            Map<String, Object> extraMap = request.getExtra();
            Integer zoneId = Integer.valueOf((String)extraMap.getOrDefault("zoneId", "0"));
            Integer littleZoneId = Integer.valueOf((String)extraMap.getOrDefault("littleZoneId", "0"));
            Integer cityId = Integer.valueOf((String) extraMap.getOrDefault("cityId", "0"));
            String orgId = (String) extraMap.getOrDefault("orgId", "");
            bpmAudit.setOrgId(orgId);
            bpmAudit.setCityId(cityId);
            bpmAudit.setZoneId(zoneId);
            bpmAudit.setLittleZoneId(littleZoneId);
            handleRetailComplaint(bpmAudit);
            operateService.applyOrgChangeCallback(bpmAudit);
            return Result.success(bpmAuditResult);
        } catch (Exception e) {
            log.error("Bpm auditCallback error,request:{}", GsonUtil.toJson(request), e);
            return buildStopStatusChangedResponseResult();
        }
    }

    @Override
    public Result<OnStatusChangedResponse> applyFinishRetailCallback(OnStatusChangedRequest request) {
        try {
            OnStatusChangedResponse bpmAuditResult = new OnStatusChangedResponse();
            bpmAuditResult.setAction(StatusChangeAction.CONTINUE);
            log.info("Bpm applyFinishRetailCallback request:{}", GsonUtil.toJson(request));
            if (request.getFinished() == null || !request.getFinished()) {
                log.info("bpm 非结案通过, request:{}", request.getProcessInstanceId());
                return Result.success(bpmAuditResult);
            }
            if (!Arrays.asList(ProcessAction.Accept, ProcessAction.Refuse, ProcessAction.Cancel).contains(request.getAction())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "bpm 收到了意料之外的结案动作, bpm id: " + request.getProcessInstanceId());
            }

            // 完善审批人信�? bpm �?operator 大部分是邮箱前缀，没有邮箱的会返�?mid
            EmployeeInfoGoOut employee;
            if (Validator.isNumber(request.getOperator())) {
                employee = eiamRemoteGateway.getEmployee(Long.valueOf(request.getOperator()));
            } else {
                employee = eiamRemoteGateway.getEmployeeInfoByEmail(request.getOperator() + ComplaintInfoConstant.XIAOMI_EMAIL_SUFFIX);
            }

            List<ComplaintFollowProcessGoOut> processList = processRepositoryGateway.getProcessList(ComplaintProcessListGoIn.builder()
                                                                                                                            .useMaster(true)
                                                                                                                            .processInstanceId(request.getProcessInstanceId())
                                                                                                                            .processTypeList(Collections.singletonList(ProcessTypeEnum.APPLY_FINISH.getProcessCode()))
                                                                                                                            .build());
            if (CollUtil.isEmpty(processList) || processList.size() != 1) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "申请结案记录不唯一, bpm id: " + request.getProcessInstanceId());
            }
            String drNo = processList.get(0).getComplaintNo();
            if (!drNo.startsWith("RC")) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取到了非零售客诉单�? drNo: " + drNo);
            }

            RetailComplaintDetaiGoOut complaintDetail =
                    retailComplaintGateway.getRetailComplaintDetail(RetailComplaintDetailGoIn.builder()
                                                                                                                                 .useMaster(true)
                                                                                                                                 .drNo(drNo)
                                                                                                                                 .build());
            // 1. 投诉单非�?
            if (complaintDetail == null) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "零售客诉号不存在, drNo:", drNo);
            }
            // 2. 已经结案
            if (!Objects.equals(RetailComplaintOrderStatusEnum.APPLICATION_FOR_CLOSURE.getCode(), complaintDetail.getOrderStatus())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "单据未处于待结案完成状�? 状态不合法, drNo:", drNo);
            }


            RetailApplyRetailCallBackSoIn soIn = RetailApplyRetailCallBackSoIn.builder()
                                                                              .processInstanceId(request.getProcessInstanceId())
                                                                              .taskNo(request.getTaskNo())
                                                                              .operator(request.getOperator())
                                                                              .action(request.getAction())
                                                                              .refuseReason(request.getRefuseReason())
                                                                              .finished(request.getFinished())
                                                                              .extra(request.getExtra())
                                                                              .orderStatus(complaintDetail.getOrderStatus())
                                                                              .drNo(drNo)
                                                                              .auditMid(ObjectUtil.isEmpty(employee.getMiId()) ? 0L : employee.getMiId())
                                                                              .auditName(StrUtil.isEmpty(employee.getName()) ? "-" : employee.getName())
                                                                              .build();
            log.info("operateService applyFinishCallback soIn:{}", GsonUtil.toJson(soIn));
            operateService.applyFinishCallback(soIn);

            if (ProcessAction.Accept == soIn.getAction()) {
                sendMqFinishMessage(drNo);
                sendFinishMsg(drNo, complaintDetail.getSuperTicketNo(), complaintDetail.getCustomerServiceMid());
            }

            return Result.success(bpmAuditResult);
        } catch (Exception e) {
            log.error("Bpm applyFinishRetailCallback error,request:{}", GsonUtil.toJson(request), e);
            FeishuBotHookUtil.text("[销售门�?客诉结案回调异常]: " + e.getMessage() + "\n param: " + GsonUtil.toJson(request), env);
            return buildStopStatusChangedResponseResult();
        }
    }


    /**
     * 发送rmq消息，让客服服务知道状态变为已结案
     * @param drNo 客诉单号
     */
    private void sendMqFinishMessage(String drNo) {
        FinishOrderStatusMqMessageGoIn finishMrOrderStatusMqMessageBO = FinishOrderStatusMqMessageGoIn
                .builder()
                .operateType(WorkFinishTypeEnum.COMPLETED.getCode())
                .workNo(drNo)
                .workType(WorkTypeEnum.RETAIL_ORG_COMPLAINT.getId())
                .build();
        boolean sendFinishMq = rmqGateway.mrOrderStatusFinishMessage(finishMrOrderStatusMqMessageBO);
        if (!sendFinishMq) {
            log.error("WaitClosureToFinishedStatusEventHandler#sendMqFinishMessage 零售客诉单结案发送mq给客服服务失�? drNo:{}",
                    drNo);
        }
    }

    /**
     * 送站内信和飞书消息给客服，通知客服跟进人，零售客诉单已经结�?
     */
    private void sendFinishMsg(String drNo, String stNo, Long customerServiceMid) {
        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();
        complaintBasicInfo.setDrNo(drNo);
        complaintBasicInfo.setStNo(stNo);
        complaintBasicInfo.setCustomerServiceMid(customerServiceMid);

        NewComplaintMessageStrategy messageStrategy =
                newMessageInformedEventFactory.getStrategy(RETAIL_FINISH_TO_CUSTOMER_SERVICE);
        MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(complaintBasicInfo,
                new HashMap<>());
        eventPublisher.publishEvent(messageInformedEvent);
    }

    private void handleRetailComplaint(ChangeOrgCallBackSoIn soIn) {
        log.info("销售投诉填充门店跟进人");
        List<EmployeeInfoGoOut> employeeInfoGoOutList = getStoreEmployees(soIn);
        if (CollUtil.isNotEmpty(employeeInfoGoOutList)) {
            handleStoreEmployees(employeeInfoGoOutList, soIn);
        } else {
            handleFallback(soIn);
        }
    }

    /**
     * 处理门店人员
     *
     * @param soIn 零售投诉单信�?
     * @return 派单结果
     */
    private List<EmployeeInfoGoOut> getStoreEmployees(ChangeOrgCallBackSoIn soIn) {
        StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
        goIn.setOrgId(soIn.getOrgId());
        List<Integer> positionIdList =
            Arrays.asList(PositionEnum.CAR_STORE_MANAGER.getCode(), PositionEnum.CAR_STORE_OA.getCode());
        goIn.setPositionIdList(positionIdList);
        List<EmployeeInfoGoOut> employeeInfoGoOutList = eiamRemoteGateway.queryEmployeeByStore(goIn);
        log.info("employeeInfoGoOutList={}", GsonUtil.toJson(employeeInfoGoOutList));
        return employeeInfoGoOutList;
    }

    /**
     * 处理门店人员
     *
     * @param employeeInfoGoOutList 门店人员列表
     */
    private void handleStoreEmployees(List<EmployeeInfoGoOut> employeeInfoGoOutList,
                                      ChangeOrgCallBackSoIn goIn) {
        List<EmployeeInfoGoOut> carStoreManagerList = employeeInfoGoOutList.stream()
            .filter(employee -> Objects.equals(employee.getPositionId(), PositionEnum.CAR_STORE_MANAGER.getCode()))
            .collect(Collectors.toList());
        List<EmployeeInfoGoOut> carStoreOaList = employeeInfoGoOutList.stream()
            .filter(employee -> Objects.equals(employee.getPositionId(), PositionEnum.CAR_STORE_OA.getCode()))
            .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(carStoreManagerList)) {
            goIn.setOrgFollowMid(RandomUtil.randomEle(carStoreManagerList).getMiId());
            goIn.setOrgFollowPositionId(PositionEnum.CAR_STORE_MANAGER.getCode());
        } else if (CollUtil.isNotEmpty(carStoreOaList)) {
            goIn.setOrgFollowMid(RandomUtil.randomEle(carStoreOaList).getMiId());
            goIn.setOrgFollowPositionId(PositionEnum.CAR_STORE_OA.getCode());
        }
    }

    /**
     * A岗兜�?
     *
     * @param goIn 零售投诉单信�?
     */
    private void handleFallback(ChangeOrgCallBackSoIn goIn) {
        List<Long> littleZoneMids = getZonePositionMids(
            PositionEnum.CAR_MANAGER_CITY.getCode(),
            Collections.singletonList(goIn.getLittleZoneId()),
            null
        );
        if (ObjectUtil.isNotEmpty(littleZoneMids)) {
            goIn.setOrgFollowMid(RandomUtil.randomEle(littleZoneMids));
            goIn.setOrgFollowPositionId(PositionEnum.CAR_MANAGER_CITY.getCode());
        } else {
            List<Long> bigZoneMids = getZonePositionMids(
                PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode(),
                null,
                Collections.singletonList(goIn.getZoneId())
            );
            if (ObjectUtil.isNotEmpty(bigZoneMids)) {
                goIn.setOrgFollowMid(RandomUtil.randomEle(bigZoneMids));
                goIn.setOrgFollowPositionId(PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode());
            }
        }
    }

    /**
     * 根据岗位id和区域id查询人员mid
     *
     * @param positionId 岗位id
     * @param littleZoneIdList 小区域id列表
     * @param bigZoneIdList 大区id列表
     * @return mid列表
     */
    private List<Long> getZonePositionMids(Integer positionId, List<Integer> littleZoneIdList,
                                           List<Integer> bigZoneIdList) {
        List<ZonePositionUserGoOut> zonePositionUser = eiamRemoteGateway.getZonePositionUser(
            ZonePositionUserGoIn.builder()
                .positionId(positionId)
                .littleZoneIdList(littleZoneIdList)
                .bigZoneIdList(bigZoneIdList)
                .build()
        );
        return zonePositionUser.stream()
            .filter(user -> user.getUserState() == 1)
            .map(ZonePositionUserGoOut::getMid)
            .collect(Collectors.toList());
    }

    /**
     * 服务门店客诉免责申请bpm回调
     * @param request 回调参数
     * @return 给bpm的响�?
     */
    @Override
    public Result<OnStatusChangedResponse> responsibilityExemptionCallback(OnStatusChangedRequest request) {
        try {

            log.info("Bpm responsibilityExemptionCallback request:{}", GsonUtil.toJson(request));
            if (!Arrays.asList(ProcessAction.Accept, ProcessAction.Refuse, ProcessAction.Cancel).contains(request.getAction())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "bpm 收到了意料之外的申请免责动作, bpm id: " + request.getProcessInstanceId());
            }

            Map<String, Object> extraMap = Objects.isNull(request.getExtra()) ? new HashMap<>() : request.getExtra();
            // 通过维保单号+业务类型完成审批
            Object complaintNoObject = extraMap.get(ComplaintInfoConstant.BPM_COMPLAINT_NO_KEY);
            String complaintNo = Objects.nonNull(complaintNoObject) ? complaintNoObject.toString() : "";
            if (StringUtils.isEmpty(complaintNo)) {
                log.error("responsibilityExemptionCallback 申请免责回调参数缺失业务主键extraMap={}", extraMap);
                return buildStopStatusChangedResponseResult();
            }
            ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(complaintNo);
            // 投诉单非�?
            if (complaintOrderGoOut == null) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "服务客诉单不存在, complaintNo:" + complaintNo);
            }

            updateAuditInDatabase(request, complaintOrderGoOut);
            // 返回BPM结果
            OnStatusChangedResponse bpmAuditResult = new OnStatusChangedResponse();
            bpmAuditResult.setAction(StatusChangeAction.CONTINUE);
            return Result.success(bpmAuditResult);
        } catch (Exception e) {
            log.error("Bpm responsibilityExemptionCallback error,request:{}", GsonUtil.toJson(request), e);
            FeishuBotHookUtil.text("[服务门店-客诉申请免责回调异常]: " + e.getMessage() + "\n param: " + GsonUtil.toJson(request), env);
            return buildStopStatusChangedResponseResult();
        }
    }

    /**
     * 抽取bpm默认响应返回�?
     * @return bpm响应
     */
    private static Result<OnStatusChangedResponse> buildStopStatusChangedResponseResult() {
        OnStatusChangedResponse onStatusChangedResponse = new OnStatusChangedResponse();
        onStatusChangedResponse.setAction(StatusChangeAction.STOP);
        return Result.success(onStatusChangedResponse);
    }

    /**
     * 更新数据库中当前审批申请记录
     * @param request bpm回调请求
     * @param complaintOrderGoOut 客诉记录
     */
    private void updateAuditInDatabase(OnStatusChangedRequest request, ComplaintOrderGoOut complaintOrderGoOut) {
        ComplaintAuditSoOut auditSoOut = complaintAuditGateway.getRecentAuditByComplaintNo(complaintOrderGoOut.getComplaintNo(),
                AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
        if (auditSoOut == null || auditSoOut.getId() == null) {
            log.error("updateAuditAndWriteFollowRecord 未查询到免责审批�? complaintNo={}", complaintOrderGoOut.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_AUDIT_NOT_FOUND);
        }
        Integer auditStatus;
        if (ProcessAction.Accept == request.getAction()) {
            auditStatus = AuditStatusEnum.APPROVED.getCode();
        } else if (ProcessAction.Refuse == request.getAction()) {
            auditStatus = AuditStatusEnum.REJECTED.getCode();
        } else if (ProcessAction.Cancel == request.getAction()) {
            auditStatus = AuditStatusEnum.CANCELLED.getCode();
        } else {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "非法�?BPM 动作�? + request.getAction());
        }
        // 完善审批人信�? bpm �?operator 大部分是邮箱前缀，没有邮箱的会返�?mid
        EmployeeInfoGoOut employee;
        if (Validator.isNumber(request.getOperator())) {
            employee = eiamRemoteGateway.getEmployee(Long.valueOf(request.getOperator()));
        } else {
            employee = eiamRemoteGateway.getEmployeeInfoByEmail(request.getOperator() + ComplaintInfoConstant.XIAOMI_EMAIL_SUFFIX);
        }
        SubmitForApprovalSoIn soIn = SubmitForApprovalSoIn.builder()
                .id(auditSoOut.getId())
                .complaintNo(complaintOrderGoOut.getComplaintNo())
                .auditStatus(auditStatus)
                .auditComment(request.getRefuseReason())
                .auditMid(Objects.isNull(employee) || ObjectUtil.isEmpty(employee.getMiId()) ? 0L : employee.getMiId())
                // 用于传递个下一次审批任务生成跟进记录的processInstanceId
                .processInstanceId(request.getProcessInstanceId())
                .currentNode(auditSoOut.getCurrentNode())
                .build();
        complaintAuditService.updateAuditAndWriteFollowRecord(soIn, complaintOrderGoOut, auditSoOut);
    }

}
