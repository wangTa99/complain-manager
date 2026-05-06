package com.wt.complaint.manage.domain.api.service.parameter.in.approve;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客诉单与结案标签关联�?
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintRelationClosingTagSoIn {

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 结案标签id链路,�?连接,例如 1/2/3
     */
    private String closingTagIdLink;

    /**
     * 结案标签名称链路,�?连接,例如 汽车/一般投�?
     */
    private String closingTagNameLink;

    /**
     * 是否删除, 0-未删, 1-已删
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}