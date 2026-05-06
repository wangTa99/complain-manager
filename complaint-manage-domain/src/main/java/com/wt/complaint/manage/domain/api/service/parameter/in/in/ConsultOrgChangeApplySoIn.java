package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 咨询单门店改派申请入�?
 */
@Data
public class ConsultOrgChangeApplySoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 申请门店 id
     */
    private String applyOrgId;

    /**
     * 申请要改派到的门�?id
     */
    private String desOrgId;

    /**
     * 改派说明
     */
    private String reassignRemark;

    /**
     * 操作�?mid
     */
    private Long operateMid;
}
