package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 查询改派人入�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryReassignEmployeeGoIn implements Serializable {

    @ApiDocClassDefine(value = "orgId", description = "改派门店code", required = true)
    @NotBlank(message = "orgId不能为空")
    private String orgId;

    @ApiDocClassDefine(value = "positionId", description = "改派岗位", required = true)
    @NotNull(message = "positionId不能为空")
    private Integer positionId;

}
