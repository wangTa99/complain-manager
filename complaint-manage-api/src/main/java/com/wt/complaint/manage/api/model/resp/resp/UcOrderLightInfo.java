package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 客诉类单据轻量信�?
 * @author linjiehong
 * @date 2025/5/26 15:22
 */
@Data
public class UcOrderLightInfo implements Serializable {
    @ApiDocClassDefine(value = "ucNo", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "bizNo", description = "业务单号：维保单")
    private String bizNo;

    @ApiDocClassDefine(value = "createTime", description = "创建时间：举报时�?)
    private String createTime;
}
