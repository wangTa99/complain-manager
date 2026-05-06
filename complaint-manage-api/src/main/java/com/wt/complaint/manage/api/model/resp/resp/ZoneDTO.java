package com.wt.complaint.manage.api.model.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZoneDTO {
    /**
     * 大区id
     */
    private Integer zoneId;
    /**
     * 大区code
     */
    private String zoneCode;
    /**
     * 大区名称
     */
    private String zoneName;
}
