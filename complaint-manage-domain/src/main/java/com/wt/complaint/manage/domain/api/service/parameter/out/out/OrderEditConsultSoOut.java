package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑咨询单出�?
 */
@Data
public class OrderEditConsultSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编辑结果
     */
    private String result;
}
