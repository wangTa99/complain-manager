package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 零售查询是否有首响记录标识请�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailHasFirstResponseRecordFlagGoIn implements Serializable {

    /**
     * 客诉单号
     */
    private String drNo;
}
