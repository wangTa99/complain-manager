package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 改派入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintReassignProcessGoIn {

    @ApiDocClassDefine(value = "operatorPositionEnum", description = "角色名称", required = true)
    private DeliverPositionEnum operatorPositionEnum;

    @ApiDocClassDefine(value = "operatorMid", description = "操作人mid", required = true)
    private Long operatorMid;
    @ApiDocClassDefine(value = "operatorName", description = "操作人name", required = true)
    private String operatorName;

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    private String drNo;

    @ApiDocClassDefine(value = "orgId", description = "改派门店code", required = true)
    private String orgId;

    @ApiDocClassDefine(value = "reassignOperatorPositionId", description = "改派岗位", required = true)
    private Integer reassignOperatorPositionId;

    @ApiDocClassDefine(value = "reassignOperatorMid", description = "改派人员mid", required = true)
    private Long reassignOperatorMid;

    @ApiDocClassDefine(value = "reassignDesc", description = "改派描述", required = true)
    private String reassignDesc;

    @ApiDocClassDefine(value = "attachmentList", description = "附件")
    private List<AttachmentGoIn> attachmentList;

}
