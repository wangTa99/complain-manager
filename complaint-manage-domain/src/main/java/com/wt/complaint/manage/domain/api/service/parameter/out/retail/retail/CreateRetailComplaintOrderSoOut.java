package com.wt.complaint.manage.domain.api.service.parameter.out.retail;


import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建零售投诉单响应参�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRetailComplaintOrderSoOut implements Serializable {
    @ApiDocClassDefine(value = "客诉单号", description = "建单成功后返回的客诉单号")
    private String workNo;
}
