package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class StoreInfoGoOut {
    private String orgId;
    private String orgName;
    private String cityId;
    private Integer zoneId;
    private String zoneCode;
    private Integer littleZoneId;
    private String littleZoneCode;
    private Integer cityZoneId;
    private String cityZoneCode;
    /**
     * com.wt.maindatacommon.enums.BusinessModeEnums
     * phoneMi:3C米家
     * phoneAuthority:3C授权
     * phoneMerchant:3C一商一�?
     * phoneService:3C售后
     * carMiManagement:汽车直营
     * carAuthority:汽车授权
     * carAgency:汽车代理
     * carVdc:VDC车辆分配中心
     */
    private String businessMode;
}
