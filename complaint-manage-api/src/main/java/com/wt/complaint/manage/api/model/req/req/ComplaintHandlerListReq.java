package com.wt.complaint.manage.api.model.req;

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
public class ComplaintHandlerListReq implements Serializable {
    @ApiDocClassDefine(value = "门店id")
    private String orgId;
}
