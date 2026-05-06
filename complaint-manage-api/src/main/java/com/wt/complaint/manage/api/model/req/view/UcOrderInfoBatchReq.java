package com.wt.complaint.manage.api.model.req.view;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客诉类单据批量查询请求参�?
 * @author linjiehong
 * @date 2025/5/21 10:23
 */
@Data
public class UcOrderInfoBatchReq implements Serializable {
    @ApiDocClassDefine(value = "ucNorList", description = "uc订单号列�?, required = true)
    List<String> ucNoList;
}
