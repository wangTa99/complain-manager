package com.wt.complaint.manage.api.model.req.deliver;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
/**
 * 改派请求�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintReassignReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "orgId", description = "改派门店code", required = true)
    @NotBlank(message = "orgId不能为空")
    private String orgId;

    @ApiDocClassDefine(value = "reassignOperatorPositionId", description = "改派岗位", required = true)
    @NotNull(message = "reassignOperatorPositionId不能为空")
    private Integer reassignOperatorPositionId;

    @ApiDocClassDefine(value = "reassignOperatorMid", description = "改派人员mid", required = true)
    @NotNull(message = "reassignOperatorMid不能为空")
    private Long reassignOperatorMid;

    @ApiDocClassDefine(value = "reassignDesc", description = "改派描述", required = true)
    @NotBlank(message = "reassignDesc不能为空")
    private String reassignDesc;

    @ApiDocClassDefine(value = "attachmentList", description = "附件")
    private List<Attachment> attachmentList;

}
