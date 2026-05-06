package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WarrantyPeriodGoOut implements Serializable {

    private static final long serialVersionUID = -7225402487013025784L;

    @ApiDocClassDefine(value = "carInfo", description = "车辆信息")
    CarInfoDto carInfo;
    @ApiDocClassDefine(value = "warrantyInfo", description = "质保信息")
    WarrantyInfoDto warrantyInfo;

    @Data
    public static class CarInfoDto implements Serializable {
        private static final long serialVersionUID = 2156273214274801015L;
        @ApiDocClassDefine(
                value = "carImg",
                description = "车辆图片"
        )
        private String carImg;
        @ApiDocClassDefine(
                value = "vid",
                description = "vid"
        )
        private String vid;
        @ApiDocClassDefine(
                value = "carType",
                description = "车型"
        )
        private String carType;
        @ApiDocClassDefine(
                value = "carTypeName",
                description = "车型名称"
        )
        private String carTypeName;
        @ApiDocClassDefine(
                value = "carOwner",
                description = "车主名称"
        )
        private String carOwner;
        @ApiDocClassDefine(
                value = "carOwnerTel",
                description = "车主电话"
        )
        private String carOwnerTel;
        @ApiDocClassDefine(
                value = "mileage",
                description = "当前公里"
        )
        private Integer mileage;
        @ApiDocClassDefine(
                value = "deliveryDate",
                description = "交付日期"
        )
        private String deliveryDate;
        @ApiDocClassDefine(
                value = "vin",
                description = "vin"
        )
        private String vin;
        @ApiDocClassDefine(
                value = "deliveryStoreId",
                description = "交付门店ID"
        )
        private String deliveryStoreId;
        @ApiDocClassDefine(
                value = "deliveryStore",
                description = "交付门店"
        )
        private String deliveryStore;
        @ApiDocClassDefine(
                value = "productionDate",
                description = "生产日期"
        )
        private String productionDate;
        @ApiDocClassDefine(
                value = "invoiceDate",
                description = "开票日�?
        )
        private String invoiceDate;
    }

    @Data
    public static class WarrantyInfoDto implements Serializable {
        private static final long serialVersionUID = 481350880917659331L;
        @ApiDocClassDefine(value = "zc", description = "整车质保信息")
        private WarrantyInfoDetailDto zc;
        @ApiDocClassDefine(value = "sd", description = "三电质保信息")
        private WarrantyInfoDetailDto sd;
        @ApiDocClassDefine(value = "ys", description = "易损质保信息")
        private WarrantyInfoDetailDto ys;
//        private List<WarrantyInfoDetailDto> ys;
    }

    @Data
    public static class WarrantyInfoDetailDto implements Serializable {
        private static final long serialVersionUID = 8310755216499855920L;
        @ApiDocClassDefine(value = "originalStartDate", description = "质保开始时�?)
        private Long originalStartTime;
        @ApiDocClassDefine(value = "originalEndDate", description = "质保结束时间")
        private Long originalEndTime;
        @ApiDocClassDefine(value = "originalStartMileage", description = "质保开始里�?)
        private Integer originalStartMileage;
        @ApiDocClassDefine(value = "originalEndMileage", description = "质保结束里程")
        private Integer originalEndMileage;
        @ApiDocClassDefine(value = "extendedMileageInfo", description = "延保里程列表")
        private List<ExtendedMileageInfo> extendedMileageInfo;
        @ApiDocClassDefine(value = "extendedPeriodInfo", description = "延保周期列表")
        private List<ExtendedPeriodInfo> extendedPeriodInfo;
        @ApiDocClassDefine(value = "warrantyEffect", description = "是否生效")
        private Boolean warrantyEffect;
    }


    public static class ExtendedPeriodInfo implements Serializable {
        private static final long serialVersionUID = -1660970112255539551L;
        @ApiDocClassDefine(
                value = "startDate",
                description = "开始时�?
        )
        private Long startTime;
        @ApiDocClassDefine(
                value = "endDate",
                description = "结束时间"
        )
        private Long endTime;
    }


    public static class ExtendedMileageInfo implements Serializable {
        private static final long serialVersionUID = 1046810348994377522L;
        @ApiDocClassDefine(
                value = "startMileage",
                description = "开始里�?
        )
        private Integer startMileage;
        @ApiDocClassDefine(
                value = "endMileage",
                description = "结束里程"
        )
        private Integer endMileage;
    }

}
