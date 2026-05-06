package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintHandlerInfo {
    @ApiDocClassDefine(value = "可被派单人员mid")
    private String mid;
    @ApiDocClassDefine(value = "可被派单人员姓名")
    private String name;
    @ApiDocClassDefine(value = "可被派单人员未完成的工单数量")
    private Integer notFinishedCnt;
}
