package com.wt.complaint.manage.domain.api.gateway.parameter.out;

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
public class BubbleCountGoOut {
    /**
     * 待首响气泡数�?
     */
    private Integer firstResponsePendingCount;

    /**
     * 催办气泡数量
     */
    private Integer remindCount;
}
