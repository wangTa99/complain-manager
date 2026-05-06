package com.wt.complaint.manage.domain.api.service.parameter.out.approve;


import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ComplaintPreNextSoOut implements Serializable {

    private static final long serialVersionUID = 2342566273768884665L;

    @ApiDocClassDefine(value = "preAuditId", description = "上一页审批id")
    private Long preAuditId;

    @ApiDocClassDefine(value = "nextAuditId", description = "下一页审批id")
    private Long nextAuditId;
}
