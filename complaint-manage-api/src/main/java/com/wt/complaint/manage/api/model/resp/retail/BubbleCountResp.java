package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 气泡数量返回
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BubbleCountResp implements Serializable {

    private static final long serialVersionUID = -7129359415103967071L;

    @ApiDocClassDefine(value = "firstResponsePendingCount", description = "待首响气泡数�?)
    private Integer firstResponsePendingCount;

    @ApiDocClassDefine(value = "remindCount", description = "催办气泡数量")
    private Integer remindCount;
}
