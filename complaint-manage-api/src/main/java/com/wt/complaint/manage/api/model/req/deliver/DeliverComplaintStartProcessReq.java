package com.wt.complaint.manage.api.model.req.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
/**
 * 开始处理请求体
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverComplaintStartProcessReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", description = "客诉单号", required = true)
    @NotBlank(message = "drNo不能为空")
    private String drNo;
}
