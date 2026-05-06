package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.ConsultDetailTabEnum;
import com.wt.complaint.manage.api.model.enums.ConsultStatusEnum;
import com.wt.complaint.manage.api.model.enums.DetailTabEnum;
import com.wt.complaint.manage.api.model.enums.DoneYNEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.service.converter.CarTagConvert;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.utils.DateUtil;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 咨询单详情出�?
 */
@Data
@Slf4j
@SuppressWarnings("all")
public class ConsultDetailSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mrNo;

    /**
     * 咨询单状�?1-待接�?2-待首�?3-待结�?4-已完�?
     */
    private Integer consultStatus;

    /**
     * 创建�?mid
     */
    private Long createMid;

    /**
     * 门店 Id
     */
    private String orgId;


    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 车辆VID
     */
    private String vid;

    /** 咨询单号 */
    private String consultNo;
    /** 客户姓名 */
    private String customerName;
    /** 客户电话 */
    private String customerPhone;
    /** 车牌�?*/
    private String carNo;
    /** 车辆VIN */
    private String vin;
    /** 咨询类型 */
    private Integer consultType;
    /** 咨询类型名称 */
    private String consultTypeName;
    /** 联系�?*/
    private String contactPerson;
    /** 联系人电�?*/
    private String contactPhone;
    /** 紧急标�?*/
    private String urgentFlag;
    /** 创建人姓�?*/
    private String creator;
    /** 创建时间（格�?yyyy-MM-dd HH:mm:ss�?*/
    private String createTime;
    /** 是否关联维保�?*/
    private String isLinkedMrOrder;
    /** 超级工单�?维保单号 */
    private String superTicketNo;
    /** 维保单服务门�?*/
    private String warrantyServiceStore;
    /** 跟进门店 */
    private String followStore;
    /** 跟进�?*/
    private String follower;
    /** 期望回电时间 */
    private String callbackTime;
    /** 诉求描述 */
    private String appealDesc;
    /** 附件列表 */
    private List<Attachment> attachmentList;
    /** 优先�? 4 一般，8 高，16 紧�?*/
    private Integer priority;
    /** 咨询单信�?*/
    private ConsultOrderInfo consultOrderInfo;
    /** 车辆信息 */
    private CarInfo carInfo;
    /** 状态栏 */
    private List<StatusData> statusDataList;
    /** 只读标识 */
    private Integer onlyView;
    /** 标签数据列表 */
    private List<TabData> tabDataList;
    /**
     * 用户操作按钮
     */
    private UserActionAuth userActionAuth;

    /**
     * 进度条信�?
     */
    private List<StatusData> statusBar;

    /**
     * 维保超级工单�?
     */
    private String mrSuperTicketNo;

    /**
     * 结案信息(已结案才�?
     */
    private CompleteInfo completeInfo;

    @Data
    public static class ConsultOrderInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 咨询单号 */
        private String consultNo;
        /** 创建人姓�?*/
        private String creator;
        /** 创建时间（格�?yyyy-MM-dd HH:mm:ss�?*/
        private String createTime;
        /** 跟进门店 */
        private String followStore;
        /** 跟进�?*/
        private String follower;
    }


    /**
     * 结案信息(已完成才�?
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompleteInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        // 处理完成时间
        private String completeTime;

        // 完成人mid
        private String completeUser;

        // 完成人姓�?
        private String completeUserName;

        // 处理结果
        private String completeResult;

        // 解决方案
        private String solution;
    }

    /**
     * 车辆信息
     */
    @Data
    public static class CarInfo implements Serializable {
        private static final long serialVersionUID = 1L;
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

    /**
     * 车辆标签
     */
    @Data
    public static class CarTag implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 标签类型 1: 汽车标签, 2: 人员标签 */
        private Integer tagType;
        /** 标签列表 */
        private List<TagItem> tagList;
    }

    /**
     * 标签�?
     */
    @Data
    public static class TagItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 标签代码 */
        private String tagCode;
        /** 标签名称 */
        private String tagName;
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


    /**
     * 标签数据
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TabData implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 标签名称 */
        private String tabName;
        /** 标签代码 */
        private String tabCode;
    }


    public void constructActionList(String currentRole, Long mid, UserAuthManager userAuthManager, UserConsultOrderInfo orderInfo) {
        List<String> detailActionAuth = userAuthManager.getDetailActionAuth(currentRole, orderInfo, mid);
        UserActionAuth userAuth = new UserActionAuth();
        userAuth.setActionsList(detailActionAuth);
        userAuth.setButtons(detailActionAuth);
        this.userActionAuth = userAuth;
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


    public void constructStatusBar(List<ComplaintFollowProcessGoOut> followProcessGoOuts, UserConsultOrderInfo orderInfo) {
        List<StatusData> statusDataList = new LinkedList<>();
        Map<String, ComplaintFollowProcessGoOut> processMap = followProcessGoOuts.stream().collect(Collectors.toMap(e -> e.getProcessType(), e -> e, (k1, k2) -> k1));

        for (ConsultStatusEnum value : ConsultStatusEnum.values()) {
            if (value.getCode() < orderInfo.getOrderStatus()) {
                StatusData build = StatusData.builder().status(value.getCode()).stateName(value.getBarBeenDesc()).doneYn(DoneYNEnum.YES.getCode()).build();
                statusDataList.add(build);
            } else if (Objects.equals(ConsultStatusEnum.FINISH_COMPLETE.getCode(), orderInfo.getOrderStatus())) {
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
            if (statusData.getStatus() == ConsultStatusEnum.PENDING_ORDER.getCode()) {
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
            if (statusData.getStatus() == ConsultStatusEnum.FIRST_RESPONSE_PENDING.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.FIRST_RESPONSE.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
            if (statusData.getStatus() == ConsultStatusEnum.FINISH_PENDING.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.APPLY_FINISH.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
            if (statusData.getStatus() == ConsultStatusEnum.FINISH_COMPLETE.getCode()) {
                ComplaintFollowProcessGoOut orDefault = processMap.getOrDefault(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode(), null);
                if (orDefault != null) {
                    statusData.setUpdateTime(DateUtil.getTimeStrByDate(orDefault.getCreateTime()));
                }
            }
        }
        this.statusBar = statusDataList.stream().filter(e -> !e.getStateName().equals("")).collect(Collectors.toList());
    }


    public void fillDetailTab(List<ConsultDetailTabEnum> detailTabByStatus, List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        if (CollUtils.isEmpty(detailTabByStatus)) {
            log.warn("detailTabByStatus is empty, consultNo:{}", consultOrderInfo.getConsultNo());
            return;
        }
        List<TabData> tabList = new ArrayList<>();
        detailTabByStatus.stream().forEach(e ->
                tabList.add(TabData.builder().tabCode(e.getType()).tabName(e.getDesc()).build())
        );
        // 若无跟进记录，不展示跟进记录tab
        List<TabData> collect = tabList;
        if (CollUtils.isEmpty(followProcessGoOuts)) {
            collect = tabList.stream().filter(e -> e.getTabCode() != ConsultDetailTabEnum.FOLLOW_UP_RECORDS.getType()).collect(Collectors.toList());
        }
        this.tabDataList = collect;
    }



}
