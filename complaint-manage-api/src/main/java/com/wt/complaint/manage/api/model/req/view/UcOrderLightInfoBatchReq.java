package com.wt.complaint.manage.api.model.req.view;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉类单据批量查询请求参�?
 * @author linjiehong
 * @date 2025/5/26 15:19
 */
@Data
public class UcOrderLightInfoBatchReq implements Serializable {
    @ApiDocClassDefine(value = "bizOrderList", description = "业务单号列表", required = true)
    private List<String> bizOrderList;
}
