package com.wt.complaint.manage.api.model.resp.consult;

import com.wt.car.common.privacy.annotation.MaskAndEncrypted;
import com.wt.car.common.privacy.enums.MaskTypeEnum;
import com.wt.car.common.privacy.vo.BaseVO;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.DetailTabEnum;
import com.wt.complaint.manage.api.model.enums.DoneYNEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
 * 咨询单详情响�?
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultDetailResp extends BaseVO {

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
    @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
    private String customerName;
    /** 客户电话 */
    @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
    private String customerPhone;
    /** 车牌�?*/
    private String carNo;
    /** 车辆VIN */
    @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
    private String vin;
    /** 咨询类型 */
    private Integer consultType;
    /** 咨询类型名称 */
    private String consultTypeName;
    /** 联系�?*/
    @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
    private String contactPerson;
    /** 联系人电�?*/
    @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
    private String contactPhone;
    /** 紧急标�?*/
    private String urgentFlag;
    /** 创建人姓�?*/
    private String creator;
    /** 创建时间（格�?yyyy-MM-dd HH:mm:ss�?*/
    private String createTime;
    /** 是否关联维保�?*/
    private String isLinkedMrOrder;
    /** 超级工单�?/
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
    public static class CompleteInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        // 处理完成时间
        private String completeTime;

        // 完成人mid
        private Long completeUser;

        // 完成人姓�?
        private String completeUserName;

        // 处理结果
        private String completeResult;

        // 解决方案
        private String solution;
    }

    /**
     * 咨询单信�?
     */
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
     * 车辆信息
     */
    @Data
    public static class CarInfo  extends BaseVO  {
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
        @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
        private String vin;

        /**
         * 车辆vid
         */
        private String vid;

        /**
         * 车主尊称
         */
        @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
        private String carOwner;

        /**
         * 车主手机号码
         */
        @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
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

}