package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 流程操作入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintProcessGoIn {

    @ApiDocClassDefine(value = "operatorPositionEnum", description = "角色名称", required = true)
    private DeliverPositionEnum operatorPositionEnum;

    @ApiDocClassDefine(value = "operatorMid", description = "操作人mid", required = true)
    private Long operatorMid;
    @ApiDocClassDefine(value = "operatorName", description = "操作人name", required = true)
    private String operatorName;

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    private String drNo;

}
