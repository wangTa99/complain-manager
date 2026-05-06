package com.wt.complaint.manage.api.model.req.deliver;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class QueryReassignEmployeeReq implements Serializable {

    @ApiDocClassDefine(value = "orgId", description = "改派门店code", required = true)
    @NotBlank(message = "orgId不能为空")
    private String orgId;

    @ApiDocClassDefine(value = "positionId", description = "改派岗位", required = true)
    @NotNull(message = "positionId不能为空")
    private Integer positionId;

}
