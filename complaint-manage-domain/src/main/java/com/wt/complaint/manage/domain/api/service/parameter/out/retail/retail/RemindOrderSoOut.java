package com.wt.complaint.manage.domain.api.service.parameter.out.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 催单出参
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemindOrderSoOut implements Serializable {
    @ApiDocClassDefine(value = "result", description = "结果")
    private String result;
}
