package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultCreateExpandDTO implements Serializable {
    @ApiDocClassDefine(value = "跟进客服mid", description = "客服MID")
    private CsEnquireInfo csEnquire;
}
