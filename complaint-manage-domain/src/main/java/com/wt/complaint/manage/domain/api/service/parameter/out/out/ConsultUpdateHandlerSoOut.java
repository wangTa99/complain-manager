package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新咨询单处理人出参
 */
@Data
public class ConsultUpdateHandlerSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 更新结果
     */
    private String result;
}
