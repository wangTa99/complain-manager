package com.wt.complaint.manage.api.model.req.deliver;

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

    @ApiDocClassDefine(value = "orgIds", description = "门店ids, �?,'拼接字符�?)
    private String orgIds;

}
