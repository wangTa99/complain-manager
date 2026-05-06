package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单接单入�?
 */
@Data
public class ConsultOrderPickUpSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 接单人员 mid
     */
    private String pickUpMid;

    /**
     * 登录角色
     */
    private String loginRole;
}
