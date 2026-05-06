package com.wt.complaint.manage.domain.api.service.parameter.out.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交付客诉统计项响应对�?
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverStatisticsItemGoOut implements Serializable {

    @ApiDocClassDefine(value = "pendingFirstResponseCount", description = "待首响数�?)
    private Integer pendingFirstResponseCount;

    @ApiDocClassDefine(value = "handlingCount", description = "跟进中数�?)
    private Integer handlingCount;

    @ApiDocClassDefine(value = "pendingResponsibilityCount", description = "待判责数�?)
    private Integer pendingResponsibilityCount;

    @ApiDocClassDefine(value = "remindCount", description = "用户催单数量")
    private Integer remindCount;

    @ApiDocClassDefine(value = "firstResponseTimeoutCount", description = "首响超时数量")
    private Integer firstResponseTimeoutCount;

    @ApiDocClassDefine(value = "closingTimeoutCount", description = "结案超时数量")
    private Integer finishTimeoutCount;

}
