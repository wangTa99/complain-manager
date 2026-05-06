package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 车载业务员工岗位信息返回结果
 * 封装了员工在不同组织下的岗位信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarEmployeeInfoGoOut implements Serializable {

    private static final long serialVersionUID = -4335311272970717334L;

    /**
     * 总部岗位信息
     */
    private List<ChannelPositionInfo> headPositionsInfoList;
    /**
     * 渠道岗位信息
     */
    private List<ChannelPositionInfo> channelPositionInfoList;
    /**
     * 大区岗位信息
     */
    private List<ZonePositionInfo> bigZonePositionsInfoList;
    /**
     * 小区岗位信息
     */
    private List<ZonePositionInfo> littleZonePositionsInfoList;
    /**
     * 城市岗位信息
     */
    private List<ZonePositionInfo> cityZonePositionInfoList;
    /**
     * 门店岗位信息
     */
    private List<StorePositionInfo> storePositionInfoList;

    /**
     * 总部岗位信息
     */
    @Data
    @AllArgsConstructor
    public static class ChannelPositionInfo implements Serializable {

        private static final long serialVersionUID = 8561843923966426610L;

        /**
         * 岗位id
         */
        private Integer positionId;
        /**
         * 岗位名称
         */
        private String positionName;
    }

    /**
     * 区域岗位信息
     */
    @Data
    @AllArgsConstructor
    public static class ZonePositionInfo implements Serializable {

        private static final long serialVersionUID = 595485722593062176L;

        /**
         * 岗位id
         */
        private Integer positionId;
        /**
         * 岗位名称
         */
        private String positionName;
        /**
         * 区域id
         */
        private Integer zoneId;
        /**
         * 区域名称
         */
        private String zoneName;
    }

    /**
     * 门店岗位信息
     */
    @Data
    @AllArgsConstructor
    public static class StorePositionInfo implements Serializable {

        private static final long serialVersionUID = 4020744265110872686L;

        /**
         * 岗位id
         */
        private Integer positionId;
        /**
         * 门店id
         */
        private String orgId;
        /**
         * 门店名称
         */
        private String storeName;
    }
}
