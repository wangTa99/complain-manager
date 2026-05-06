package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 区域列表查询请求参数
 * 封装区域查询的多条件参数
 */
@Data
@Builder
public class ZoneListGoIn {
    private List<Integer> zoneIdList;
    private List<String> zoneCodeList;
    private List<Integer> zoneScopeList;
    private String fuzzyName;
    private String zoneName;
    private Integer enabled;
    private Integer pageIndex;
    private Integer pageSize;
}
