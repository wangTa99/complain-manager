package com.wt.complaint.manage.api.model.req.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

/**
 * 催单提醒
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailRemindOrderReq implements Serializable {

    @ApiDocClassDefine(value = "drNo", required = true, description = "客诉单号")
    @NotBlank(message = "drNo不能为空")
    private String drNo;

    @ApiDocClassDefine(value = "orderRemindInfo", description = "客服催单时填写的催单信息")
    @NotBlank(message = "orderRemindInfo不能为空")
    private String orderRemindInfo;
}
