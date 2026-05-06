package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import com.wt.complaint.manage.api.model.enums.ConsultTypeEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RelationOrderEnum;
import com.wt.complaint.manage.domain.api.enums.ConsultOrderStatusEnum;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.enums.PriorityEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserConsultOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserConsultOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;
import com.wt.complaint.manage.domain.constant.KeyWordConstant;
import com.wt.complaint.manage.domain.constant.PushConstant;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.enumInfo.WorkFinishTypeEnum;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.UserComplaintRelateInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.strategy.consult.message.ConsultMessageInformedEventFactory;
import com.wt.complaint.manage.domain.strategy.consult.message.ConsultMessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 咨询单操作服务实现类
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserConsultOperateServiceImpl implements UserConsultOperateService {

    @Resource
    private UserConsultOrderGateway userConsultOrderGateway;

    @Resource
    private NoGeneratorRemoteGateway noGeneratorRemoteGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private ConsultMessageInformedEventFactory consultMessageInformedEventFactory;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private FileRemoteGateway fileRemoteGateway;


    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    public static final String POSITION_NAME = "线上客诉处理专家";


    @Resource
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;

    @Resource
    private CarRemoteGateway carRemoteGateway;


    @Resource
    private RmqGateway rmqGateway;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateConsultOrderSoOut createConsultOrder(CreateConsultOrderSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.createConsultOrder soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 生成咨询单号
            String consultNo = noGeneratorRemoteGateway.generateConsultNo();
            log.info("生成的咨询单号：{}", consultNo);

            //通过vid查vin
            String vin = carRemoteGateway.getVinByVid(soIn.getVid());
            if (StringUtils.isNotEmpty(vin) &&vin.length() >= KeyWordConstant.VIN_SUFFIX_LEN) {
                soIn.setVinSufix(
                        vin.substring(vin.length() - KeyWordConstant.VIN_SUFFIX_LEN));
            }


            // 2. 构建创建参数
            UcConsultOrderGoIn goIn = buildCreateConsultOrderGoIn(soIn, consultNo);
            
            // 3. 调用 Gateway 创建咨询�?
            int result = userConsultOrderGateway.createUserConsultOrder(goIn);
            if (result <= 0) {
                log.error("创建咨询单失败，consultNo:{}", consultNo);
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "创建咨询单失�?);
            }
            
            // 4. 返回咨询单号
            CreateConsultOrderSoOut soOut = new CreateConsultOrderSoOut();
            soOut.setConsultNo(consultNo);


            // 创建咨询单与维保工单关联关系
            if(StringUtils.isNotBlank(soIn.getExpandSoIn().getMrSuperTicketNo())){
                UserComplaintRelateInfo userComplaintRelateInfo = buildRelateInfo(soIn, consultNo);
                ComplaintRelationOrderGoIn complaintRelationOrderGoIn =
                        Convert.convert(ComplaintRelationOrderGoIn.class, userComplaintRelateInfo);
                complaintRelationOrderGoIn.setComplaintNo(consultNo);
                complaintRelationOrderRepositoryGateway.save(complaintRelationOrderGoIn);
            }

            // 5. 持久化操作记�?
            // 组装更跟进记�?
            Map<Long, String> userNameMap = eiamRemoteGateway.getNameByMid(Arrays.asList(
                    soIn.getCreateMid(),
                    soIn.getOperatorMid()));
            complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(buildComplaintFollowProcess(consultNo,soIn, userNameMap));

            //6. 发送消息提�?
            UcConsultOrderGoIn goInNew = new UcConsultOrderGoIn();
            goInNew.setConsultNo(consultNo);
            UserConsultOrderInfo userConsultOrderInfo =
                    userConsultOrderGateway.searchUserConsultOrderInfo(goInNew);
            sendMsg(userConsultOrderInfo,PushConstant.NEW_CONSULT_TO_DEAL);
            log.info("创建咨询单成功，consultNo:{}", consultNo);
            return soOut;
        } catch (BusinessException e) {
            log.error("创建咨询单失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("创建咨询单异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "创建咨询单异�?);
        }
    }

    private UserComplaintRelateInfo buildRelateInfo(CreateConsultOrderSoIn soIn, String ucNo) {
        ConsultCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();
        UserComplaintRelateInfo userComplaintRelateInfo = new UserComplaintRelateInfo();
        userComplaintRelateInfo.setUcNo(ucNo);
        String mrSuperTicketNo = expandSoIn.getMrSuperTicketNo();
        userComplaintRelateInfo.setBizNo(mrSuperTicketNo);
        userComplaintRelateInfo.setBizType(RelationOrderEnum.SUPER_TICKET_NO.getCode());
        return userComplaintRelateInfo;
    }


    private void sendMsg(UserConsultOrderInfo soOut, String type) {
        ConsultMessageInformedStrategy messageStrategy =
                consultMessageInformedEventFactory.getStrategy(type);
        CompletableFuture.runAsync(() -> {
            MessageInformedEvent messageInformedEvent = messageStrategy.createMessageInformedEvent(soOut,
                    new HashMap<>());
            eventPublisher.publishEvent(messageInformedEvent);
        }, commonThreadPoolExecutor).exceptionally(e -> {
            // 发消息失败不要阻塞创建咨询单主流�?
            log.error("sendMsg error,咨询单发送消息失�?发送类�?{}, soOut:{}", type,RetailJsonUtil.toJson(soOut),
                    e);
            return null;
        });
    }

    /**
     * 构建创建咨询单参�?
     */
    private UcConsultOrderGoIn buildCreateConsultOrderGoIn(CreateConsultOrderSoIn soIn, String consultNo) {
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();

        // 基本信息
        goIn.setConsultNo(consultNo);
        goIn.setConsultType(soIn.getExpandSoIn().getEnquireType()); // 咨询单类�?
        goIn.setSoNo(soIn.getSoNo());
        goIn.setSuperTicketNo(soIn.getSuperTicketNo());
        goIn.setVid(soIn.getVid());
        goIn.setVinSufix(soIn.getVinSufix());
        goIn.setContactNameC(soIn.getContactName());
        goIn.setContactPhoneC(soIn.getContactTel());
        goIn.setTestTag(soIn.getTestTag() != null ? soIn.getTestTag() : 0);
        goIn.setCreateMid(soIn.getCreateMid());
        goIn.setIdempotentKey(soIn.getIdempotentId());

        // 状�?
        goIn.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode()); // 待接�?
        goIn.setReminderTimes(0);

        // 时间
        goIn.setCreateTime(new Date());
        goIn.setUpdateTime(new Date());

        // 扩展信息
        if (soIn.getExpandSoIn() != null) {
            goIn.setPriority(soIn.getExpandSoIn().getPriority() != null ? soIn.getExpandSoIn().getPriority() : PriorityEnum.NORMAL.getCode());
            goIn.setProblemDesc(soIn.getExpandSoIn().getRemark());
            goIn.setOrgId(soIn.getExpandSoIn().getOrgId());
            //时间戳转date类型
            if(soIn.getExpandSoIn().getExpectedTouchTime() != null){
                goIn.setExpectingBackTime(new Date(soIn.getExpandSoIn().getExpectedTouchTime()*1000));
            }
        } else {
            goIn.setPriority(PriorityEnum.NORMAL.getCode()); // 默认一般优先级
        }

        return goIn;
    }

    /**
     * 添加跟进记录组装
     */
    private ComplaintFollowProcessGoIn buildComplaintFollowProcess(String consultNo, OrderAddFollowUpRecordSoIn soIn,
                                                                   Map<Long, String> userNameMap) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                // 操作人操作岗�?
                .operateMid(String.valueOf(soIn.getFollowUpMid()))
                .operateName(soIn.getFollowUpName())
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0") // 创建人岗位默认设�?线上客诉处理专家
                .operatePositionName(POSITION_NAME)
                //进行格式转换
                .attachments(CollUtil.isNotEmpty(soIn.getAttachmentList()) ? soIn.getAttachmentList().stream().map(attachmentSoIn -> {
                    return AttachmentGoIn.builder()
                            .id(attachmentSoIn.getId())
                            .type(attachmentSoIn.getType())
                            .url(attachmentSoIn.getUrl())
                            .fileName(attachmentSoIn.getFileName())
                            .build();
                }).collect(Collectors.toList()) : new ArrayList<>())
                .followUpContent(soIn.getFollowInfo())
                .followUpTime(DateUtil.getTimeStrByDate(new Date()))
                .followUpMid(String.valueOf(soIn.getFollowUpMid()))
                .followUpName(soIn.getFollowUpName())
                .build();
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(consultNo)
                .processType(ProcessTypeEnum.ZX_ADD_FOLLOW_RECORD.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }


    /**
     * 申请结案组装跟进记录
     */
    private ComplaintFollowProcessGoIn buildComplaintFollowProcess(String consultNo, ConsultFinishSoIn soIn,
                                                                   Map<Long, String> userNameMap) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                // 操作人操作岗�?
                .operateMid(String.valueOf(soIn.getOperateMid()))
                .operateName(userNameMap.get(soIn.getOperateMid()))
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0") // 创建人岗位默认设�?线上客诉处理专家
                .operatePositionName(POSITION_NAME)
                .applyOrgId(soIn.getApplyOrgId())
                .applyOrgName(storeRemoteGateway.getStoreInfo(soIn.getApplyOrgId()).getOrgName())
                .attachments(soIn.getFinishAttachmentList())
                .finishDesc(soIn.getFinishDesc())
                .handleType(soIn.getHandleType())
                .followUpMid(String.valueOf(soIn.getOperateMid()))
                .followUpName(userNameMap.get(soIn.getOperateMid()))
                .build();
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(consultNo)
                .processType(ProcessTypeEnum.ZX_FINISH.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

    /**
     * 编辑组装跟进记录
     */
    private ComplaintFollowProcessGoIn buildComplaintFollowProcess(UserConsultOrderInfo consultOrder, OrderEditConsultSoIn soIn, String oldMrSuperTicketNo,
                                                                   Map<Long, String> userNameMap) {
        ConsultCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();
        // 比较expandSoIn和consultOrder，找出不同的字段
        Map<String, Object> beforeUpdateMap = new HashMap<>();
        Map<String, Object> afterUpdateMap = new HashMap<>();
        //比较新旧维保单超级工单号
        if( !Objects.equals(oldMrSuperTicketNo, expandSoIn.getMrSuperTicketNo())){
            beforeUpdateMap.put("mrSuperTicketNo", oldMrSuperTicketNo);
            afterUpdateMap.put("mrSuperTicketNo", expandSoIn.getMrSuperTicketNo());
        }
        // 比较优先�?
        if (!Objects.equals(consultOrder.getPriority(), expandSoIn.getPriority())) {
            beforeUpdateMap.put("priority", consultOrder.getPriority());
            beforeUpdateMap.put("priorityName", PriorityEnum.getDescByCode(consultOrder.getPriority()));
            afterUpdateMap.put("priority", expandSoIn.getPriority());
            afterUpdateMap.put("priorityName", PriorityEnum.getDescByCode(expandSoIn.getPriority()));
        }

        // 比较咨询类型
        if (!Objects.equals(consultOrder.getConsultType(), expandSoIn.getEnquireType())) {
            beforeUpdateMap.put("consultType", consultOrder.getConsultType());
            beforeUpdateMap.put("consultTypeName", ConsultTypeEnum.getDescByCode(consultOrder.getConsultType()));
            afterUpdateMap.put("consultType", expandSoIn.getEnquireType());
            afterUpdateMap.put("consultTypeName", ConsultTypeEnum.getDescByCode(expandSoIn.getEnquireType()));
        }

        // 比较问题描述
        if (!Objects.equals(consultOrder.getProblemDesc(), expandSoIn.getRemark())) {
            beforeUpdateMap.put("problemDesc", consultOrder.getProblemDesc());
            afterUpdateMap.put("problemDesc", expandSoIn.getRemark());
        }

        // 比较门店ID
        if (!Objects.equals(consultOrder.getOrgId(), expandSoIn.getOrgId())) {
            Map<String, String> storeNameMap = storeRemoteGateway.getStoreNameMap(Arrays.asList(consultOrder.getOrgId(), expandSoIn.getOrgId()));
            beforeUpdateMap.put("orgId", storeNameMap.get(consultOrder.getOrgId()));
            afterUpdateMap.put("orgId", storeNameMap.get(expandSoIn.getOrgId()));
        }

        // 比较期望联系时间
        Date expectedTouchDateTime = expandSoIn.getExpectedTouchTime() != null ? new Date(expandSoIn.getExpectedTouchTime() * 1000) : null;
        if (!Objects.equals(consultOrder.getExpectingBackTime(), expectedTouchDateTime)) {
            beforeUpdateMap.put("expectingBackTime", DateUtil.isDefaultTime(consultOrder.getExpectingBackTime()) ? null : DateUtil.getTimeStrByDate(consultOrder.getExpectingBackTime()));
            afterUpdateMap.put("expectingBackTime", expectedTouchDateTime);
        }

        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                // 操作人操作岗�?
                .operateMid(String.valueOf(soIn.getOperatorMid()))
                .operateName(userNameMap.get(soIn.getOperatorMid()))
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0") // 创建人岗位默认设�?线上客诉处理专家
                .operatePositionName(POSITION_NAME)
                .problemCategory(soIn.getExpandSoIn().getProblemCategory())
                .orgId(soIn.getExpandSoIn().getOrgId())
                .orgName(storeRemoteGateway.getStoreInfo(soIn.getExpandSoIn().getOrgId()).getOrgName())
                // 跟进人跟进岗�?
                .operatorPositionId(soIn.getOperatorPositionId())
                .operatorPositionName(
                        DeliverPositionEnum.getDescByCode(soIn.getOperatorPositionId()))
                .questionDescription(soIn.getExpandSoIn().getRemark())
                .attachments(soIn.getExpandSoIn().getAttachments())
                .followUpMid(String.valueOf(soIn.getOperatorMid()))
                .followUpName(userNameMap.get(soIn.getOperatorMid()))
                .beforeUpdate(beforeUpdateMap.isEmpty() ? null : GsonUtil.toJson(beforeUpdateMap))
                .afterUpdate(afterUpdateMap.isEmpty() ? null : GsonUtil.toJson(afterUpdateMap))
                .build();
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(consultOrder.getConsultNo())
                .processType(ProcessTypeEnum.ZX_INFO_UPDATE.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

    /**
     * 创建组装跟进记录
     */
    private ComplaintFollowProcessGoIn buildComplaintFollowProcess(String consultNo, CreateConsultOrderSoIn soIn,
                                             Map<Long, String> userNameMap) {
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                // 操作人操作岗�?
                .operateMid(String.valueOf(soIn.getCreateMid()))
                .operateName(userNameMap.get(soIn.getCreateMid()))
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0") // 创建人岗位默认设�?线上客诉处理专家
                .operatePositionName(POSITION_NAME)

                .problemCategory(soIn.getExpandSoIn().getProblemCategory())
                .orgId(soIn.getOrgId())
                .orgName(storeRemoteGateway.getStoreInfo(soIn.getOrgId()).getOrgName())
                // 跟进人跟进岗�?
                .operatorPositionId(soIn.getOperatorPositionId())
                .operatorPositionName(
                        DeliverPositionEnum.getDescByCode(soIn.getOperatorPositionId()))
                .questionDescription(soIn.getExpandSoIn().getRemark())
                .attachments(soIn.getExpandSoIn().getAttachments())
                .followUpMid(String.valueOf(soIn.getOperatorMid()))
                .followUpName(userNameMap.get(soIn.getOperatorMid()))
                .build();
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(consultNo)
                .processType(ProcessTypeEnum.CREATE_ORDER.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderEditConsultSoOut editConsult(OrderEditConsultSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.editConsult soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderInfo consultOrder = userConsultOrderGateway.searchUserConsultOrderInfo(queryGoIn);
            
            if (consultOrder == null) {
                log.error("编辑咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }
            
            // 2. 构建更新参数
            UcConsultOrderUpdateGoIn updateGoIn = buildUpdateConsultOrderGoIn(soIn);
            
            // 3. 调用 Gateway 更新咨询�?
            int result = userConsultOrderGateway.updateOrderSelective(updateGoIn);
            String oldMrSuperTicketNo = null;

                // 查询是否已存在关联记�?
                ComplaintRelationOrderListGoIn relationQuery = ComplaintRelationOrderListGoIn.builder()
                        .complaintNoList(Arrays.asList(soIn.getConsultNo()))
                        .build();
                List<ComplaintRelationOrderGoOut> relationList = complaintRelationOrderRepositoryGateway.findList(relationQuery);
                ComplaintRelationOrderGoOut existingRelation = null;
                if(CollUtil.isNotEmpty(relationList)){
                    existingRelation = relationList.stream().filter(re -> re.getBizType() == 2).collect(Collectors.toList()).get(0);
                }
                String mrSuperTicketNo = soIn.getExpandSoIn().getMrSuperTicketNo();
                if (existingRelation != null) {
                    // 如果存在关联记录，则更新
                    oldMrSuperTicketNo = existingRelation.getBizNo();
                    ComplaintRelationOrderGoIn updateRelation = ComplaintRelationOrderGoIn.builder()
                            .id(existingRelation.getId())
                            .complaintNo(soIn.getConsultNo())
                            .bizNo(mrSuperTicketNo == null ? "" : mrSuperTicketNo)
                            .bizType(2) // 维保单类�?
                            .createTime(new Date())
                            .build();
                    complaintRelationOrderRepositoryGateway.update(updateRelation);
                } else if(StringUtils.isNotBlank(mrSuperTicketNo)){
                    // 如果不存在关联记录，则新�?
                    ComplaintRelationOrderGoIn newRelation = ComplaintRelationOrderGoIn.builder()
                            .complaintNo(soIn.getConsultNo())
                            .bizNo(mrSuperTicketNo)
                            .bizType(2) // 维保单类�?
                            .createTime(new Date())
                            .build();
                    complaintRelationOrderRepositoryGateway.save(newRelation);
                }

            //组装跟进记录
            List<Long> fileIdList = CollUtil.emptyIfNull(soIn.getExpandSoIn().getAttachments()).stream().map(AttachmentGoIn::getId).collect(Collectors.toList());
            fileRemoteGateway.fileCommit(fileIdList);
            Map<Long, String> userNameMap = eiamRemoteGateway.getNameByMid(Arrays.asList(
                    soIn.getCreateMid(),
                    soIn.getOperatorMid()));
             ComplaintFollowProcessGoIn followUpRecord = buildComplaintFollowProcess(consultOrder, soIn,oldMrSuperTicketNo, userNameMap);
             complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);

            // 4. 返回结果
            OrderEditConsultSoOut soOut = new OrderEditConsultSoOut();
            soOut.setResult("SUCCESS");
            log.info("编辑咨询单成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("编辑咨询单失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("编辑咨询单异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "编辑咨询单异�?);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultOrderPickUpSoOut pickUpOrder(ConsultOrderPickUpSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.pickUpOrder soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderMainGoOut consultOrder = userConsultOrderGateway.searchUserConsultMainData(queryGoIn);
            
            if (consultOrder == null || consultOrder.getUserConsultOrderInfoList() == null || 
                consultOrder.getUserConsultOrderInfoList().isEmpty()) {
                log.error("接单咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }
            
            // 2. 验证咨询单状态是否允许接单（待接单状态为 1�?
            UserConsultOrderInfo orderInfo = consultOrder.getUserConsultOrderInfoList().get(0);
            if (orderInfo.getOrderStatus() == null ||  orderInfo.getOrderStatus() != 1) {
                log.error("咨询单状态不允许接单，orderStatus:{}", orderInfo.getOrderStatus());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单状态不允许接单");
            }
            // 2.1 校验用户权限，是否允许接�?
            // 判断接单人在当前门店中，是否拥有对应的权限操�?
           boolean canPick = judgeHandlerAction(orderInfo.getOrgId(),Long.valueOf(soIn.getPickUpMid()),Arrays.asList(CarEmployeeEnum.RECEIVER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode(),CarEmployeeEnum.ACCIDENT_RECEIVER.getCode()));
            if (!canPick) {
                log.error("接单�?{} 无权限接单，单据号：{}", soIn.getPickUpMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "无权限主动接�?只有服务代表和服务主管能操作");
            }

            EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getPickUpMid()))).build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));

            // 构建跟进记录表内�?
            RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                    .pickUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                    .orderReceiverMid(soIn.getPickUpMid())
                    .orderReceiverName(employeeMap.containsKey(soIn.getPickUpMid()) ? employeeMap.get(soIn.getPickUpMid()).getName() : "")
                    .build();

            // 构建跟进记录表gateway入参
            ComplaintFollowProcessGoIn followUpRecord  = ComplaintFollowProcessGoIn.builder()
                    .complaintNo(soIn.getConsultNo())
                    .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode())
                    .processContent(GsonUtil.toJson(recordInfoGoIn))
                    .build();
            // 记录跟进记录�?
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }

            // 3. 更新咨询单处理人和状�?
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getConsultNo())
                    .operatorMid(Long.valueOf(soIn.getPickUpMid()))
                    .orderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode()) // 待首�?
                    .build();
            int result = userConsultOrderGateway.updateOrderSelective(updateGoIn);

            // 4. 返回结果
            ConsultOrderPickUpSoOut soOut = new ConsultOrderPickUpSoOut();
            soOut.setResult("success");
            log.info("接单成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("接单失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("接单异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "接单异常");
        }
    }

    private boolean judgeHandlerAction(String orgId, Long mid, List<Integer> list) {

        CompletableFuture<List<EmployeeInfoGoOut>> employInfoByStoreFuture = getEmployInfoByStoreFuture(list, orgId);
        List<EmployeeInfoGoOut> employeeInfoGoOuts = employInfoByStoreFuture.join();
        List<EmployeeInfoGoOut> collect = employeeInfoGoOuts.stream().filter(e -> Objects.equals(e.getMiId(), mid)).collect(Collectors.toList());
        return !collect.isEmpty();

    }
    private CompletableFuture<List<EmployeeInfoGoOut>> getEmployInfoByStoreFuture(List<Integer> positionIdList, String orgId) {
        return CompletableFuture.supplyAsync(() -> eiamRemoteGateway.queryEmployeeByStore(StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build()), commonThreadPoolExecutor);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.addFollowUpRecords soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            //查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderInfo userConsultOrderInfo = userConsultOrderGateway.searchUserConsultOrderInfo(queryGoIn);
            
            if (userConsultOrderInfo == null) {
                log.error("添加跟进记录的咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }

            // 获取登陆人信�?
            EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(Long.valueOf(soIn.getFollowUpMid()))).build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            soIn.setFollowUpName(employeeMap.containsKey(soIn.getFollowUpMid()) ? employeeMap.get(soIn.getFollowUpMid()).getName() : "");

            //跟进记录组装
            Map<Long, String> userNameMap = eiamRemoteGateway.getNameByMid(Arrays.asList(
                    userConsultOrderInfo.getCreateMid(),
                    Long.parseLong(soIn.getFollowUpMid())));
            log.info("userNameMap:{},soIn:{}", GsonUtil.toJson(userNameMap), GsonUtil.toJson(soIn));
            complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(buildComplaintFollowProcess(soIn.getConsultNo(),soIn, userNameMap));

            //如果咨询单是待首响状态则更新到待结案状�?
            if(Objects.equals(userConsultOrderInfo.getOrderStatus(), ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode())){
                UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                        .consultNo(soIn.getConsultNo())
                        .operatorMid(Long.valueOf(soIn.getFollowUpMid()))
                        .orderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode()) // 待结�?
                        .build();
                int result = userConsultOrderGateway.updateOrderSelective(updateGoIn);
                if (result <= 0) {
                    log.error("更新咨询单状态失败，soIn:{}", GsonUtil.toJson(soIn));
                    throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "更新咨询单状态失�?);
                }
            }

            //返回结果
            OrderFollowUpRecordSoOut soOut = new OrderFollowUpRecordSoOut();
            soOut.setRecordResult("SUCCESS");
            log.info("添加跟进记录成功，ucNo:{}", soIn.getUcNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("添加跟进记录失败，soIn:{},errorMsg:{}", GsonUtil.toJson(soIn), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("添加跟进记录异常，soIn:{},errorMsg:{}", GsonUtil.toJson(soIn), e.getMessage());
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "添加跟进记录异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultReassignSoOut reassign(ConsultReassignSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.reassign soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderMainGoOut consultOrder = userConsultOrderGateway.searchUserConsultMainData(queryGoIn);
            
            if (consultOrder == null || consultOrder.getUserConsultOrderInfoList() == null || 
                consultOrder.getUserConsultOrderInfoList().isEmpty()) {
                log.error("改派咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }
            // 2. 判断咨询单的状态（只有待首响以及待结案，才允许改派跟进人）
            UserConsultOrderInfo orderInfo = consultOrder.getUserConsultOrderInfoList().get(0);
            if (orderInfo.getOrderStatus() == null || (orderInfo.getOrderStatus() != 2 && orderInfo.getOrderStatus() != 3)) {
                log.error("咨询单状态不允许改派跟进人，orderStatus:{}", orderInfo.getOrderStatus());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单状态不允许改派跟进�?);
            }
            
            // 2. 验证权限
               // 2.1 校验发起人在当前的门店中 是否有店长、服务主管岗�?
            Map<Long, CarEmployee> midCarEmployeeMap = carEmployeeRemoteGateway.queryCarEmployee(Arrays.asList(soIn.getOperateMid(),soIn.getReassignOperatorMid()));

            if (midCarEmployeeMap.entrySet().isEmpty()) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取岗位信息失败");
            }
            if (midCarEmployeeMap.get(soIn.getOperateMid()) == null) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取操作人岗位信息失�?);
            }
            // 校验 操作人权限（在当前的门店�?是否为店长、或服务主管岗位�?


            boolean canDispatch =  judgeHandlerAction(orderInfo.getOrgId(), soIn.getOperateMid(),Arrays.asList(CarEmployeeEnum.MANAGER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode()));
            if (!canDispatch) {
                log.error("操作�?{} 无权限执行改派跟进人，单据号：{}", soIn.getOperateMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "无权限执行改派跟进人，只有店长、服务主管能操作");
            }

            //2.2  校验处理人权�?（在当前的门店中 是否为服务代表、服务主管）
            boolean canReceive = judgeHandlerAction(orderInfo.getOrgId(), soIn.getReassignOperatorMid(),Arrays.asList(CarEmployeeEnum.RECEIVER.getCode(),CarEmployeeEnum.ACCIDENT_RECEIVER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode()));

            if (!canReceive) {
                log.error("跟进�?{} 无权限跟进，单据号：{}", soIn.getReassignOperatorMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "跟进人无权限跟进，只有服务代表、服务主管能操作");
            }
            // 附件持久�?
            if (CollUtil.isNotEmpty(soIn.getAttachmentList())) {
                List<Long> fileIdList = soIn.getAttachmentList().stream()
                        .map(AttachmentGoIn::getId)
                        .collect(Collectors.toList());
                fileRemoteGateway.fileCommit(fileIdList);
            }
            
            // 3. 更新咨询单门店和处理�?
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getConsultNo())
                    .orgId(soIn.getOrgId())
                    .operatorMid(soIn.getReassignOperatorMid())
                    .build();
            
           userConsultOrderGateway.updateOrderSelective(updateGoIn);

            
            // 4. 发送通知
             sendMsg(orderInfo,PushConstant.CONSULT_REASSIGN);

            // 5. 记录操作日志
            // 存跟进记�?
            RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                    .operateMid(String.valueOf(soIn.getOperateMid()))
                    .dispatcherName(midCarEmployeeMap.containsKey(soIn.getOperateMid()) ? midCarEmployeeMap.get(soIn.getOperateMid()).getName() : "")
                    .operateTime(DateUtil.getTimeStrByDate(new Date()))
                    .operatePositionId(String.valueOf(midCarEmployeeMap.get(soIn.getOperateMid()).getPositionId()))
                    .reassignDesc(soIn.getReassignDesc())
                    .orgId(orderInfo.getOrgId())
                    .orgName(storeRemoteGateway.getStoreInfo(orderInfo.getOrgId()).getOrgName())
                    .reassignOrgId(soIn.getOrgId())
                    .reassignOrgName(storeRemoteGateway.getStoreInfo(soIn.getOrgId()).getOrgName())
                    .reassignOperatorPositionId(soIn.getReassignOperatorPositionId())
                    .reassignOperatorMid(soIn.getReassignOperatorMid())
                    .orderReceiverName(midCarEmployeeMap.containsKey(soIn.getReassignOperatorMid()) ? midCarEmployeeMap.get(soIn.getReassignOperatorMid()).getName() : "")
                    .attachments(soIn.getAttachmentList())
                    .build();
            // 构建跟进记录表gateway入参
            ComplaintFollowProcessGoIn followUpRecord  = ComplaintFollowProcessGoIn.builder()
                    .complaintNo(soIn.getConsultNo())
                    .processType(ProcessTypeEnum.REASSIGN_HANDLER.getProcessCode())
                    .processContent(GsonUtil.toJson(recordInfoGoIn))
                    .build();
            // 记录跟进记录�?
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }

            // 6. 返回结果
            ConsultReassignSoOut soOut = new ConsultReassignSoOut();
            soOut.setResult("success");
            log.info("改派成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("改派失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("改派异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "改派异常");
        }
    }

    @Override
    public ConsultOrgChangeApplySoOut submitChangeOrgApply(ConsultOrgChangeApplySoIn soIn) {
        log.info("UserConsultOperateServiceImpl.submitChangeOrgApply soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderMainGoOut consultOrder = userConsultOrderGateway.searchUserConsultMainData(queryGoIn);
            
            if (consultOrder == null || consultOrder.getUserConsultOrderInfoList() == null || 
                consultOrder.getUserConsultOrderInfoList().isEmpty()) {
                log.error("改派门店的咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }

            // 2. 验证咨询单状态是否允许改派门店（待接单状态为 1�?
            UserConsultOrderInfo orderInfo = consultOrder.getUserConsultOrderInfoList().get(0);
            if (orderInfo.getOrderStatus() == null ||  orderInfo.getOrderStatus() != 1) {
                log.error("咨询单状态不允许改派门店，orderStatus:{}", orderInfo.getOrderStatus());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单状态不允许改派门店");
            }
            
            // 2. 验证权限
            boolean canSubmitApplyOrg =  judgeHandlerAction(orderInfo.getOrgId(), soIn.getOperateMid(),Arrays.asList(CarEmployeeEnum.RECEIVER.getCode(),CarEmployeeEnum.ACCIDENT_RECEIVER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode(),CarEmployeeEnum.MANAGER.getCode()));
            if (!canSubmitApplyOrg) {
                log.error("门店改派�?{} 无权限改派门店，单据号：{}", soIn.getOperateMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "无权限改派门店，只有服务代表、服主管、店长能操作");
            }

            // 3 直接改派门店，无需申请
            EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getOperateMid()))).build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<Long, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId(), Function.identity()));

            // 构建跟进记录表内�?
            RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                    .operateTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                    .applyOrgId(soIn.getApplyOrgId())
                    .reassignOrgId(soIn.getDesOrgId())
                    .applyOrgDisplayName(storeRemoteGateway.getStoreInfo(soIn.getApplyOrgId()).getOrgName())
                    .applyOrgName(storeRemoteGateway.getStoreInfo(soIn.getApplyOrgId()).getOrgName())
                    .reassignOrgName(storeRemoteGateway.getStoreInfo(soIn.getDesOrgId()).getOrgName())
                    .reassignOrgDisplayName(storeRemoteGateway.getStoreInfo(soIn.getDesOrgId()).getOrgName())
                    .operateMid(String.valueOf(soIn.getOperateMid()))
                    .operateName(employeeMap.containsKey(soIn.getOperateMid()) ? employeeMap.get(soIn.getOperateMid()).getName() : "")
                    .reassignDesc(soIn.getReassignRemark())
                    .build();

            // 构建跟进记录表gateway入参
            ComplaintFollowProcessGoIn followUpRecord  = ComplaintFollowProcessGoIn.builder()
                    .complaintNo(soIn.getConsultNo())
                    .processType(ProcessTypeEnum.REASSIGN_STORE.getProcessCode())
                    .processContent(GsonUtil.toJson(recordInfoGoIn))
                    .build();
            // 记录跟进记录�?
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }

            // 3. 更新咨询单内�?
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getConsultNo())
                    .orgId(soIn.getDesOrgId())
                    .build();
            userConsultOrderGateway.updateOrderSelective(updateGoIn);

            // 4. 返回申请流程 ID
            ConsultOrgChangeApplySoOut soOut = new ConsultOrgChangeApplySoOut();
            soOut.setResult("success");
            log.info("改派门店成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("改派门店失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("改派门店异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "改派门店异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultUpdateHandlerSoOut updateHandler(ConsultUpdateHandlerSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.updateHandler soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderMainGoOut consultOrder = userConsultOrderGateway.searchUserConsultMainData(queryGoIn);
            
            if (consultOrder == null || consultOrder.getUserConsultOrderInfoList() == null || 
                consultOrder.getUserConsultOrderInfoList().isEmpty()) {
                log.error("更新处理人的咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }

            // 2. 验证咨询单状态是否允许派单（待接单状态为 1�?
            UserConsultOrderInfo orderInfo = consultOrder.getUserConsultOrderInfoList().get(0);
            if (orderInfo.getOrderStatus() == null || orderInfo.getOrderStatus() != 1) {
                log.error("咨询单状态不允许派单，orderStatus:{}", orderInfo.getOrderStatus());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单状态不允许派单");
            }
            
            // 2. 验证权限
              // 2.1 校验 操作人权限（是否为店长、或服务主管岗位�?
            boolean canDispatch =  judgeHandlerAction(orderInfo.getOrgId(), soIn.getOperateMid(),Arrays.asList(CarEmployeeEnum.MANAGER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode()));

            if (!canDispatch) {
                log.error("派单�?{} 无权限派单，单据号：{}", soIn.getOperateMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "派单人无权限派单，只有店长、服务主管能操作");
            }

            //2.2  校验处理人权�?（是否为服务代表、服务主管）
            boolean canReceive = judgeHandlerAction(orderInfo.getOrgId(), soIn.getOperatorMid(),Arrays.asList(CarEmployeeEnum.RECEIVER.getCode(),CarEmployeeEnum.ACCIDENT_RECEIVER.getCode(),CarEmployeeEnum.RECEIVER_MANAGER.getCode()));
            if (!canReceive) {
                log.error("接单�?{} 无权限接单，单据号：{}", soIn.getOperatorMid(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "接单人无权限接单，只有服务代表、服务主管能操作");
            }
            // 3. 更新咨询单处理人
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getConsultNo())
                    .operatorMid(soIn.getOperatorMid())
                    .orderStatus(ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode()) // 待首�?
                    .build();
            
           userConsultOrderGateway.updateOrderSelective(updateGoIn);

            // 4. 记录操作日志
            // 构建跟进记录表内�?
            EmployeeListGoIn eiamGoIn = EmployeeListGoIn.builder().miIdList(Arrays.asList(soIn.getOperatorMid(),soIn.getOperateMid())).build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                    .orderReceiverMid(String.valueOf(soIn.getOperatorMid()))
                    .orderReceiverName(employeeMap.containsKey(String.valueOf(soIn.getOperatorMid())) ? employeeMap.get(String.valueOf(soIn.getOperatorMid())).getName() : "")
                    .dispatchTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                    .dispatcherMid(String.valueOf(soIn.getOperateMid()))
                    .dispatcherName(employeeMap.containsKey(String.valueOf(soIn.getOperateMid())) ? employeeMap.get(String.valueOf(soIn.getOperateMid())).getName() : "")
                    .build();

            // 构建跟进记录表gateway入参
            ComplaintFollowProcessGoIn followUpRecord  = ComplaintFollowProcessGoIn.builder()
                    .complaintNo(soIn.getConsultNo())
                    .processType(ProcessTypeEnum.DISPATCH_ORDER.getProcessCode())
                    .processContent(GsonUtil.toJson(recordInfoGoIn))
                    .build();
            // 记录跟进记录�?
            if (followUpRecord != null) {
                complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
            }
            // 5. 返回结果
            ConsultUpdateHandlerSoOut soOut = new ConsultUpdateHandlerSoOut();
            soOut.setResult("success");
            log.info("更新处理人成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("更新处理人失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("更新处理人异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新处理人异�?);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultFinishSoOut finish(ConsultFinishSoIn soIn) {
        log.info("UserConsultOperateServiceImpl.finish soIn:{}", GsonUtil.toJson(soIn));
        
        try {
            // 1. 查询咨询单是否存�?
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getConsultNo());
            UserConsultOrderInfo consultOrder = userConsultOrderGateway.searchUserConsultOrderInfo(queryGoIn);
            
            if (consultOrder == null) {
                log.error("结案的咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
            }

            //咨询单状态不是待结案
            if(!Objects.equals(ConsultOrderStatusEnum.WAIT_CLOSE.getCode(),consultOrder.getOrderStatus())) {
                log.error("咨询单状态不是待结案，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单状态不是待结案");
            }

            // 2. 验证权限和结案条�?
            UserInfo userInfo = UserInfo.fromRpcContext();
            boolean canFinish = judgeHandlerAction(consultOrder.getOrgId(),Long.valueOf(userInfo.getMiID()),Arrays.asList(CarEmployeeEnum.RECEIVER_MANAGER.getCode(),CarEmployeeEnum.MANAGER.getCode()));
            if (!canFinish && !Objects.equals(userInfo.getMiID(), consultOrder.getOperatorMid())) {
                log.warn("当前操作�?{} 无权限结案，单据号：{}", userInfo.getMiID(), soIn.getConsultNo());
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "仅有主管跟店�?�?咨询单跟进人能操作提交结�?);
            }
            
            // 3. 更新咨询单状态为已完�?
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getConsultNo())
                    .orderStatus(ConsultOrderStatusEnum.COMPLETED.getCode()) // 已完�?
                    .finishTime(new Date())
                    .operatorMid(soIn.getOperateMid())
                    .finishDesc(soIn.getFinishDesc())
                    .handleResult(soIn.getHandleType())
                    .build();
            
            int result = userConsultOrderGateway.updateOrderSelective(updateGoIn);
            if (result <= 0) {
                log.error("结案失败，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "结案失败");
            }

            //4. 记录结案日志
            Map<Long, String> userNameMap = eiamRemoteGateway.getNameByMid(Arrays.asList(
                    consultOrder.getCreateMid(),
                    soIn.getOperateMid()));
            complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(buildComplaintFollowProcess(consultOrder.getConsultNo(),soIn, userNameMap));

            //发送mq
            FinishOrderStatusMqMessageGoIn finishMrOrderStatusMqMessageBO = FinishOrderStatusMqMessageGoIn
                    .builder()
                    .operateType(WorkFinishTypeEnum.COMPLETED.getCode())
                    .workNo(soIn.getConsultNo())
                    .workType(31)
                    .build();
            boolean sendFinishMq = rmqGateway.mrOrderStatusFinishMessage(finishMrOrderStatusMqMessageBO);
            if (!sendFinishMq) {
                log.error("onStatusChangeTransactionCommitAfter 发送mq失败");
            }
            //5. 返回结果
            ConsultFinishSoOut soOut = new ConsultFinishSoOut();
            soOut.setResult("success");
            log.info("结案成功，consultNo:{}", soIn.getConsultNo());
            return soOut;
        } catch (BusinessException e) {
            log.error("结案失败，soIn:{}", GsonUtil.toJson(soIn), e);
            throw e;
        } catch (Exception e) {
            log.error("结案异常，soIn:{}", GsonUtil.toJson(soIn), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "结案异常");
        }
    }

    /**
     * 构建更新咨询单参�?
     */
    private UcConsultOrderUpdateGoIn buildUpdateConsultOrderGoIn(OrderEditConsultSoIn soIn) {
        UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                .consultNo(soIn.getConsultNo())
                .build();
        
        // 如果有扩展信息，更新相关字段
        if (soIn.getExpandSoIn() != null) {
            updateGoIn.setPriority(soIn.getExpandSoIn().getPriority() != null ?
                    soIn.getExpandSoIn().getPriority() : null);
            updateGoIn.setProblemDesc(soIn.getExpandSoIn().getRemark());
            updateGoIn.setConsultType(soIn.getExpandSoIn().getEnquireType());
            updateGoIn.setOrgId(soIn.getExpandSoIn().getOrgId());
            updateGoIn.setExpectingBackTime(soIn.getExpandSoIn().getExpectedTouchTime() != null ?
                    new Date(soIn.getExpandSoIn().getExpectedTouchTime()*1000) : DateUtil.getDefaultTime());
        }
        
        return updateGoIn;
    }

    /**
     * 构建跟进记录
     */
    private ComplaintFollowProcessGoIn buildComplaintFollowProcess(String consultNo, Long createMid, ProcessTypeEnum processTypeEnum) {
        // 构建记录信息
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .operateMid(String.valueOf(createMid))
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .operatePositionId("0")
                .operatePositionName("线上客诉处理专家")
                .build();
        
        // 构建跟进记录
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(consultNo)
                .processType(processTypeEnum.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RemindOrderSoOut remindOrder(RetailRemindOrderSoIn soIn) {
        RemindOrderSoOut soOut = new RemindOrderSoOut();
        // 加锁
        String lockKey = "remindOrder:" + RedisUtil.generateRemindKey(soIn.getDrNo());
        if (BooleanUtils.isFalse(RedisUtil.tryLock(lockKey))) {
            log.info("当前lockKey正被锁，lockkey;{}, drNo:{}", lockKey, soIn.getDrNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "正在催单中，请稍后再�?);
        }
        // 幂等
        try {
            UcConsultOrderGoIn queryGoIn = new UcConsultOrderGoIn();
            queryGoIn.setConsultNo(soIn.getDrNo());
            UserConsultOrderInfo consultOrder = userConsultOrderGateway.searchUserConsultOrderInfo(queryGoIn);
            if (Objects.isNull(consultOrder)) {
                log.error("咨询单不存在，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "该咨询单" + soIn.getDrNo() + "不存�?);
            }
            // 获取登陆人信�?
            EmployeeListGoIn eiamGoIn =
                    EmployeeListGoIn.builder().miIdList(Collections.singletonList(Long.valueOf(soIn.getReminderMid())))
                            .build();
            List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(eiamGoIn);
            // 用stream将员工列表转换为map
            Map<String, EmployeeInfoGoOut> employeeMap =
                    employeeList.stream().collect(Collectors.toMap(e -> e.getMiId().toString(), Function.identity()));
            soIn.setReminderName(
                    employeeMap.containsKey(soIn.getReminderMid()) ? employeeMap.get(soIn.getReminderMid()).getName() :
                            "");

            // 更新催单次数
            UcConsultOrderUpdateGoIn updateGoIn = UcConsultOrderUpdateGoIn.builder()
                    .consultNo(soIn.getDrNo())
                    .updateTime(new Date())
                    .reminderTimes(consultOrder.getReminderTimes() + 1)
                    .build();
            int updateResult = userConsultOrderGateway.updateOrderSelective(updateGoIn);
            DateTime reminderDate = cn.hutool.core.date.DateUtil.date();

            // 构建催单信息
            ComplaintFollowProcessGoIn recordInfoGoIn =
                    buildRemindRecordInfo(soIn, reminderDate);

            log.info("UserConsultOperateServiceImpl.remindOrder recordInfoGoIn:{}",
                    GsonUtil.toJson(recordInfoGoIn));

            // 持久化派单记�?
            Boolean insertRecords = complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(
                    recordInfoGoIn);
            if (updateResult > 0 && insertRecords) {
                // 发送催单消�?
                UcConsultOrderGoIn goInNew = new UcConsultOrderGoIn();
                goInNew.setConsultNo(soIn.getDrNo());
                UserConsultOrderInfo userConsultOrderInfo =
                        userConsultOrderGateway.searchUserConsultOrderInfo(goInNew);
                sendMsg(userConsultOrderInfo,PushConstant.CONSULT_REMIND);
                soOut.setResult("SUCCESS");
                return soOut;
            } else {
                log.error("更新失败，soIn:{}", GsonUtil.toJson(soIn));
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "更新失败");
            }
        } finally {
            RedisUtil.unlock(lockKey);
        }
    }

    /**
     * 构建催单记录
     *
     * @param soIn         催单入参
     * @param reminderDate
     * @return 催单记录
     */
    private static ComplaintFollowProcessGoIn buildRemindRecordInfo(RetailRemindOrderSoIn soIn, DateTime reminderDate) {
        // 构建跟进信息
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .remindOrderTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .orderReminderMid(soIn.getReminderMid())
                .orderReminderName(soIn.getReminderName())
                .orderRemindInfo(soIn.getOrderRemindInfo())
                .build();
            recordInfoGoIn.setOperateMid(soIn.getReminderMid());
            recordInfoGoIn.setOperateName(soIn.getReminderName());
            recordInfoGoIn.setOperatePositionId("0"); // 岗位默认设为 线上客诉处理专家
            recordInfoGoIn.setOperatePositionName(POSITION_NAME);
            recordInfoGoIn.setOperateTime(DateUtil.getTimeStrByDate(reminderDate));
        return ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getDrNo())
                .processType(ProcessTypeEnum.REMIND.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
    }

}
