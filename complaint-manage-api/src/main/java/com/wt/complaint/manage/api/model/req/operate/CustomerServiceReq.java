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
public class CustomerServiceReq implements Serializable {
    @ApiDocClassDefine(value = "工单�?)
    private String stNo;
    @ApiDocClassDefine("新修改的客服人员mid")
    private Long customerServiceMid;
}
