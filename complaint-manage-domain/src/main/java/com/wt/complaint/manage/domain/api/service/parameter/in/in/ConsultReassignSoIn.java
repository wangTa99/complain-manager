package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 咨询单改派入�?
 */
@Data
public class ConsultReassignSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 改派门店 code
     */
    private String orgId;

    /**
     * 改派岗位
     */
    private Integer reassignOperatorPositionId;

    /**
     * 改派人员 mid
     */
    private Long reassignOperatorMid;

    /**
     * 改派描述
     */
    private String reassignDesc;

    /**
     * 附件
     */
    private List<AttachmentGoIn> attachmentList;

    /**
     * 操作�?mid
     */
    private Long operateMid;
}
