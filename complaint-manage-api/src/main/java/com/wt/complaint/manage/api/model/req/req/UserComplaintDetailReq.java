package com.wt.complaint.manage.api.model.req;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintDetailReq implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号", required = true)
    @NotBlank(message = "ucNo不能为空")
    private String ucNo;
}
