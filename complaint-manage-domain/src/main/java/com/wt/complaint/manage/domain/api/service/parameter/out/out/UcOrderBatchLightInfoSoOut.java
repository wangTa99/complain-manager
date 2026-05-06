package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.resp.UcOrderLightInfo;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/26 19:33
 */
@Data
public class UcOrderBatchLightInfoSoOut {
    @ApiDocClassDefine(value = "ucOrderInfoList", description = "举报单信息列�?)
    private List<UcOrderLightInfo> ucOrderInfoList;

}
