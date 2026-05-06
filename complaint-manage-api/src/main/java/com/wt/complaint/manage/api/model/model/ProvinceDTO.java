package com.wt.complaint.manage.api.model;

import com.wt.complaint.manage.api.model.resp.CityDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceDTO {

    /**
     * 省id
     */
    private Integer provinceId;
    /**
     * 省名�?
     */
    private String provinceName;
    /**
     * firstLetter
     */
    private String firstLetter;
    /**
     * 城市列表
     */
    private List<CityDTO> cityList;
}
