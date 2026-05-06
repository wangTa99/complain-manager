package com.wt.complaint.manage.api.model.resp.consult;

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
public class ConsultStatisticsItemResp implements Serializable {

    @ApiDocClassDefine(value = "pendingReceiveCount", description = "待接单数�?)
    private Integer pendingReceiveCount;

    @ApiDocClassDefine(value = "pendingFirstResponseCount", description = "待首响数�?)
    private Integer pendingFirstResponseCount;

    @ApiDocClassDefine(value = "pendingCloseCount", description = "待结案数�?)
    private Integer pendingCloseCount;

    @ApiDocClassDefine(value = "completedCount", description = "已完成数�?)
    private Integer completedCount;


}