package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetComplaintHandlerInfo {
    /**
     * 可被派单人员mid
     */
    private String mid;
    /**
     * 可被派单人员姓名
     */
    private String name;
    /**
     * 可被派单人员未完成的工单数量
     */
    private Integer notFinishedCnt;
}
