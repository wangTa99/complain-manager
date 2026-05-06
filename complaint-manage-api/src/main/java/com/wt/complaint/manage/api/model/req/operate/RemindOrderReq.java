package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemindOrderReq implements Serializable {
    @Deprecated
    @ApiDocClassDefine(value = "complaintNo", required = true, description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "ucNo", required = true, description = "客诉类单�?)
    private String ucNo;

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    private String consultNo;

    @ApiDocClassDefine(value = "orderRemindInfo", description = "客服催单时填写的催单信息")
    private String orderRemindInfo;
}
