package com.wt.complaint.manage.api.model.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDTO {

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;
}
