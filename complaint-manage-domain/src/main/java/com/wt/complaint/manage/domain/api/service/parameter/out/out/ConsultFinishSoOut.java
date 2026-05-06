package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单结案出�?
 */
@Data
public class ConsultFinishSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结案结果
     */
    private String result;
}
