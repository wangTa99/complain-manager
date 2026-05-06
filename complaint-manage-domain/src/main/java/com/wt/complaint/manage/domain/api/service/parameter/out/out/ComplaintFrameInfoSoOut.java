package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.DetailTabEnum;
import com.wt.complaint.manage.api.model.enums.DoneYNEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.converter.CarTagConvert;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.proretail.newcommon.param.BaseParamModelSoOut;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import com.xiaomi.newretail.common.tools.utils.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ComplaintFrameInfoSoOut extends BaseParamModelSoOut {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 工单�?
     */
    private String stNo;

    /**
     * 客诉单状�?
     */
    private Integer complaintStatus;

    /**
     * 客诉单状态名�?
     */
    private String complaintStatusName;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 风险等级
     */
    private Integer riskLevel;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 车架号Vin
     */
    private String vin;

    /**
     * 车辆VID
     */
    private String vid;

    /**
     * 催单次数
     */
    private Integer remindCount;

    /**
     * 客诉标签列表 投诉率免考核（COMPLAINT_RATE_ASSESSMENT_FREE�?72H无法结案(FINISH_72H_ASSESSMENT_FREE) 首响超时(FIRST_RESPONSE_TIMEOUT) 结案超时(FINISH_TIMEOUT)
     */
    private List<String> complaintTagList;

    /**
     * 客诉进度条信�?
     */
    private List<StatusData> statusBar;

    /**
     * 车辆信息
     */
    private CarInfo carInfo;

    /**
     * 车辆质保相关标签
     */
    private WarrantyInfo warrantyInfo;

    /**
     * 客诉单基本信�?
     */
    private ComplaintOrderInfo complaintOrderInfo;

    /**
     * 客诉单详情页tab展示列表
     */
    private List<TabData> tabDataList;

    /**
     * 用户操作按钮
     */
    private UserActionAuth userActionAuth;

    /**
     * 是否只查�?0�?�?
     */
    private Integer onlyView;

    /**
     * 审批日志列表
     */
    private List<AuditLog> auditLogList;

    /**
     * 创建来源: 1-服务门店, 2-线上客服
     */
    private Integer createSource;

    /**
     * 创建来源描述
     */
    private String createSourceDesc;

    public void fillBaseInfo(ComplaintOrderInfoGoIn orderInfo) {
        this.complaintNo = orderInfo.getComplaintNo();
        this.soNo = orderInfo.getSoNo();
        this.stNo = orderInfo.getSuperTicketNo();
        this.carNo = orderInfo.getCarNo();
        this.riskLevel = orderInfo.getRiskLevel();
        this.complaintStatus = orderInfo.getStatus();
        this.complaintStatusName = ComplaintStatusEnum.getDescByCode(orderInfo.getStatus());
        this.contactName = KeyCenterUtil.decrypt(orderInfo.getContactNameC());
        this.contactPhone = KeyCenterUtil.decrypt(orderInfo.getContactPhoneC());
        this.remindCount = orderInfo.getReminderTimes();
        this.onlyView = orderInfo.getOnlyView();
        this.createSource = orderInfo.getCreateSource();
        this.createSourceDesc = CreateSourceEnum.getDescByCode(orderInfo.getCreateSource());
    }

    public void fillCarInfo(List<CarInfoGoOut> carInfoGoOutList, CarUserAggGoOut carUserAgg, GetDynamicInfoResponseGoOut carDynamicInfo, BatchMemberInfoBO memberInfoBO) {
        CarInfo carInfoTemp = new CarInfo();
        this.carInfo = carInfoTemp;
        // 车基础信息组装
        Map<String, CarInfoGoOut> carMap = carInfoGoOutList.stream().collect(Collectors.toMap(CarInfoGoOut::getVid, e -> e, (k1, k2) -> k1));
        CarInfoGoOut carInfo = carMap.get(vid);
        if (Objects.isNull(carInfo)) {
            log.warn("车辆基础信息为空，vid:{}", vid);
            return;
        }
        this.vin = carInfo.getVin();
        carInfoTemp.setVid(carInfo.getVid());
        carInfoTemp.setVin(carInfo.getVin());
        carInfoTemp.setCarType(carInfo.getCarType());
        carInfoTemp.setCarImg(carInfo.getCarImg());
        carInfoTemp.setItemMap(carInfo.getItemMap());
        List<LabelDTO> labelList = Arrays.asList(CarTagConvert.INSTANCE.toCarTag(carInfo.getCarTag()));
        // 添加svip标签
        labelList = addMemberLabel(memberInfoBO, vid, labelList);

        carInfoTemp.setCarTagList(labelList);
        // 车主信息组装
        if (Objects.nonNull(carUserAgg)) {
            carInfoTemp.setCarOwner(carUserAgg.getSysUserName());
            carInfoTemp.setCarOwnerTel(carUserAgg.getSysUserPhone());
        }
        // 车辆动态信息组�?
        if (Objects.nonNull(carDynamicInfo)) {
            Map<String, GetDynamicInfoResponseGoOut.DynamicInfoItemDto> dynamicInfoMap = Optional.ofNullable(carDynamicInfo.getItems())
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(e -> e.getVid(), e -> e, (k1, k2) -> k1));
            GetDynamicInfoResponseGoOut.DynamicInfoItemDto dynamicInfoItemDto = dynamicInfoMap.get(vid);
            if (Objects.isNull(dynamicInfoItemDto)) {
                log.warn("carDynamicInfo is null, vid:{}", vid);
                // 车辆动态信息不阻塞其他信息的组�?
                return;
            }
            carInfoTemp.setCurrentVersion(dynamicInfoItemDto.getSysVersion());
        }
    }


    public static List<LabelDTO> addMemberLabel(BatchMemberInfoBO memberInfoBO, String vid, List<LabelDTO> labelList) {
        log.info("添加会员标签 param vid : {},  labelList : {}, memberInfoBO :{}", vid, GsonUtil.toJsonStr(labelList), GsonUtil.toJsonStr(memberInfoBO));

        if (memberInfoBO == null || CollUtil.isEmpty(memberInfoBO.getList())) {
            log.info("memberInfoBO is null or empty, return");
            return labelList;
        }

        if (StrUtil.isBlank(vid)) {
            log.info("vid is blank, return");
            return labelList;
        }

        Map<String, BatchMemberInfoBO.MemberInfoBo> memberInfoMap = new HashMap<>();
        if (CollUtil.isNotEmpty(memberInfoBO.getList())) {
            memberInfoMap = memberInfoBO.getList().stream().collect(Collectors.toMap(BatchMemberInfoBO.MemberInfoBo::getVid, Function.identity()));
        }
        log.info("会员map:{}", memberInfoMap);
        BatchMemberInfoBO.MemberInfoBo memberInfoBo = memberInfoMap.get(vid);
        if (Objects.nonNull(memberInfoBo)) {
            LabelDTO label = new LabelDTO();

            if (CollUtil.isNotEmpty(labelList) && CollUtil.isNotEmpty(labelList.stream()
                    .filter(item -> item != null && item.getTagType() != null && item.getTagType() == 1).collect(Collectors.toList()))) {
                label = labelList.stream().filter(item -> item != null && item.getTagType() != null && item.getTagType() == 1).collect(Collectors.toList()).get(0);
            } else {
                labelList.add(label);
                label.setTagType(1);
            }

            LabelDTO.TagInfo tagInfo = new LabelDTO.TagInfo();
            tagInfo.setTagCode(String.valueOf(memberInfoBo.getLevel()));
            tagInfo.setTagName(memberInfoBo.getLevelName());

            if (CollUtil.isEmpty(label.getTagList())) {
                label.setTagList(new ArrayList<>());
            }
            label.getTagList().add(tagInfo);
        }
        log.info("添加后标签列�?{}", GsonUtil.toJsonStr(labelList));
        return labelList;
    }

    public void fillWarrantyPeriod(WarrantyPeriodGoOut warrantyPeriodGoOut) {
        if (Objects.isNull(warrantyPeriodGoOut)) {
            log.error("warrantyPeriodGoOut is null, skip fillWarrantyPeriod detail, use default warranty flags");
            return;
        }
        WarrantyInfo warrantyInfoTemp = new WarrantyInfo();
        warrantyInfoTemp.setWarrantyEffectZc(Optional.ofNullable(warrantyPeriodGoOut.getWarrantyInfo())
            .map(WarrantyPeriodGoOut.WarrantyInfoDto::getZc)
            .map(WarrantyPeriodGoOut.WarrantyInfoDetailDto::getWarrantyEffect).orElse(false));
        warrantyInfoTemp.setWarrantyEffectSd(Optional.ofNullable(warrantyPeriodGoOut.getWarrantyInfo())
            .map(WarrantyPeriodGoOut.WarrantyInfoDto::getSd)
            .map(WarrantyPeriodGoOut.WarrantyInfoDetailDto::getWarrantyEffect).orElse(false));
        warrantyInfoTemp.setWarrantyEffectYs(Optional.ofNullable(warrantyPeriodGoOut.getWarrantyInfo())
            .map(WarrantyPeriodGoOut.WarrantyInfoDto::getYs)
            .map(WarrantyPeriodGoOut.WarrantyInfoDetailDto::getWarrantyEffect).orElse(false));
        this.warrantyInfo = warrantyInfoTemp;
    }

    /**
     * 填充车辆里程和交付日期信�?
     * @param warrantyPeriodGoOut 质保信息（包含里程和交付日期�?
     */
    public void fillCarMileageAndDeliveryDate(WarrantyPeriodGoOut warrantyPeriodGoOut) {
        if (Objects.isNull(this.carInfo)) {
            log.warn("carInfo is null, skip fillCarMileageAndDeliveryDate");
            return;
        }
        if (Objects.isNull(warrantyPeriodGoOut) || Objects.isNull(warrantyPeriodGoOut.getCarInfo())) {
            log.warn("warrantyPeriodGoOut or carInfo is null, skip fillCarMileageAndDeliveryDate");
            return;
        }
        WarrantyPeriodGoOut.CarInfoDto warrantyCarInfo = warrantyPeriodGoOut.getCarInfo();
        // TODO:zzy 这里待确认是否替换为浮点�?
        this.carInfo.setMileage(warrantyCarInfo.getMileage());
        this.carInfo.setDeliveryDate(warrantyCarInfo.getDeliveryDate());
    }

    public void fillComplaintOrderInfo(ComplaintOrderInfoGoIn orderInfo, List<EmployeeInfoGoOut> employeeInfoList, StoreInfoGoOut storeInfo) {
        ComplaintOrderInfo complaintOrderInfo = new ComplaintOrderInfo();
        this.complaintOrderInfo = complaintOrderInfo;
        complaintOrderInfo.setComplaintNo(orderInfo.getComplaintNo());
        complaintOrderInfo.setCreateTime(DateUtil.getTimeStrByDate(orderInfo.getCreateTime()));
        complaintOrderInfo.setOrgId(orderInfo.getOrgId());
        complaintOrderInfo.setOrgName(Objects.nonNull(storeInfo) ? storeInfo.getOrgName() : "");
        // 填充涉媒信息
        complaintOrderInfo.setMediaInvolved(orderInfo.getMediaInvolved());
        complaintOrderInfo.setMediaLink(orderInfo.getMediaLink());
        complaintOrderInfo.setUpgradeTime(DateUtil.getTimeStrByDate(orderInfo.getUpgradeTime()));
        if (CollUtils.isEmpty(employeeInfoList)) {
            log.warn("工单处理人信息为�?);
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap = employeeInfoList.stream().collect(Collectors.toMap(e -> e.getMiId(), e -> e, (k1, k2) -> k1));
        complaintOrderInfo.setCustomerServiceName(employeeMap.containsKey(orderInfo.getCustomerServiceMid()) ? employeeMap.get(orderInfo.getCustomerServiceMid()).getName() : "");
        complaintOrderInfo.setCustomerServiceEmail(employeeMap.containsKey(orderInfo.getCustomerServiceMid()) ? employeeMap.get(orderInfo.getCustomerServiceMid()).getEmailPrefix() : "");
        complaintOrderInfo.setHandleName(employeeMap.containsKey(orderInfo.getOperatorMid()) ? employeeMap.get(orderInfo.getOperatorMid()).getName() : "");
    }

    public void fillComplaintTag(List<ComplaintTagGoOut> complaintTagGoOutList) {
        if (CollUtils.isEmpty(complaintTagGoOutList)) {
            log.warn("complaintTagGoOutList is empty, complaintNo:{}", complaintOrderInfo.getComplaintNo());
            return;
        }
        Map<String, List<ComplaintTagGoOut>> complaintTagMap = complaintTagGoOutList.stream().collect(Collectors.groupingBy(e -> e.getComplaintNo()));
        List<ComplaintTagGoOut> complaintTagGoOuts = complaintTagMap.get(complaintOrderInfo.getComplaintNo());
        List<String> tagList = complaintTagGoOuts.stream().map(e -> e.getTagType()).collect(Collectors.toList());
        this.complaintTagList = tagList;
    }

    public void fillDetailTab(List<DetailTabEnum> detailTabByStatus, List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        if (CollUtils.isEmpty(detailTabByStatus)) {
            log.warn("detailTabByStatus is empty, complaintNo:{}", complaintOrderInfo.getComplaintNo());
            return;
        }
        List<TabData> tabList = new ArrayList<>();
        detailTabByStatus.stream().forEach(e ->
            tabList.add(TabData.builder().tabCode(e.getType()).tabName(e.getDesc()).build())
        );
        // 若无跟进记录，不展示跟进记录tab
        List<TabData> collect = tabList;
        if (CollUtils.isEmpty(followProcessGoOuts)) {
            collect = tabList.stream().filter(e -> e.getTabCode() != DetailTabEnum.FOLLOW_UP_RECORDS.getType()).collect(Collectors.toList());
        }
        this.tabDataList = collect;
    }

    public void constructStatusBar(List<ComplaintFollowProcessGoOut> followProcessGoOuts, ComplaintOrderInfoGoIn orderInfo) {
        List<StatusData> statusDataList = new LinkedList<>();
        Map<String, ComplaintFollowProcessGoOut> processMap = followProcessGoOuts.stream().collect(Collectors.toMap(e -> e.getProcessType(), e -> e, (k1, k2) -> k1));

        for (ComplaintStatusEnum value : ComplaintStatusEnum.values()) {
            if (value.getCode() < orderInfo.getStatus()) {
                StatusData build = StatusData.builder().status(value.getCode()).stateName(value.getBarBeenDesc()).doneYn(DoneYNEnum.YES.getCode()).build();
                statusDataList.add(build);
            } else if (Objects.equals(ComplaintStatusEnum.FINISH_COMPLETE.getCode(), orderInfo.getStatus())) {
                StatusData build = StatusData.builder().status(value.getCode()).stateName(value.getBarBeenDesc()).doneYn(DoneYNEnum.YES.getCode()).build();
                statusDataList.add(build);
            } else {
                StatusData build = StatusData.builder().status(value.getCode()).stateName(value.getBarFutureDesc()).doneYn(DoneYNEnum.NO.getCode()).build();
                statusDataList.add(build);
            }
        }
        for (StatusData statusData : statusDataList) {
            if (statusData.getDoneYn() == DoneYNEnum.NO.getCode()) {
                continue;
            }
            if (statusData.getStatus() == ComplaintStatusEnum.PENDING_ORDER.getCode()) {
                ComplaintFollowProcessGoOut pickUpOrder = processMap.getOrDefault(ProcessTypeEnum.PICKUP_ORDER.getProcessCode(), null);
                ComplaintFollowProcessGoOut dispatchOrder = processMap.getOrDefault(ProcessTypeEnum.DISPATCH_ORDER.getProcessCode(), null);
                if (pickUpOrder != null && dispatchOrder == null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(pickUpOrder.getCreateTime()));
                }
                if (dispatchOrder != null && pickUpOrder == null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(dispatchOrder.getCreateTime()));
                }
                if (dispatchOrder != null && pickUpOrder != null) {
                    // 取时间早的那�?
                    if (pickUpOrder.getCreateTime().getTime() < dispatchOrder.getCreateTime().getTime()) {
                        statusData.setUpdateTime(DateUtil.getTimeStrByDate(pickUpOrder.getCreateTime()));
                    } else {
                        statusData.setUpdateTime(DateUtil.getTimeStrByDate(dispatchOrder.getCreateTime()));
                    }
                }
            }
            if (statusData.getStatus() == ComplaintStatusEnum.FIRST_RESPONSE_PENDING.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.FIRST_RESPONSE.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
            if (statusData.getStatus() == ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.APPLY_FINISH.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
            if (statusData.getStatus() == ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode(), null);
                ComplaintFollowProcessGoOut orDefault1 = processMap.getOrDefault(ProcessTypeEnum.AUDIT_FINISH_REJECT.getProcessCode(), null);
                if (orDefault != null && orDefault1 != null) {
                    // 比出orDefault.getCreateTime和orDefault1.getCreateTime的最小�?
                    Date minDate = orDefault.getCreateTime().before(orDefault1.getCreateTime()) ? orDefault.getCreateTime() : orDefault1.getCreateTime();
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(minDate));
                } else if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                } else if (orDefault1 != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault1.getCreateTime()));
                }
            }
            if (statusData.getStatus() == ComplaintStatusEnum.FINISH_COMPLETE.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
        }
        this.statusBar = statusDataList.stream().filter(e -> !e.getStateName().equals("")).collect(Collectors.toList());
    }

    public void constructActionList(String currentRole, Long mid, UserAuthManager userAuthManager, ComplaintOrderInfoGoIn orderInfo) {
        List<String> detailActionAuth = userAuthManager.getDetailActionAuth(currentRole, orderInfo, mid);
        UserActionAuth userAuth = new UserActionAuth();
        userAuth.setActionsList(detailActionAuth);
        this.userActionAuth = userAuth;
    }

    public void fillAuditProcessLog(List<ComplaintFollowProcessGoOut> followProcessGoOuts, List<EmployeeInfoGoOut> employeeInfoList) {
        List<AuditLog> auditLogs = new ArrayList<>();
        if (CollUtil.isEmpty(followProcessGoOuts)) {
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap = Optional.ofNullable(employeeInfoList).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(e -> e.getMiId(), e -> e, (k1, k2) -> k1));
        List<ComplaintFollowProcessGoOut> auditProcessList = followProcessGoOuts.stream().filter(e -> ProcessTypeEnum.getAuditProcessCodeList().contains(e.getProcessType())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(auditProcessList)) {
            auditProcessList.forEach(e -> auditLogs.add(constructAuditLog(e, employeeMap)));
        }
        this.auditLogList = auditLogs;
    }

    private AuditLog constructAuditLog(ComplaintFollowProcessGoOut processLog, Map<Long, EmployeeInfoGoOut> employeeMap) {
        AuditLog tempLog = new AuditLog();
        RecordInfoSoOut recordInfoSoOut = GsonUtil.parseObj(processLog.getProcessContent(), RecordInfoSoOut.class);
        tempLog.setCreateTime(DateUtil.getTimeStrByDate(processLog.getCreateTime()));
        String processType = processLog.getProcessType();
        if (ProcessTypeEnum.getApplyProcessCodeList().contains(processType)) {
            fillAuditOperatorFromMid(tempLog, recordInfoSoOut != null ? recordInfoSoOut.getApplyMid() : null, employeeMap, processType);
        } else if (ProcessTypeEnum.getOnlyAuditProcessCodeList().contains(processType)) {
            fillAuditOperatorFromMid(tempLog, recordInfoSoOut != null ? recordInfoSoOut.getAuditMid() : null, employeeMap, processType);
        }
        return tempLog;
    }

    private void fillAuditOperatorFromMid(AuditLog tempLog, Long operatorMid, Map<Long, EmployeeInfoGoOut> employeeMap, String processType) {
        tempLog.setOperatorMid(operatorMid != null ? operatorMid.toString() : "");
        EmployeeInfoGoOut emp = operatorMid != null ? employeeMap.get(operatorMid) : null;
        tempLog.setOperatorName(emp != null ? emp.getName() : "");
        tempLog.setOperatorEmail(emp != null ? emp.getEmailPrefix() : "");
        tempLog.setOperatorAction(ProcessTypeEnum.getNameByCode(processType));
    }


    @Data
    public static class CarInfo implements Serializable {
        /**
         * 车型
         */
        private String carType;

        /**
         * 车图�?
         */
        private String carImg;

        /**
         * 车辆VIN码，即车架号
         */
        private String vin;

        /**
         * 车辆vid
         */
        private String vid;

        /**
         * 车主尊称
         */
        private String carOwner;

        /**
         * 车主手机号码
         */
        private String carOwnerTel;

        /**
         * 软件版本
         */
        private String currentVersion;

        /**
         * 车辆标签列表，如用户关怀�?
         */
        private List<LabelDTO> carTagList;

        /**
         * 汽车配置信息 key:identityEnum  value itemValue.name
         */
        private Map<String, String> itemMap;

        /**
         * 行驶里程，单位km
         */
        private Integer mileage;

        /**
         * 交付日期
         */
        private String deliveryDate;
    }

    @Data
    public static class ComplaintOrderInfo implements Serializable {
        /**
         * 客诉单号
         */
        private String complaintNo;

        /**
         * 跟进客服名称
         */
        private String customerServiceName;

        /**
         * 跟进客服邮箱前缀
         */
        private String customerServiceEmail;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 门店id
         */
        private String orgId;

        /**
         * 门店名称
         */
        private String orgName;

        /**
         * 处理�?
         */
        private String handleName;

        /**
         * 是否涉媒 0-�?1-�?
         */
        private Integer mediaInvolved;

        /**
         * 涉媒链接
         */
        private String mediaLink;

        /**
         * 升级投诉时间
         */
        private String upgradeTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TabData implements Serializable {
        /**
         * tab编码
         */
        private String tabCode;

        /**
         * tab名称
         */
        private String tabName;
    }

    @Data
    @Builder
    public static class StatusData implements Serializable {
        private Integer status;
        /**
         * 进度节点名称
         */
        private String stateName;

        /**
         * 是否已完�?0:未完�?1:已完�?
         */
        private Integer doneYn;

        /**
         * 更新时间
         */
        private String updateTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarrantyInfo implements Serializable {

        private static final long serialVersionUID = 2195862883261219604L;

        /**
         * 保修有效�?三电
         */
        private Boolean warrantyEffectSd;

        /**
         * 保修有效�?延保
         */
        private Boolean warrantyEffectYs;

        /**
         * 保修有效�?整车
         */
        private Boolean warrantyEffectZc;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuditLog implements Serializable {

        private static final long serialVersionUID = 2167323228802627847L;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 操作者MID
         */
        private String operatorMid;

        /**
         * 操作人姓�?
         */
        private String operatorName;

        /**
         * 邮箱
         */
        private String operatorEmail;

        /**
         * 操作记录
         */
        private String operatorAction;
    }


}
