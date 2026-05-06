package com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcConfigGoIn {

    // 模块 key
    private String moduleKey;

    // mid
    private String mid;

    // 门店 id
    private String orgId;

    // 当前角色 key
    private String currRole;
}
