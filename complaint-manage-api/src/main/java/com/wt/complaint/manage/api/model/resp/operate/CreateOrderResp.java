package com.wt.complaint.manage.api.model.resp.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 建单出参
 * @author linjiehong
 * @date 2025/5/19 10:49
 */
@Data
public class CreateOrderResp implements Serializable {
    @ApiDocClassDefine(value = "作业单号", description = "建单成功后返回的作业单号")
    private String workNo;
}
