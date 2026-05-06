package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintDetailGoIn implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号", required = true)
    @NotBlank(message = "ucNo不能为空")
    private String ucNo;
}
