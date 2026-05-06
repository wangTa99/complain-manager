package com.wt.complaint.manage.api.model.req.approve;


import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ComplaintPreNextReq implements Serializable {

    private static final long serialVersionUID = 2342566273768884665L;

    @ApiDocClassDefine(value = "id", description = "当前页审批id")
    private Long id;
}
