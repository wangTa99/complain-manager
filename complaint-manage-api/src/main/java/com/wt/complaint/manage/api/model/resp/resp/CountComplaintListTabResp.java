package com.wt.complaint.manage.api.model.resp;

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
public class CountComplaintListTabResp implements Serializable {

    private static final long serialVersionUID = 4323463348291254190L;

    @ApiDocClassDefine(value = "total", description = "总数")
    private Integer total;

    @ApiDocClassDefine(value = "pendingOrderCount", description = "待接单数�?)
    private Integer pendingOrderCount;

    @ApiDocClassDefine(value = "dealingCount", description = "处理中数量，即待首响+待申请结案客诉单数量")
    private Integer dealingCount;

    @ApiDocClassDefine(value = "approachingTimeoutCount", description = "即将超时的投诉单数量，即首响剩余4个小时或者是结案剩余12个小时的客诉�?)
    private Integer approachingTimeoutCount;

    @ApiDocClassDefine(value = "finishEvaluationPendingCount", description = "待结案评估客诉单数量")
    private Integer finishEvaluationPendingCount;

    @ApiDocClassDefine(value = "onlyViewCount", description = "仅查阅的数量")
    private Integer onlyViewCount;

    @ApiDocClassDefine(value = "pendingReviewCount", description = "待复盘数量（服务投诉未复盘单�?)
    private Integer pendingReviewCount;
}
