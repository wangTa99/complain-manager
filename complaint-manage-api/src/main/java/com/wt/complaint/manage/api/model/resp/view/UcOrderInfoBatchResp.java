package com.wt.complaint.manage.api.model.resp.view;

import com.wt.complaint.manage.api.model.resp.UcOrderViewInfo;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉类单据批量查询出�?
 * @author linjiehong
 * @date 2025/5/21 10:25
 */
@Data
public class UcOrderInfoBatchResp implements Serializable {
    @ApiDocClassDefine(value = "ucOrderInfoList", description = "举报单信息列�?)
    private List<UcOrderViewInfo> ucOrderViewInfoList;
}
