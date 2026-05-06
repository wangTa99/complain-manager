package com.wt.complaint.manage.api.model.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LittleZoneDTO {
    /**
     * 小区id
     */
    private Integer littleZoneId;
    /**
     * 小区code
     */
    private String littleZoneCode;
    /**
     * 小区名称
     */
    private String littleZoneName;
}
