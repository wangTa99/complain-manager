package com.wt.complaint.manage.domain.api.service.parameter.out.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 气泡数量查询结果
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BubbleCountSoOut {
    @ApiDocClassDefine(value = "firstResponsePendingCount", description = "待首响气泡数�?)
    private Integer firstResponsePendingCount;

    @ApiDocClassDefine(value = "remindCount", description = "催办气泡数量")
    private Integer remindCount;
}
