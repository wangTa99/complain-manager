package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单改派出�?
 */
@Data
public class ConsultReassignSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 改派结果
     */
    private String result;
}
