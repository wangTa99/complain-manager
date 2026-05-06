package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新咨询单处理人入参
 */
@Data
public class ConsultUpdateHandlerSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 接单处理�?mid
     */
    private Long operatorMid;

    /**
     * 操作�?mid
     */
    private Long operateMid;
}
