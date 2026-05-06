package com.wt.complaint.manage.api.model.resp.operate;

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
public class CreateComplaintOrderResp implements Serializable {
    @ApiDocClassDefine(value = "客诉单号", description = "建单成功后返回的客诉单号")
    private String workNo;
}
