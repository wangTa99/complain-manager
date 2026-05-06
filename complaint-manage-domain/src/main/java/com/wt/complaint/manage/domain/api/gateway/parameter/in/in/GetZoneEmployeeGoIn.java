package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

/**
 * 获取大区员工信息
 * 封装员工的大区id、岗位id和权限状�?
 */
@Data
@Builder
public class GetZoneEmployeeGoIn {

    /**
     * 大区id
     */
    private Integer zoneId;

    /**
     * 岗位id
     */
    private Integer positionId;

    /**
     * 权限状�?0-无效 1-有效
     */
    private Integer privilegeState;
}
