package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinishOrderStatusMqMessageGoIn {
    /**
     * 操作类型�?完成�?取消
     */
    private Integer operateType;

    /**
     * 维保单号
     */
    private String workNo;

    /**
     * 1到店�?上门
     */
    private Integer workType;
}
