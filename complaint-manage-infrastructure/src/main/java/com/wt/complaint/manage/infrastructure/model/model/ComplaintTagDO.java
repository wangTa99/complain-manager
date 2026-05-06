package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.util.Date;

/**
 * 客诉标签�?
 *
 * @TableName complaint_tag
 */
@Data
public class ComplaintTagDO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 标签类型 1 投诉率免考核（COMPLAINT_RATE_ASSESSMENT_FREE�?2 72H无法结案(FINISH_72H_ASSESSMENT_FREE) 3 首响超时(FIRST_RESPONSE_TIMEOUT) 4 结案超时(FINISH_TIMEOUT)
     */
    private String tagType;

    /**
     * 是否删除, 0-未删, 1-已删
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;
}