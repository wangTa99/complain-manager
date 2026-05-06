package com.wt.complaint.manage.domain.api.service.parameter.in.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDetailForCustomerServiceSoIn implements Serializable {

    private static final long serialVersionUID = 8438724519342177664L;

    @ApiDocClassDefine(value = "complaintNo",
            description = "客诉单号, 仅用于客服工作台,查询对应客诉单最新的结案审批记录",
            required = true)
    @NotBlank(message = "complaintNo不能为空")
    private String complaintNo;

    /**
     * 登录人mid
     */
    private Long mid;
}
