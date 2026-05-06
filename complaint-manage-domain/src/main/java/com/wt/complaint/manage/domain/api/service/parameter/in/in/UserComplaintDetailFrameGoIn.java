package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

@Data
public class UserComplaintDetailFrameGoIn implements Serializable {
    private static final long serialVersionUID = -1812249032902185150L;

    @ApiDocClassDefine(value = "ucNo", description = "举报单号", required = true)
    @NotBlank(message = "ucNo不能为空")
    private String ucNo;
}
