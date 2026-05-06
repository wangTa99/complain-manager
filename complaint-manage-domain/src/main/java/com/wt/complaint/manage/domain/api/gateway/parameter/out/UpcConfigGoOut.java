package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcConfigGoOut {
    // 资源 key
    private String resourceTag;

    // 资源 名称
    private String resourceName;
}
