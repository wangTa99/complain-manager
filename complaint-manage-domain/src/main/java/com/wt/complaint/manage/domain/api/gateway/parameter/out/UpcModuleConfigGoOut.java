package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcModuleConfigGoOut {

    // 角色 key
    private String roleKey;

    // 模块 key
    private String moduleKey;

    // 资源 key
    private List<UpcConfigGoOut> configs;
}
