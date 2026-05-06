package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

import java.util.List;

@Data
public class GetDynamicInfoResponseGoOut {
    private List<DynamicInfoItemDto> items;

    @Data
    public static class DynamicInfoItemDto {
        /**
         * vin�?
         */
        private String vin;
        /**
         * vid
         */
        private String vid;
        /**
         * 剩余电量
         */
        private String power;

        /**
         * 行驶总里�?
         */
        private String mileage;

        /**
         * 当前车机版本
         */
        private String sysVersion;

        /**
         * 车机模式
         */
        private String carMode;

        /**
         * 充电状�?
         */
        private String chargingState;

        /**
         * 充电状态码
         */
        private int chargingStateCode;
    }

}
