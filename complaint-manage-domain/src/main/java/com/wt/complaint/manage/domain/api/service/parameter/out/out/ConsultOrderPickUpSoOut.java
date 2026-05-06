package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单接单出�?
 */
@Data
public class ConsultOrderPickUpSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接单结果
     */
    private String result;
}
