package com.wt.complaint.manage.domain.aggregation;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.wt.car.soc.api.dto.FieldValue;
import com.wt.car.soc.api.dto.FieldValueDto;
import com.wt.car.soc.api.dto.GroupValueDto;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.MediaInvolvedEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.ReviewedEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.enums.TagTypeEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.service.converter.OrderOperationConverter;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.KeyWordConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.GsonUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@Slf4j
public class ComplaintOrderAggregation {

    private ComplaintOrderInfoGoIn complaintOrderInfoGoIn;

    private ComplaintFollowProcessGoIn complaintFollowProcessGoIn;

    private ComplaintTagSoIn tagSoIn;

    /**
     * 投诉升级参数
     */
    private RecordInfoGoIn upgradeInfo;

    /**
     * 字段更新描述格式：由"旧�?更新�?新�?
     */
    private static final String UPDATE_FORMAT = "由\"%s\"更新为\"%s\"";

    /**
     * 字段更新描述格式（从空值更新）：由" "更新�?新�?
     */
    private static final String UPDATE_FROM_EMPTY_FORMAT = "由\" \"更新为\"%s\"";


    /**
     * 接单
     *
     * @param soIn
     */
    public void pickUpComplaintOrder(OrderPickUpSoIn soIn) {
        // 校验待接单是否存�?
        if (Objects.isNull(this.complaintOrderInfoGoIn)) {
            log.error("pickUpComplaintOrder complaintOrderInfoGoIn is null, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        // 校验当前状态是否可接单
        Integer status = this.complaintOrderInfoGoIn.getStatus();
        if (!ComplaintStatusEnum.PENDING_ORDER.getCode().equals(status)) {
            log.error("当前状态无法接�? complaintNo:{}, status:{}", soIn.getComplaintNo(), status);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前客诉单无法接�? complaintNo:{}, status:{}",
                    soIn.getComplaintNo(), status);
        }
        // 构建接单后的订单信息
        ComplaintOrderInfoGoIn pickUpOrderGoIn = new ComplaintOrderInfoGoIn();
        pickUpOrderGoIn.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
        pickUpOrderGoIn.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        pickUpOrderGoIn.setOperatorMid(Long.valueOf(soIn.getPickUpMid()));
        this.complaintOrderInfoGoIn = pickUpOrderGoIn;
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .pickUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .orderReceiverMid(soIn.getPickUpMid())
                .orderReceiverName(soIn.getPickUpName())
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();
    }

    /**
     * 新建客诉�?
     *
     * @param soIn
     */
    public void createComplaintOrder(ComplaintOrderCreateSoIn soIn) {
        this.complaintOrderInfoGoIn = new ComplaintOrderInfoGoIn();
        ComplaintOrderCreateExpandSoIn expandSoIn = soIn.getExpandSoIn();
        List<TemplateStructSoIn> complaintInfo = expandSoIn.getComplaintInfo();
        extractExpandInfo(complaintInfo);
        this.complaintOrderInfoGoIn.setIdempotentKey(soIn.getIdempotentId());
        this.complaintOrderInfoGoIn.setVid(soIn.getVid());
        this.complaintOrderInfoGoIn.setSuperTicketNo(soIn.getSuperTicketNo());
        this.complaintOrderInfoGoIn.setSoNo(soIn.getSoNo());
        this.complaintOrderInfoGoIn.setCarNo(expandSoIn.getCarNo());
        this.complaintOrderInfoGoIn.setStatus(
                this.complaintOrderInfoGoIn.getOnlyView() == 0 ? ComplaintStatusEnum.PENDING_ORDER.getCode() :
                        ComplaintStatusEnum.FINISH_COMPLETE.getCode());
        this.complaintOrderInfoGoIn.setComplaintNo(soIn.getComplaintNo());
        this.complaintOrderInfoGoIn.setCustomerServiceMid(Long.valueOf(expandSoIn.getCustomerServiceMid()));
        this.complaintOrderInfoGoIn.setContactNameC(KeyCenterUtil.encrypt(soIn.getContactName()));
        this.complaintOrderInfoGoIn.setContactPhoneC(KeyCenterUtil.encrypt(soIn.getContactTel()));
        this.complaintOrderInfoGoIn.setContactPhoneMd5(KeyCenterUtil.md5(soIn.getContactTel()));
        this.complaintOrderInfoGoIn.setContactGender(soIn.getContactTitle());
        this.complaintOrderInfoGoIn.setComplaintContent(GsonUtil.toJson(complaintInfo));
        this.complaintOrderInfoGoIn.setTestTag(soIn.getTestTag());
        this.complaintOrderInfoGoIn.setCarType(soIn.getCarType());
        this.complaintOrderInfoGoIn.setCreateSource(soIn.getCreateSource());
        // soIn的createMid为超级工单的创建人，本工单为内部工单，客诉单的创建人取跟进客服mid
        this.complaintOrderInfoGoIn.setCreateMid(Long.valueOf(expandSoIn.getCustomerServiceMid()));
        if (StringUtils.isNotEmpty(soIn.getVin()) && soIn.getVin().length() >= KeyWordConstant.VIN_SUFFIX_LEN) {
            this.complaintOrderInfoGoIn.setVinSufix(
                    soIn.getVin().substring(soIn.getVin().length() - KeyWordConstant.VIN_SUFFIX_LEN));
        }
        if (StringUtils.isNotEmpty(soIn.getContactTel()) &&
                soIn.getContactTel().length() >= KeyWordConstant.PHONE_SUFFIX_LEN) {
            this.complaintOrderInfoGoIn.setContactPhoneSufix(
                    Integer.valueOf(soIn.getContactTel()
                            .substring(soIn.getContactTel().length() - KeyWordConstant.PHONE_SUFFIX_LEN)));
        }


        boolean productComplaintFree = Objects.equals(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode(),
                this.getComplaintOrderInfoGoIn().getComplaintType());

        boolean productRiskComplaintFree = Objects.equals(ComplaintTypeEnum.PRODUCT_RISK.getCode(),
                this.getComplaintOrderInfoGoIn().getComplaintType());

        boolean serviceStoreSourceFree = Objects.equals(CreateSourceEnum.STORE.getCode(),
                this.getComplaintOrderInfoGoIn().getCreateSource());

        if (productComplaintFree || productRiskComplaintFree || serviceStoreSourceFree) {
            ComplaintTagSoIn tempTagSoIn = new ComplaintTagSoIn();
            tempTagSoIn.setComplaintNo(soIn.getComplaintNo());
            tempTagSoIn.setTagType(TagTypeEnum.COMPLAINT_RATE_ASSESSMENT_FREE.getCode());
            tempTagSoIn.setIsDeleted(0);
            this.tagSoIn = tempTagSoIn;
        }
        // 新建客诉单，默认reviewed为否
        this.complaintOrderInfoGoIn.setReviewed(ReviewedEnum.NO.getCode());
        logInfo();
    }

    public void updateHandler(OrderUpdateHandlerSoIn soIn) {
        // 校验待接单是否存�?
        if (Objects.isNull(this.complaintOrderInfoGoIn)) {
            log.error("updateHandler complaintOrderInfoGoIn is null, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        // 校验当前状态是否可接单
        Integer status = this.complaintOrderInfoGoIn.getStatus();
        if (Objects.equals(status, ComplaintStatusEnum.FINISH_COMPLETE.getCode())) {
            log.error("complaintOrderInfoGoIn has finished, complaintNo:{}, status:{}", soIn.getComplaintNo(), status);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR,
                    "complaintOrderInfoGoIn has finished, complaintNo:{}, status:{}", soIn.getComplaintNo(), status);
        }
        if (Objects.equals(status, ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode())) {
            log.error("complaintOrderInfoGoIn should wait to be audited, complaintNo:{}, status:{}",
                    soIn.getComplaintNo(), status);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR,
                    "complaintOrderInfoGoIn should wait to be audited, complaintNo:{}, status:{}",
                    soIn.getComplaintNo(), status);
        }
        // 构建接单后的订单信息
        ComplaintOrderInfoGoIn updateHandlerInfoGoIn = new ComplaintOrderInfoGoIn();
        updateHandlerInfoGoIn.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
        updateHandlerInfoGoIn.setOperatorMid(Long.valueOf(soIn.getHandlerMid()));
        updateHandlerInfoGoIn.setUpdateTime(new Date());
        // 若状态为待接单状态，则更新为待首�?
        if (ComplaintStatusEnum.PENDING_ORDER.getCode().equals(status)) {
            updateHandlerInfoGoIn.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        }
        this.complaintOrderInfoGoIn = updateHandlerInfoGoIn;
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .dispatchTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .dispatcherMid(soIn.getDispatcherMid())
                .dispatcherName(soIn.getDispatcherName())
                .orderReceiverMid(soIn.getHandlerMid())
                .orderReceiverName(soIn.getHandlerName())
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.DISPATCH_ORDER.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();
    }

    @Deprecated
    public void addFollowUpRecord(OrderAddFollowUpRecordSoIn soIn) {
        // 校验待接单是否存�?
        if (Objects.isNull(this.complaintOrderInfoGoIn)) {
            log.error("addFollowUpRecord complaintOrderInfoGoIn is null, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        Boolean isFirstResp = ComplaintStatusEnum.canFirstResponse(this.complaintOrderInfoGoIn.getStatus());
        if (isFirstResp) {
            // 构建投诉订单信息
            ComplaintOrderInfoGoIn updateHandlerInfoGoIn = new ComplaintOrderInfoGoIn();
            updateHandlerInfoGoIn.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
            updateHandlerInfoGoIn.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
            updateHandlerInfoGoIn.setFirstResponseTime(new Date());
            this.complaintOrderInfoGoIn = updateHandlerInfoGoIn;
        } else {
            // 非首响，无需修改客诉单状�?
            log.info("complaintOrderInfoGoIn status is not firstResp, complaintNo:{}, status:{}", soIn.getComplaintNo(),
                    this.complaintOrderInfoGoIn.getStatus());
        }
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .followUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .followUpMid(soIn.getFollowUpMid())
                .followUpName(soIn.getFollowUpName())
                .followUpContent(soIn.getFollowInfo())
                .attachments(OrderOperationConverter.INSTANCE.toAttachmentGoIn(soIn.getAttachmentList()))
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(isFirstResp ? ProcessTypeEnum.FIRST_RESPONSE.getProcessCode() :
                        ProcessTypeEnum.ADD_FOLLOW_RECORD.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        if (!isFirstResp) {
            this.complaintOrderInfoGoIn = null;
        }
        logInfo();
    }

    public void addFollowUpRecordV2(OrderAddFollowUpRecordSoInV2 soIn) {
        // 校验待接单是否存�?
        if (Objects.isNull(this.complaintOrderInfoGoIn)) {
            log.error("addFollowUpRecordV2 complaintOrderInfoGoIn is null, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        Boolean isFirstResp = ComplaintStatusEnum.canFirstResponse(this.complaintOrderInfoGoIn.getStatus());
        if (isFirstResp) {
            // 构建投诉订单信息
            ComplaintOrderInfoGoIn updateHandlerInfoGoIn = new ComplaintOrderInfoGoIn();
            updateHandlerInfoGoIn.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
            updateHandlerInfoGoIn.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
            updateHandlerInfoGoIn.setFirstResponseTime(new Date());
            this.complaintOrderInfoGoIn = updateHandlerInfoGoIn;
        } else {
            // 非首响，无需修改客诉单状�?
            log.info("addFollowUpRecordV2 complaintOrderInfoGoIn status is not firstResp, complaintNo:{}, status:{}",
                    soIn.getComplaintNo(), this.complaintOrderInfoGoIn.getStatus());
        }
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .followUpTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .followUpMid(soIn.getFollowUpMid())
                .followUpName(soIn.getFollowUpName())
                .followUpContent(soIn.getFollowInfo())
                .attachments(OrderOperationConverter.INSTANCE.toAttachmentGoIn(soIn.getAttachmentList()))
                .mileage(Double.valueOf(soIn.getMileage()))
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(isFirstResp ? ProcessTypeEnum.FIRST_RESPONSE.getProcessCode() :
                        ProcessTypeEnum.ADD_FOLLOW_RECORD.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        if (!isFirstResp) {
            this.complaintOrderInfoGoIn = null;
        }
        logInfo();
    }

    public void addDistributionRecord(OrderAddDistributionRecordSoIn soIn) {
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .distributionId(soIn.getDistributionId())
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.SEND_INTEGRAL.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();
    }

    public void remindOrder(OrderRemindSoIn soIn) {
        // 校验待接单是否存�?
        if (Objects.isNull(this.complaintOrderInfoGoIn)) {
            log.error("remindOrder complaintOrderInfoGoIn is null, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        // 构建催单信息
        ComplaintOrderInfoGoIn remindOrderInfo = new ComplaintOrderInfoGoIn();
        remindOrderInfo.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
        remindOrderInfo.setReminderTimes(this.complaintOrderInfoGoIn.getReminderTimes() + 1);
        this.complaintOrderInfoGoIn = remindOrderInfo;
        // 构建跟进信息
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .remindOrderTime(DateUtil.getTimeStrByTimeStampMS(System.currentTimeMillis()))
                .orderReminderMid(soIn.getReminderMid())
                .orderReminderName(soIn.getReminderName())
                .orderRemindInfo(soIn.getOrderRemindInfo())
                .build();
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.REMIND.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();
    }

    private void logInfo() {
        log.info("complaintOrderInfoGoIn:{}",
                Objects.isNull(complaintOrderInfoGoIn) ? "" : GsonUtil.toJson(complaintOrderInfoGoIn));
        log.info("complaintFollowProcessGoIn:{}",
                Objects.isNull(complaintFollowProcessGoIn) ? "" : GsonUtil.toJson(complaintFollowProcessGoIn));
        log.info("tagSoIn:{}", Objects.isNull(tagSoIn) ? "" : GsonUtil.toJson(tagSoIn));
    }

    private void extractExpandInfo(List<TemplateStructSoIn> complaintInfo) {
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
    }

    /**
     * 升级投诉�?
     *
     * @param soIn 投诉单升级参�?
     */
    public void upgradeComplaintOrder(ComplaintOrderUpgradeSoIn soIn) {
        if (!ComplaintTypeEnum.PRODUCT_RISK.getCode().equals(complaintOrderInfoGoIn.getComplaintType())) {
            log.warn("complaintOrderInfoGoIn is not product risk, complaintNo:{}", soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前投诉单不是产品风险分类无法升�?);
        }
        // 升级状态校验：1-待接单�?5-申请改派门店待审核�?0-待首响�?0-待申请结�?
        if (!ComplaintStatusEnum.PENDING_ORDER.getCode().equals(complaintOrderInfoGoIn.getStatus())
            && !ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode().equals(complaintOrderInfoGoIn.getStatus())
            && !ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode().equals(complaintOrderInfoGoIn.getStatus())
            && !ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode().equals(complaintOrderInfoGoIn.getStatus())) {
            log.warn("complaintOrderInfoGoIn status={} 不是1-待接单�?5-申请改派门店待审核�?0-待首响�?0-待申请结�? complaintNo:{}", complaintOrderInfoGoIn.getStatus(), soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前投诉单状态为1-待接单�?5-申请改派门店待审核�?0-待首响�?0-待申请结案时才支持升�?);
        }

        // 保存原始投诉类型
        Integer originalComplaintType = complaintOrderInfoGoIn.getComplaintType();

        // 构建接单后的订单信息
        ComplaintOrderInfoGoIn updateInfoGoIn = new ComplaintOrderInfoGoIn();
        // 如果当前产品风险类客诉单状态是待申请结案，需要扭转到待首�?
        if (ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode().equals(complaintOrderInfoGoIn.getStatus())) {
            updateInfoGoIn.setStatus(ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode());
        }
        updateInfoGoIn.setComplaintNo(this.complaintOrderInfoGoIn.getComplaintNo());
        updateInfoGoIn.setUpgradeTime(new Date());
        // 数据库投诉类型字段complaintContent也跟随变�?
        updateInfoGoIn.setComplaintType(soIn.getTargetType());
        String complaintContent = handleComplaintContentUpdate(soIn);
        updateInfoGoIn.setComplaintContent(complaintContent);

        this.complaintOrderInfoGoIn = updateInfoGoIn;
        // 构建跟进记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .upgradeTime(DateUtil.getTimeStrByDate(updateInfoGoIn.getUpgradeTime()))
                .upgraderMid(soIn.getOperatorMid())
                .upgraderName(soIn.getOperatorName())
                .originalTypeDesc(ComplaintTypeEnum.getDescByCode(originalComplaintType))
                .targetTypeDesc(ComplaintTypeEnum.getDescByCode(soIn.getTargetType()))
                .upgradeReason(soIn.getUpgradeReason())
                .build();
        this.upgradeInfo = recordInfoGoIn;
        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.UPGRADE_COMPLAINT.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();

    }

    /**
     * 处理模板字段更新
     *
     * @param soIn 投诉入参
     */
    private String handleComplaintContentUpdate(ComplaintOrderUpgradeSoIn soIn) {
        ComplaintTypeEnum complaintTypeEnum = ComplaintTypeEnum.getEnumByCode(soIn.getTargetType());
        if (complaintTypeEnum == null || complaintTypeEnum == ComplaintTypeEnum.PRODUCT_RISK) {
            log.warn("complaintOrderInfoGoIn, complaintNo: {}, targetType: {}", soIn.getComplaintNo(),
                    soIn.getTargetType());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "升级目标投诉类型不能为产品风�?);
        }
        
        String originalContent = complaintOrderInfoGoIn.getComplaintContent();
        // 修改complaint_content里面的投诉分类系统字�?
        try {
            List<GroupValueDto> fieldGroups = GsonUtil.fromListJson(originalContent, GroupValueDto.class);
            if (CollUtil.isEmpty(fieldGroups) || CollUtil.isEmpty(fieldGroups.get(0).getFields())) {
                log.info("handleComplaintContentUpdate complaintContent={}, fields={}",
                        originalContent, 
                        CollUtil.isEmpty(fieldGroups) ? "empty groups" : GsonUtil.toJson(fieldGroups.get(0).getFields()));
                return originalContent;
            }
            
            List<FieldValueDto> fields = fieldGroups.get(0).getFields();
            fields.forEach((FieldValueDto field) -> {
                if (Objects.equals(field.getFieldCode(), ComplaintInfoConstant.COMPLAINT_TYPE)) {
                    FieldValue fieldValue = new FieldValue();
                    fieldValue.setCode(String.valueOf(complaintTypeEnum.getCode()));
                    fieldValue.setDesc(complaintTypeEnum.getDesc());
                    field.setValue(Lists.newArrayList(fieldValue));
                    log.info("handleComplaintContentUpdate fieldValue={}", GsonUtil.toJson(fieldValue));
                }
            });
            return GsonUtil.toJson(fieldGroups);
        } catch (Exception e) {
            log.warn("upgradeComplaintOrder parse complaintContent complaintContent={} error:",
                    originalContent, e);
            return originalContent;
        }
    }

    /**
     * 编辑客诉�?
     *
     * @param soIn 编辑入参
     */
    public void editComplaint(OrderEditComplaintSoIn soIn) {

        // 构建更新后的订单信息
        ComplaintOrderInfoGoIn updateInfoGoIn = new ComplaintOrderInfoGoIn();
        updateInfoGoIn.setComplaintNo(soIn.getComplaintNo());

        // 更新风险等级
        // riskLevel 现在直接�?code (1, 2, 3, 4)，不再需要从描述转换
        if (StringUtils.isNotBlank(soIn.getRiskLevel())) {
            updateInfoGoIn.setRiskLevel(Integer.valueOf(soIn.getRiskLevel()));
        }

        // 更新是否涉媒
        if (StringUtils.isNotBlank(soIn.getMediaInvolved())) {
            updateInfoGoIn.setMediaInvolved(Integer.valueOf(soIn.getMediaInvolved()));
        }

        // 更新涉媒链接 - 修复：允许清空涉媒链接（当mediaLink不为null时，无论是否为空字符串都更新�?
        if (soIn.getMediaLink() != null) {
            updateInfoGoIn.setMediaLink(soIn.getMediaLink());
        }

        // 更新complaint_content中的字段（complaint、riskLevel、mediaInvolved、mediaLink�?
        if (soIn.getComplaint() != null || StringUtils.isNotBlank(soIn.getRiskLevel())
                || StringUtils.isNotBlank(soIn.getMediaInvolved()) || soIn.getMediaLink() != null) {
            String updatedComplaintContent = handleComplaintContentUpdateForEdit(soIn);
            updateInfoGoIn.setComplaintContent(updatedComplaintContent);
        }

        // 构建四个变更字段
        String complaintTypeChange = buildComplaintTypeChange(soIn.getComplaint());
        String riskLevelChange = buildRiskLevelChange(soIn.getRiskLevel());
        String mediaInvolvedChange = buildMediaInvolvedChange(soIn.getMediaInvolved());
        String mediaLinkChange = buildMediaLinkChange(soIn.getMediaLink());

        // 判断是否有变更项
        if (StringUtils.isBlank(complaintTypeChange) && StringUtils.isBlank(riskLevelChange)
                && StringUtils.isBlank(mediaInvolvedChange) && StringUtils.isBlank(mediaLinkChange)) {
            // 四个参数都没有发生变更，设置为null并拦截后续事务操�?
            log.info("编辑客诉单没有变更项，complaintNo:{}", soIn.getComplaintNo());
            this.complaintOrderInfoGoIn = null;
            this.complaintFollowProcessGoIn = null;
            logInfo();
            return;
        }

        // 替换order,从旧order改为需要更新的属�?
        this.complaintOrderInfoGoIn = updateInfoGoIn;

        // 构建操作记录
        RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                .operateMid(String.valueOf(soIn.getOperateMid()))
                .operateName(soIn.getOperateName())
                .operateTime(DateUtil.getTimeStrByDate(new Date()))
                .complaintTypeChange(complaintTypeChange)
                .riskLevelChange(riskLevelChange)
                .mediaInvolvedChange(mediaInvolvedChange)
                .mediaLinkChange(mediaLinkChange)
                .build();

        this.complaintFollowProcessGoIn = ComplaintFollowProcessGoIn.builder()
                .complaintNo(soIn.getComplaintNo())
                .processType(ProcessTypeEnum.COMPLAINT_INFO_UPDATE.getProcessCode())
                .processContent(GsonUtil.toJson(recordInfoGoIn))
                .build();
        logInfo();
    }

    /**
     * 处理编辑时的complaint_content更新
     */
    private String handleComplaintContentUpdateForEdit(OrderEditComplaintSoIn soIn) {
        FieldValueSoIn complaint = soIn.getComplaint();
        if (complaintOrderInfoGoIn == null) {
            log.error("handleComplaintContentUpdateForEdit complaintOrderInfoGoIn is null");
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "编辑客诉单失�?客诉单信息为�?);
        }
        try {
            List<GroupValueDto> oldGroups =
                    GsonUtil.fromListJson(complaintOrderInfoGoIn.getComplaintContent(), GroupValueDto.class);
            if (CollUtil.isEmpty(oldGroups)) {
                log.info("handleComplaintContentUpdateForEdit complaintContent={}",
                        complaintOrderInfoGoIn.getComplaintContent());
                return complaintOrderInfoGoIn.getComplaintContent();
            }
            List<FieldValueDto> fields = oldGroups.get(0).getFields();
            if (CollUtil.isEmpty(fields)) {
                log.info("handleComplaintContentUpdateForEdit fields is empty, complaintContent={}",
                        complaintOrderInfoGoIn.getComplaintContent());
                return complaintOrderInfoGoIn.getComplaintContent();
            }
            fields.forEach(field -> applyFieldUpdateForEdit(field, soIn, complaint));
            return GsonUtil.toJson(oldGroups);
        } catch (Exception e) {
            log.error("editComplaint parse complaintContent complaintContent={} error:",
                    complaintOrderInfoGoIn.getComplaintContent(), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "编辑客诉单失�?投诉场景字段异常");
        }
    }

    private void applyFieldUpdateForEdit(FieldValueDto field, OrderEditComplaintSoIn soIn, FieldValueSoIn complaint) {
        if (Objects.equals(field.getFieldCode(), ComplaintInfoConstant.COMPLAINT_SCENE)) {
            FieldValue fieldValue = new FieldValue();
            fieldValue.setCode(complaint.getCode());
            fieldValue.setDesc(complaint.getDesc());
            fieldValue.setPathId(complaint.getPathId());
            fieldValue.setPathName(complaint.getPathName());
            field.setValue(Lists.newArrayList(fieldValue));
            log.info("handleComplaintContentUpdateForEdit fieldValue={}", GsonUtil.toJson(fieldValue));
            return;
        }
        if (Objects.equals(field.getFieldCode(), ComplaintInfoConstant.RISK_LEVEL)
                && StringUtils.isNotBlank(soIn.getRiskLevel())) {
            FieldValue fieldValue = new FieldValue();
            fieldValue.setCode(soIn.getRiskLevel());
            fieldValue.setDesc(RiskLevelEnum.getDescByCode(Integer.valueOf(soIn.getRiskLevel())));
            field.setValue(Lists.newArrayList(fieldValue));
            log.info("handleComplaintContentUpdateForEdit riskLevel fieldValue={}", GsonUtil.toJson(fieldValue));
            return;
        }
        if (Objects.equals(field.getFieldCode(), ComplaintInfoConstant.MEDIA_INVOLVED)
                && StringUtils.isNotBlank(soIn.getMediaInvolved())) {
            FieldValue fieldValue = new FieldValue();
            fieldValue.setCode(soIn.getMediaInvolved());
            fieldValue.setDesc(MediaInvolvedEnum.getDescByCodeStr(soIn.getMediaInvolved()));
            field.setValue(Lists.newArrayList(fieldValue));
            log.info("handleComplaintContentUpdateForEdit mediaInvolved fieldValue={}", GsonUtil.toJson(fieldValue));
            return;
        }
        if (Objects.equals(field.getFieldCode(), ComplaintInfoConstant.MEDIA_LINK) && soIn.getMediaLink() != null) {
            if (StringUtils.isNotBlank(soIn.getMediaLink())) {
                FieldValue fieldValue = new FieldValue();
                fieldValue.setCode("");
                fieldValue.setDesc(soIn.getMediaLink());
                field.setValue(Lists.newArrayList(fieldValue));
                log.info("handleComplaintContentUpdateForEdit mediaLink fieldValue={}", GsonUtil.toJson(fieldValue));
            } else {
                field.setValue(Lists.newArrayList());
                log.info("handleComplaintContentUpdateForEdit mediaLink cleared");
            }
        }
    }

    /**
     * 构建投诉场景变更描述
     */
    private String buildComplaintTypeChange(FieldValueSoIn newComplaint) {
        if (newComplaint == null) {
            return null;
        }
        FieldValueSoIn oldComplaint =
                ParseComplaintContentUtil.parseComplaintFieldValue(complaintOrderInfoGoIn.getComplaintContent());
        if (oldComplaint == null) {

            return String.format(UPDATE_FROM_EMPTY_FORMAT, newComplaint.getPathName());
        }
        if (!Objects.equals(oldComplaint.getCode(), newComplaint.getCode())) {
            return String.format(UPDATE_FORMAT, oldComplaint.getPathName(), newComplaint.getPathName());
        }
        return null;
    }

    /**
     * 构建风险等级变更描述
     */
    private String buildRiskLevelChange(String newRiskLevel) {
        if (StringUtils.isBlank(newRiskLevel)) {
            return null;
        }
        String oldRiskLevel = String.valueOf(complaintOrderInfoGoIn.getRiskLevel());
        if (!Objects.equals(oldRiskLevel, newRiskLevel)) {
            return String.format(UPDATE_FORMAT,
                    RiskLevelEnum.getDescByCode(complaintOrderInfoGoIn.getRiskLevel()),
                    RiskLevelEnum.getDescByCode(Integer.valueOf(newRiskLevel)));
        }
        return null;
    }

    /**
     * 构建是否涉媒变更描述
     */
    private String buildMediaInvolvedChange(String newMediaInvolved) {
        if (StringUtils.isBlank(newMediaInvolved)) {
            return null;
        }
        Integer oldMediaInvolved = complaintOrderInfoGoIn.getMediaInvolved();
        String oldDesc = MediaInvolvedEnum.getDescByCode(oldMediaInvolved);
        String newDesc = MediaInvolvedEnum.getDescByCodeStr(newMediaInvolved);
        if (!Objects.equals(oldDesc, newDesc)) {
            return String.format(UPDATE_FORMAT, oldDesc, newDesc);
        }
        return null;
    }

    /**
     * 构建涉媒链接变更描述
     * 修复：支持清空涉媒链接的场景
     */
    private String buildMediaLinkChange(String newMediaLink) {
        // 如果newMediaLink为null，说明前端没有传这个字段，不需要更�?
        if (newMediaLink == null) {
            return null;
        }
        String oldMediaLink = complaintOrderInfoGoIn.getMediaLink();
        // 修复：当新值和旧值不同时（包括清空的情况），生成变更记录
        if (!Objects.equals(oldMediaLink, newMediaLink)) {
            String oldValue = StringUtils.isBlank(oldMediaLink) ? "" : oldMediaLink;
            String newValue = StringUtils.isBlank(newMediaLink) ? "" : newMediaLink;
            return String.format(UPDATE_FORMAT, oldValue, newValue);
        }
        return null;
    }

}
