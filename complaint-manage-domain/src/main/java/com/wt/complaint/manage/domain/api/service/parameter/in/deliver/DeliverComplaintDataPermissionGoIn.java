package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据权限相关字段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverComplaintDataPermissionGoIn {

    /**
     * 数据权限大区id
     */
    private List<Integer> permissionZoneId;

    /**
     * 数据权限小区id
     */
    private List<Integer> permissionLittleZoneId;

    /**
     * 数据权限门店id
     */
    private List<String> permissionOrgId;

    /**
     * 数据权限跟进人id
     */
    private List<Long> permissionOperatorMid;

}
