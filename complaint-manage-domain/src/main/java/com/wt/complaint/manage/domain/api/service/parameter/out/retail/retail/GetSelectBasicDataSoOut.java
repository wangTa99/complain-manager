package com.wt.complaint.manage.domain.api.service.parameter.out.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 气泡数量返回
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSelectBasicDataSoOut implements Serializable {

    @ApiDocClassDefine(value = "bigZoneList", description = "大区数据")
    private List<ZonePosition> bigZoneList;

    @ApiDocClassDefine(value = "littleZoneList", description = "小区数据")
    private List<ZonePosition> littleZoneList;

    @ApiDocClassDefine(value = "storePositionList", description = "门店数据")
    private List<StorePosition> storePositionList;

    /**
     * 区域岗位信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ZonePosition implements Serializable {
        @ApiDocClassDefine(value = "zoneId", description = "区域id")
        private Integer zoneId;
        @ApiDocClassDefine(value = "zoneName", description = "区域名称")
        private String zoneName;
    }

    /**
     * 门店岗位信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StorePosition implements Serializable {
        @ApiDocClassDefine(value = "orgId", description = "门店id")
        private String orgId;
        @ApiDocClassDefine(value = "storeName", description = "门店名称")
        private String storeName;
    }

}

