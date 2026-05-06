package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.enums.DetailTabEnum;
import com.wt.complaint.manage.api.model.enums.DoneYNEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.ReportDetailTabEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.api.model.resp.UseComplaintActionAuth;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.WarrantyPeriodGoOut;
import com.wt.complaint.manage.domain.api.service.converter.CarTagConvert;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import com.xiaomi.newretail.common.tools.utils.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class UserComplaintDetailFrameSoOut implements Serializable {

    @ApiDocClassDefine(value = "举报单号", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "服务单号", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "工单�?, description = "工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "举报单状�?, description = "举报单状�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private Integer orderStatus;

    @ApiDocClassDefine(value = "举报单状态名�?, description = "举报单状态名�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private String orderStatusName;

    @ApiDocClassDefine(value = "车牌�?, description = "车牌�?)
    private String carNo;

    @ApiDocClassDefine(value = "联系人姓�?, description = "联系人姓�?)
    private String contactName;

    @ApiDocClassDefine(value = "联系电话", description = "联系电话")
    private String contactPhone;

    @ApiDocClassDefine(value = "车架号Vin", description = "车架号Vin")
    private String vin;

    @ApiDocClassDefine(value = "车辆vid", description = "车辆vid")
    private String vid;

    @ApiDocClassDefine(value = "催单次数", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "举报单进度条信息", description = "举报单进度条信息")
    private List<UserComplaintDetailFrameSoOut.StatusData> statusBar;

    @ApiDocClassDefine(value = "车辆信息", description = "车辆信息")
    private UserComplaintDetailFrameSoOut.CarInfo carInfo;

    @ApiDocClassDefine(value = "warrantyInfo", description = "车辆质保相关标签")
    private UserComplaintDetailFrameSoOut.WarrantyInfo warrantyInfo;

    @ApiDocClassDefine(value = "举报单基本信�?, description = "举报单基本信�?)
    private UserComplaintDetailFrameSoOut.UserComplaintOrderInfo userComplaintOrderInfo;

    @ApiDocClassDefine(value = "举报单详情页tab展示列表", description = "举报单详情页tab展示列表 跟进记录 followUpRecords，举报信�?userComplaintInfo，线上服务记�?onlineServiceRecords")
    private List<UserComplaintDetailFrameSoOut.TabData> tabDataList;

    @ApiDocClassDefine(value = "用户操作按钮", description = "用户操作按钮")
    private UseComplaintActionAuth useComplaintActionAuth;

    @Data
    public static class CarInfo implements Serializable {

        private static final long serialVersionUID = -2510831465834726671L;

        @ApiDocClassDefine(value = "车型", description = "车型")
        private String carType;

        @ApiDocClassDefine(value = "车图�?, description = "车图�?)
        private String carImg;

        @ApiDocClassDefine(value = "车辆VIN�?, description = "车辆VIN码，即车架号")
        private String vin;

        @ApiDocClassDefine(value = "车辆vid", description = "车辆vid")
        private String vid;

        @ApiDocClassDefine(value = "车主尊称", description = "车主尊称")
        private String carOwner;

        @ApiDocClassDefine(value = "车主手机号码", description = "车主手机号码")
        private String carOwnerTel;

        @ApiDocClassDefine(value = "软件版本", description = "软件版本")
        private String currentVersion;

        @ApiDocClassDefine(value = "车辆标签列表", description = "车辆标签列表，如用户关怀�?)
        private List<LabelDTO> carTagList;

        @ApiDocClassDefine(value = "汽车配置信息", description = "汽车配置信息 key:identityEnum  value itemValue.name")
        private Map<String, String> itemMap;
    }

    @Data
    public static class UserComplaintOrderInfo implements Serializable {
        @ApiDocClassDefine(value = "举报单号", description = "举报单号")
        private String ucNo;

        @ApiDocClassDefine(value = "创建时间", description = "创建时间")
        private String createTime;

        @ApiDocClassDefine(value = "门店id", description = "门店id")
        private String orgId;

        @ApiDocClassDefine(value = "门店名称", description = "门店名称")
        private String orgName;

        @ApiDocClassDefine(value = "处理�?, description = "处理�?)
        private String handleName;
    }

    @Data
    @Builder
    public static class TabData implements Serializable {

        private static final long serialVersionUID = 5503324723653893981L;

        @ApiDocClassDefine(value = "tab编码", description = "tab编码")
        private String tabCode;

        @ApiDocClassDefine(value = "tab名称", description = "tab名称")
        private String tabName;
    }

    @Data
    @Builder
    public static class StatusData implements Serializable {

        private static final long serialVersionUID = 8471466162436255837L;

        private Integer status;

        @ApiDocClassDefine(value = "进度节点名称", description = "进度节点名称")
        private String stateName;

        @ApiDocClassDefine(value = "是否完成", description = "是否已完�?0:未完�?1:已完�?)
        private Integer doneYn;

        @ApiDocClassDefine(value = "更新时间", description = "更新时间")
        private String updateTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarrantyInfo implements Serializable {

        private static final long serialVersionUID = 2195862883261219604L;

        @ApiDocClassDefine(value = "warrantyEffectSd", description = "保修有效�?三电")
        private Boolean warrantyEffectSd;

        @ApiDocClassDefine(value = "warrantyEffectYs", description = "保修有效�?延保")
        private Boolean warrantyEffectYs;

        @ApiDocClassDefine(value = "warrantyEffectZc", description = "保修有效�?整车")
        private Boolean warrantyEffectZc;
    }

    public void fillBaseInfo(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        this.ucNo = userComplaintOrderDetailSoOut.getUcNo();
        this.orderStatus = userComplaintOrderDetailSoOut.getOrderStatus();
        this.orderStatusName = ReportOrderStatusEnum.getDescByCode(userComplaintOrderDetailSoOut.getOrderStatus());
        this.soNo = userComplaintOrderDetailSoOut.getSoNo();
        this.superTicketNo = userComplaintOrderDetailSoOut.getSuperTicketNo();
        this.carNo = userComplaintOrderDetailSoOut.getCarNo();
        if (StrUtil.isNotBlank(userComplaintOrderDetailSoOut.getContactNameC())) {
            this.contactName = KeyCenterUtil.decrypt(userComplaintOrderDetailSoOut.getContactNameC());
        }
        if (StrUtil.isNotBlank(userComplaintOrderDetailSoOut.getContactPhoneC())) {
            this.contactPhone = KeyCenterUtil.decrypt(userComplaintOrderDetailSoOut.getContactPhoneC());
        }
        this.reminderTimes = userComplaintOrderDetailSoOut.getReminderTimes();
    }

    public void fillCarInfo(List<CarInfoGoOut> carInfoGoOutList, CarUserAggGoOut carUserAgg,
                            GetDynamicInfoResponseGoOut carDynamicInfo, BatchMemberInfoBO memberInfoBO) {
        UserComplaintDetailFrameSoOut.CarInfo carInfoTemp = new UserComplaintDetailFrameSoOut.CarInfo();
        this.carInfo = carInfoTemp;
        // 车基础信息组装
        Map<String, CarInfoGoOut> carMap = carInfoGoOutList.stream().collect(
                Collectors.toMap(CarInfoGoOut::getVid, e -> e, (k1, k2) -> k1));
        CarInfoGoOut carInfoGoOut = carMap.get(vid);
        if (Objects.isNull(carInfoGoOut)) {
            log.warn("车辆基础信息为空，vid:{}", vid);
            return;
        }
        this.vin = carInfoGoOut.getVin();
        carInfoTemp.setVid(carInfoGoOut.getVid());
        carInfoTemp.setVin(carInfoGoOut.getVin());
        carInfoTemp.setCarType(carInfoGoOut.getCarType());
        carInfoTemp.setCarImg(carInfoGoOut.getCarImg());
        carInfoTemp.setItemMap(carInfoGoOut.getItemMap());
        List<LabelDTO> labelList = Collections.singletonList(CarTagConvert.INSTANCE.toCarTag(carInfoGoOut.getCarTag()));
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
            Map<String, GetDynamicInfoResponseGoOut.DynamicInfoItemDto> dynamicInfoMap =
                    Optional.ofNullable(carDynamicInfo.getItems())
                            .orElse(new ArrayList<>()).stream()
                            .collect(Collectors.toMap(GetDynamicInfoResponseGoOut.DynamicInfoItemDto::getVid, e -> e,
                                    (k1, k2) -> k1));
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
        log.info("添加会员标签 param vid : {},  labelList : {}, memberInfoBO :{}", vid, GsonUtil.toJsonStr(labelList),
                GsonUtil.toJsonStr(memberInfoBO));

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
            memberInfoMap = memberInfoBO.getList().stream()
                    .collect(Collectors.toMap(BatchMemberInfoBO.MemberInfoBo::getVid, Function.identity()));
        }
        log.info("会员map:{}", memberInfoMap);
        BatchMemberInfoBO.MemberInfoBo memberInfoBo = memberInfoMap.get(vid);
        if (Objects.nonNull(memberInfoBo)) {
            LabelDTO label = new LabelDTO();

            if (CollUtil.isNotEmpty(labelList) && CollUtil.isNotEmpty(labelList.stream()
                    .filter(item -> item != null && item.getTagType() != null && item.getTagType() == 1)
                    .collect(Collectors.toList()))) {
                label = labelList.stream()
                        .filter(item -> item != null && item.getTagType() != null && item.getTagType() == 1)
                        .collect(Collectors.toList()).get(0);
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

    public void fillComplaintOrderInfo(UserComplaintOrderDetailSoOut orderInfo,
                                       List<EmployeeInfoGoOut> employeeInfoList, StoreInfoGoOut storeInfo) {
        UserComplaintDetailFrameSoOut.UserComplaintOrderInfo complaintOrderInfo =
                new UserComplaintDetailFrameSoOut.UserComplaintOrderInfo();
        this.userComplaintOrderInfo = complaintOrderInfo;
        complaintOrderInfo.setUcNo(orderInfo.getUcNo());
        complaintOrderInfo.setCreateTime(DateUtil.getTimeStrByDate(orderInfo.getCreateTime()));
        complaintOrderInfo.setOrgId(orderInfo.getOrgId());
        complaintOrderInfo.setOrgName(Objects.nonNull(storeInfo) ? storeInfo.getOrgName() : "");
        if (CollUtils.isEmpty(employeeInfoList)) {
            log.warn("工单处理人信息为�?);
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap =
                employeeInfoList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, e -> e, (k1, k2) -> k1));
        complaintOrderInfo.setHandleName(employeeMap.containsKey(orderInfo.getOperatorMid()) ?
                employeeMap.get(orderInfo.getOperatorMid()).getName() : "");
    }

    public void fillDetailTab(List<ReportDetailTabEnum> detailTabByStatus,
                              List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        if (CollUtils.isEmpty(detailTabByStatus)) {
            log.warn("detailTabByStatus is empty, ucNo:{}", userComplaintOrderInfo.getUcNo());
            return;
        }
        List<UserComplaintDetailFrameSoOut.TabData> tabList = new ArrayList<>();
        detailTabByStatus.forEach(e ->
                tabList.add(UserComplaintDetailFrameSoOut.TabData.builder().tabCode(e.getType()).tabName(e.getDesc())
                        .build())
        );
        // 若无跟进记录，不展示跟进记录tab
        List<UserComplaintDetailFrameSoOut.TabData> collect = tabList;
        if (CollUtils.isEmpty(followProcessGoOuts)) {
            collect = tabList.stream().filter(e -> !Objects.equals(e.getTabCode(),
                            DetailTabEnum.FOLLOW_UP_RECORDS.getType()))
                    .collect(Collectors.toList());
        }
        this.tabDataList = collect;
    }

    public void constructStatusBar(List<ComplaintFollowProcessGoOut> followProcessGoOuts,
                                   UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        List<UserComplaintDetailFrameSoOut.StatusData> statusDataList = new LinkedList<>();
        Map<String, ComplaintFollowProcessGoOut> processMap =
                followProcessGoOuts.stream()
                        .collect(Collectors.toMap(ComplaintFollowProcessGoOut::getProcessType, e -> e, (k1, k2) -> k1));

        for (ReportOrderStatusEnum value : ReportOrderStatusEnum.values()) {
            if (value.getCode() < userComplaintOrderDetailSoOut.getOrderStatus()) {
                UserComplaintDetailFrameSoOut.StatusData
                        build = UserComplaintDetailFrameSoOut.StatusData.builder().status(value.getCode())
                        .stateName(value.getBarBeenDesc()).doneYn(
                                DoneYNEnum.YES.getCode()).build();
                statusDataList.add(build);
            } else if (Objects.equals(ReportOrderStatusEnum.FINISH.getCode(),
                    userComplaintOrderDetailSoOut.getOrderStatus())) {
                UserComplaintDetailFrameSoOut.StatusData
                        build = UserComplaintDetailFrameSoOut.StatusData.builder().status(value.getCode())
                        .stateName(value.getBarBeenDesc()).doneYn(DoneYNEnum.YES.getCode()).build();
                statusDataList.add(build);
            } else {
                UserComplaintDetailFrameSoOut.StatusData
                        build = UserComplaintDetailFrameSoOut.StatusData.builder().status(value.getCode())
                        .stateName(value.getBarFutureDesc()).doneYn(DoneYNEnum.NO.getCode()).build();
                statusDataList.add(build);
            }
        }
        for (UserComplaintDetailFrameSoOut.StatusData statusData : statusDataList) {
            if (Objects.equals(statusData.getDoneYn(), DoneYNEnum.NO.getCode())) {
                continue;
            }
            if (Objects.equals(statusData.getStatus(), ReportOrderStatusEnum.PENDING_ORDER.getCode())) {
                ComplaintFollowProcessGoOut pickUpOrder =
                        processMap.getOrDefault(ProcessTypeEnum.PICKUP_ORDER.getProcessCode(), null);
                if (pickUpOrder != null) {
                    statusData.setUpdateTime(cn.hutool.core.date.DateUtil.format(pickUpOrder.getCreateTime(),
                            "yyyy-MM-dd HH:mm"));
                }
            }
            if (Objects.equals(statusData.getStatus(), ReportOrderStatusEnum.PENDING_JUDGE.getCode())) {
                ComplaintFollowProcessGoOut reportJudgeOrder =
                        processMap.getOrDefault(ProcessTypeEnum.REPORT_JUDGE.getProcessCode(), null);
                if (reportJudgeOrder != null) {
                    statusData.setUpdateTime(cn.hutool.core.date.DateUtil.format(reportJudgeOrder.getCreateTime(),
                            "yyyy-MM-dd HH:mm"));
                }
            }
            if (Objects.equals(statusData.getStatus(), ReportOrderStatusEnum.FINISH.getCode())) {
                statusData.setUpdateTime(
                        cn.hutool.core.date.DateUtil.format(userComplaintOrderDetailSoOut.getFinishTime(),
                                "yyyy-MM-dd HH:mm"));
            }
        }
        this.statusBar = statusDataList.stream().filter(e -> !e.getStateName().isEmpty()).collect(Collectors.toList());
    }

    public void constructActionList(CarEmployeeInfoSoOut carEmployeeInfoSoOut, Long mid,
                                    ReportAuthManager reportAuthManager,
                                    UserComplaintOrderDetailSoOut orderInfo) {
        boolean hasOperationalVerification = carEmployeeInfoSoOut.hasOperationalVerification;
        boolean hasRegionalOperationsManagement = carEmployeeInfoSoOut.hasRegionalOperationsManagement;
        boolean hasCityServiceManager = carEmployeeInfoSoOut.hasCityServiceManager;
        List<String> totalDetailActionAuth = new ArrayList<>();
        // 是否有运营检核岗
        if (hasOperationalVerification) {
            List<String> detailActionAuth =
                    reportAuthManager.getDetailActionAuth(PositionEnum.OPERATIONAL_VERIFICATION, orderInfo, mid);
            totalDetailActionAuth.addAll(detailActionAuth);
        }
        // 是否有区域运营管理岗
        if (hasRegionalOperationsManagement) {
            List<String> detailActionAuth =
                    reportAuthManager.getDetailActionAuth(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT, orderInfo, mid);
            totalDetailActionAuth.addAll(detailActionAuth);
        }
        // 是否有城市服务经理岗
        if (hasCityServiceManager) {
            List<String> detailActionAuth =
                    reportAuthManager.getDetailActionAuth(PositionEnum.CITY_SERVICE_MANAGER, orderInfo, mid);
            totalDetailActionAuth.addAll(detailActionAuth);
        }
        UseComplaintActionAuth userAuth = new UseComplaintActionAuth();
        userAuth.setActionsList(totalDetailActionAuth.stream().distinct().collect(Collectors.toList()));
        this.useComplaintActionAuth = userAuth;
    }

    public void fillWarrantyPeriod(WarrantyPeriodGoOut warrantyPeriodGoOut) {
        UserComplaintDetailFrameSoOut.WarrantyInfo warrantyInfoTemp = new UserComplaintDetailFrameSoOut.WarrantyInfo();
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
}
