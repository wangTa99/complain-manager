package com.wt.complaint.manage.api.model.resp.view;

import com.wt.complaint.manage.api.model.resp.UcOrderLightInfo;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉类单据轻量信息批量查询出�?
 * @author linjiehong
 * @date 2025/5/26 15:20
 */
@Data
public class UcOrderLightInfoBatchResp implements Serializable {
    @ApiDocClassDefine(value = "ucOrderInfoList", description = "举报单信息列�?)
    private List<UcOrderLightInfo> ucOrderInfoList;
}
