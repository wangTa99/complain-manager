package com.wt.complaint.manage.domain.api.service.parameter.out.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintAuditListSoOut implements Serializable {

    private static final long serialVersionUID = 7279365060458843183L;

    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Long total;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<ComplaintAuditSoOut> dataList;
}
