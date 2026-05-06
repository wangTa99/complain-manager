package com.wt.complaint.manage.api.model.resp.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * 举报单出�?
 * @author linjiehong
 * @date 2025/5/19 13:29
 */
@Data
public class JudgeOrderResp implements Serializable {
    @ApiDocClassDefine(value = "result", description = "结果")
    private String result;
}
