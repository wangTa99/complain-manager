package com.wt.complaint.manage.domain.api.service.parameter.out.retail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 零售客诉单详情响�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailHasFirstResposeRecordFlagSoOut implements Serializable {
    /**
     * 是否有首响记录标�?true-�?false-�?
     */
    private boolean hasFirstResposeRecordFlag;
}
