package com.wt.complaint.manage.api.model.resp.common;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用下拉选项返回�?
 *
 * @author huxiankang
 * @date 2025/6/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonOptionResp {

    @ApiDocClassDefine(value = "isDefault", description = "是否为默认选项")
    private boolean isDefault;

    @ApiDocClassDefine(value = "statusCode", description = "下拉选项code")
    private Object statusCode;

    @ApiDocClassDefine(value = "statusName", description = "下拉选项name")
    private String statusName;

}
