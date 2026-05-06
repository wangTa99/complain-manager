package com.wt.complaint.manage.infrastructure.model;

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
public class RetailHasFirstResposeRecordFlagDO implements Serializable {
    /**
     * 跟进记录id
     */
    private Integer id;
}
