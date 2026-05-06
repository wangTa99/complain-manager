package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.util.Date;

/**
 * 客诉单与结案标签关联�?
 *
 * @TableName complaint_relation_closing_tag
 */
@Data
public class ComplaintRelationClosingTagDO {
    /**
     * 自增ID
     */
    private Long id;

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