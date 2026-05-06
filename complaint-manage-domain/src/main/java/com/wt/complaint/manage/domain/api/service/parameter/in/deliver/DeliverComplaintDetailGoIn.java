package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintDetailGoIn extends DeliverComplaintDataPermissionGoIn {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    private String drNo;

    @ApiDocClassDefine(value = "operatorPositionEnum", description = "登录人岗�?, required = true)
    private DeliverPositionEnum operatorPositionEnum;
}
