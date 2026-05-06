package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/26 19:31
 */
@Data
public class UcOrderBatchLightInfoSoIn {
    /**
     * 业务单号：工单号
     */
    private List<String> bizOrderList;

    /**
     * 关联信息
     */
    List<ComplaintRelationOrderGoOut> relationList;
}
