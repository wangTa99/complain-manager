package com.wt.complaint.manage.api.model.req.common;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用数据请求参数
 * @author linjiehong
 * @date 2025/5/19 13:33
 */
@Data
public class CommonDataReq implements Serializable {
    @ApiDocClassDefine(value = "orderType", description = "作业单类�?)
    private String orderType;
}
