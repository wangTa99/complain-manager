package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.api.model.req.operate.IssueTypeContent;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑咨询单入�?
 */
@Data
public class OrderEditConsultSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询单号
     */
    private String consultNo;

    /**
     * 扩展信息
     */
    private ConsultCreateExpandSoIn expandSoIn;

    /**
     * 创建�?mid
     */
    private Long createMid;

    /**
     * 跟进人mid
     */
    private Long operatorMid;

    /**
     * 跟进人岗位id
     */
    private Integer operatorPositionId;
}
