package com.wt.complaint.manage.api.model.req.consult;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

/**
 * 查询统计项请求体
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
public class StatisticsItemReq implements java.io.Serializable {

    @ApiDocClassDefine(value = "orgId", description = "门店id")
    private String orgId;

    @ApiDocClassDefine(value = "onlyMe", description = "只看自己的标志，默认�?，代表全部，1 代表自己")
    private int onlyMe;
}
