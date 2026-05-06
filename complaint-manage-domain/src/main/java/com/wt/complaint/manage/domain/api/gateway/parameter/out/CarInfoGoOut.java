package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
public class CarInfoGoOut {
    private String vin;

    private String vid;

    /**
     * 车型
     */
    private String carType;

    /**
     * 车辆图片
     */
    private String carImg;

    /**
     * 车辆用�?
     */
    private String carPurposeName;

    private CarTagGoOut carTag;

    /**
     * 汽车配置信息key:identityEnum  value itemValue.name
     */
    private Map<String, String> itemMap;
}
