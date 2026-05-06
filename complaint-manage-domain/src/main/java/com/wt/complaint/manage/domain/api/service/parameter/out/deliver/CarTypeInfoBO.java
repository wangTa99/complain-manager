package com.wt.complaint.manage.domain.api.service.parameter.out.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车型版本信息
 *
 * @author huxiankang
 * @date 2025/10/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarTypeInfoBO {

    // 车型、车型版�?
    @ApiDocClassDefine(value = "carTypeName", description = "车型")
    private String carTypeName;
    @ApiDocClassDefine(value = "saleCarVersion", description = "车型版本")
    private String saleCarVersion;

}
