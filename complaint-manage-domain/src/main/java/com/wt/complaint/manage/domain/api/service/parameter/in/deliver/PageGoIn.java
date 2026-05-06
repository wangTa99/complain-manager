package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiankang
 * @date 2025/11/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageGoIn {

    @ApiDocClassDefine(value = "offset", description = "偏移�?)
    private Integer offset;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小")
    private Integer pageSize;

}
