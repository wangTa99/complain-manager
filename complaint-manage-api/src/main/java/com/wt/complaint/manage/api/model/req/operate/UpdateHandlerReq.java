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
public class UpdateHandlerReq implements Serializable {
    @ApiDocClassDefine(value = "客诉单号")
    private String complaintNo;
    @ApiDocClassDefine(value = "咨询单号")
    private String consultNo;
    @ApiDocClassDefine(value = "被改派的处理人mid")
    private String handlerMid;
}
