package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
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
public class RetailHasFirstResposeRecordFlagResp implements Serializable {

    private static final long serialVersionUID = -5876593852837762183L;

    @ApiDocClassDefine(value = "hasFirstResposeRecordFlag", description = "是否有首响记录标�?true-�?false-�?)
    private boolean hasFirstResposeRecordFlag;
}
