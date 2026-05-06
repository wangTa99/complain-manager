package com.wt.complaint.manage.api.model.req;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleComplaintDetailReq implements Serializable {

    private static final long serialVersionUID = 8543377916665180826L;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号", required = true)
    private String complaintNo;

    @ApiDocClassDefine(value = "mid", description = "当前登录人mid,非必�?)
    private Long mid;
}
