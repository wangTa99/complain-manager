package com.wt.complaint.manage.api.model.resp;

import lombok.Builder;
import lombok.Data;

/**
 * 城市数据
 */
@Data
@Builder
public class CityZoneDTO {

    /**
     * 城市id
     */
    private Integer cityZoneId;

    /**
     * 城市名称
     */
    private String cityZoneName;

    /**
     * 城市code
     */
    private String cityZoneCode;
}
