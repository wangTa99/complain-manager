package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建咨询单出�?
 */
@Data
public class CreateConsultOrderSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;
}
