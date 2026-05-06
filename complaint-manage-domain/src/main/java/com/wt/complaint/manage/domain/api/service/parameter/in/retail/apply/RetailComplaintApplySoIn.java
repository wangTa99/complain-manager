package com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 零售客诉单申请入�?
 * 封装客诉单信息、申请门店信息、改派门店信息等
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintApplySoIn {
    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 零售客诉单状�?
     */
    private Integer orderStatus;

    /**
     * 申请门店
     */
    private String applyOrgId;

    /**
     * 申请门店名称
     */
    private String applyOrgName;

    /**
     * 改派门店id
     */
    private String desOrgId;

    /**
     * 改派门店名称
     */
    private String desOrdName;

    /**
     * 大区id
     */
    private String zoneId;

    /**
     * 汽车小区id
     */
    private String littleZoneId;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 改派内容
     */
    private String reassignRemark;

    /**
     * 登陆人mid
     */
    private Long createMid;

    private String createName;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    private Integer complaintType;

    private String complaintTypeName;

    private String problemCategory;

    /**
     * 问题描述
     */
    private String problemDesc;

    // 投诉场景
    private String complaintScene;

    private Map<String, StoreInfoGoOut> storeMap;
}
