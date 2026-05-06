package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 气泡数据统计请求参数
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticRetailCountGoIn implements Serializable {

    private static final long serialVersionUID = -854389302185416006L;
    
    /**
     * 用户mid
     */
    private String mid;

    /**
     * tab 1-待接�? 2-处理�? 3-即将超时, 4-已结�?
     */
    private Integer tab;

    /**
     * 数据类型 0-大区 1-小区 2-门店
     */
    private Integer type;

    /**
     * 下拉框选择�?
     */
    private String value;

    /**
     * 大区id
     */
    private String zoneId;

    /**
     * 小区id
     */
    private String littleZoneId;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 搜索条件
     */
    private String searchTerm;

    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 联系人电话检索码
     */
    private String contactPhoneMd5;

    /**
     * 下钻门店
     */
    private String orgCode;

    /**
     * 售后工作台权�?
     */
    private StaticRetailCountGoIn.AfterSaleWorkbenchPermissionGroup afterSaleWorkbenchPermissionGroup;

    @NoArgsConstructor
    @Data
    public static class AfterSaleWorkbenchPermissionGroup implements Serializable {

        private static final long serialVersionUID = -5977612121852305005L;
        /**
         * 0-所有全�?1-大区权限 2-小区权限 3-门店权限
         */
        private Integer afterSaleWorkbenchPermissionType;

        /**
         * 汽车大区岗位信息
         */
        private List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList;

        /**
         * 汽车小区岗位信息
         */
        private List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList;

        /**
         * 门店岗位
         */
        private List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList;
    }
}
