package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 咨询单创建扩展信息入�?
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultCreateExpandSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级：一�?-4，高 -8，紧�?-16
     */
    private Integer priority;

    /**
     * 咨询类型
     */
    private Integer enquireType;


    /**
     * 问题类目
     */
    private String problemCategory;

    /**
     * 问题描述
     */
    private String remark;

    /**
     * 期望联系时间�?0 位毫秒时间戳
     */
    private Long expectedTouchTime;

    /**
     * 附件信息
     */
    private List<AttachmentGoIn> attachments;

    /**
     * 门店 id
     */
    private String orgId;

    /**
     * 维保超级工单�?
     */
    private String mrSuperTicketNo;
}
