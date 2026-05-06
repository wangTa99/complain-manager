package com.wt.complaint.manage.api.model.req;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 客诉类单据详情请求参�?
 * @author MI
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintDetailFrameReq implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号", required = true)
    @NotBlank(message = "ucNo不能为空")
    private String ucNo;
}
