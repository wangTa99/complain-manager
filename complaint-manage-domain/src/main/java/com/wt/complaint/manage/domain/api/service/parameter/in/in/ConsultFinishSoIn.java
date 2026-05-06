package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 咨询单结案入�?
 */
@Data
public class ConsultFinishSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 结案描述
     */
    private String finishDesc;

    /**
     * 处理类型 1 已处�?2 无需门店处理
     */
    private Integer handleType;

    /**
     * 结案附件
     */
    private List<AttachmentGoIn> finishAttachmentList;

    /**
     * 操作�?mid
     */
    private Long operateMid;

    /**
     * 申请门店id
     */
    private String applyOrgId;
}
