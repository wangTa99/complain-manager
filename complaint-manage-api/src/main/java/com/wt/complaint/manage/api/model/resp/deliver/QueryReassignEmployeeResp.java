package com.wt.complaint.manage.api.model.resp.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

/**
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Data
public class QueryReassignEmployeeResp {

    @ApiDocClassDefine(value = "miId", description = "miId", required = true)
    private Long miId;
    @ApiDocClassDefine(value = "name", description = "名字", required = true)
    private String name;

}
