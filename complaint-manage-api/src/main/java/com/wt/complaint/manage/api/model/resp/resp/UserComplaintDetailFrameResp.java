package com.wt.complaint.manage.api.model.resp;

import com.wt.car.common.privacy.annotation.MaskAndEncrypted;
import com.wt.car.common.privacy.enums.MaskTypeEnum;
import com.wt.car.common.privacy.vo.BaseVO;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户举报单详情页数据
 * @author linjiehong
 * @date 2025/5/26 15:22
 */
@Data
public class UserComplaintDetailFrameResp extends BaseVO {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "soNo", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "superTicketNo", description = "工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "orderStatus", description = "举报单状�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private Integer orderStatus;

    @ApiDocClassDefine(value = "orderStatusName", description = "举报单状态名�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private String orderStatusName;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
    @ApiDocClassDefine(value = "contactName", description = "联系人姓�?)
    private String contactName;

    @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
    @ApiDocClassDefine(value = "contactPhone", description = "联系电话")
    private String contactPhone;

    @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
    @ApiDocClassDefine(value = "vin", description = "车架号Vin")
    private String vin;

    @ApiDocClassDefine(value = "reminderTimes", description = "催单次数")
    private Integer reminderTimes;

    @ApiDocClassDefine(value = "statusBar", description = "举报单进度条信息")
    private List<UserComplaintDetailFrameResp.StatusData> statusBar;

    @ApiDocClassDefine(value = "carInfo", description = "车辆信息")
    private UserComplaintDetailFrameResp.CarInfo carInfo;

    @ApiDocClassDefine(value = "warrantyInfo", description = "车辆质保相关标签")
    private UserComplaintDetailFrameResp.WarrantyInfo warrantyInfo;

    @ApiDocClassDefine(value = "userComplaintOrderInfo", description = "举报单基本信�?)
    private UserComplaintDetailFrameResp.UserComplaintOrderInfo userComplaintOrderInfo;

    @ApiDocClassDefine(value = "tabDataList", description = "举报单详情页tab展示列表 跟进记录 followUpRecords，举报信�?userComplaintInfo，线上服务记�?onlineServiceRecords")
    private List<UserComplaintDetailFrameResp.TabData> tabDataList;

    @ApiDocClassDefine(value = "useComplaintActionAuth", description = "用户操作按钮")
    private UseComplaintActionAuth useComplaintActionAuth;

    /**
     * 车辆信息
     * @author linjiehong
     */
    @Data
    public static class CarInfo extends BaseVO {
        @ApiDocClassDefine(value = "carType", description = "车型")
        private String carType;

        @ApiDocClassDefine(value = "carImg", description = "车图�?)
        private String carImg;

        @MaskAndEncrypted(maskType = MaskTypeEnum.VIN)
        @ApiDocClassDefine(value = "vin", description = "车辆VIN码，即车架号")
        private String vin;

        @ApiDocClassDefine(value = "vid", description = "车辆vid")
        private String vid;

        @MaskAndEncrypted(maskType = MaskTypeEnum.NAME)
        @ApiDocClassDefine(value = "carOwner", description = "车主尊称")
        private String carOwner;

        @MaskAndEncrypted(maskType = MaskTypeEnum.PHONE)
        @ApiDocClassDefine(value = "carOwnerTel", description = "车主手机号码")
        private String carOwnerTel;

        @ApiDocClassDefine(value = "currentVersion", description = "软件版本")
        private String currentVersion;

        @ApiDocClassDefine(value = "carTagList", description = "车辆标签列表，如用户关怀�?)
        private List<LabelDTO> carTagList;

        @ApiDocClassDefine(value = "itemMap", description = "汽车配置信息 key:identityEnum  value itemValue.name")
        private Map<String, String> itemMap;
    }

    /**
     * 举报单进度条信息
     */
    @Data
    public static class UserComplaintOrderInfo implements Serializable {
        @ApiDocClassDefine(value = "ucNo", description = "举报单号")
        private String ucNo;

        @ApiDocClassDefine(value = "createTime", description = "创建时间")
        private String createTime;

        @ApiDocClassDefine(value = "orgId", description = "门店id")
        private String orgId;

        @ApiDocClassDefine(value = "orgName", description = "门店名称")
        private String orgName;

        @ApiDocClassDefine(value = "handleName", description = "处理�?)
        private String handleName;
    }

    @Data
    public static class TabData implements Serializable {

        private static final long serialVersionUID = 5503324723653893981L;

        @ApiDocClassDefine(value = "tabCode", description = "tab编码")
        private String tabCode;

        @ApiDocClassDefine(value = "tabName", description = "tab名称")
        private String tabName;
    }

    /**
     * 举报单状态信�?
     */
    @Data
    @Builder
    public static class StatusData implements Serializable {

        private static final long serialVersionUID = 8471466162436255837L;

        @ApiDocClassDefine(value = "stateName", description = "进度节点名称")
        private String stateName;

        @ApiDocClassDefine(value = "doneYn", description = "是否已完�?0:未完�?1:已完�?)
        private Integer doneYn;

        @ApiDocClassDefine(value = "updateTime", description = "更新时间")
        private String updateTime;
    }

    /**
     * 车辆质保相关标签
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarrantyInfo implements Serializable {
        @ApiDocClassDefine(value = "warrantyEffectSd", description = "保修有效�?三电")
        private Boolean warrantyEffectSd;

        @ApiDocClassDefine(value = "warrantyEffectYs", description = "保修有效�?延保")
        private Boolean warrantyEffectYs;

        @ApiDocClassDefine(value = "warrantyEffectZc", description = "保修有效�?整车")
        private Boolean warrantyEffectZc;
    }
}
