package com.wt.complaint.manage.api.model.req.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAuditDetailReq implements Serializable {

    private static final long serialVersionUID = 1015616565726267145L;

    @ApiDocClassDefine(value = "id", description = "审批id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

}
