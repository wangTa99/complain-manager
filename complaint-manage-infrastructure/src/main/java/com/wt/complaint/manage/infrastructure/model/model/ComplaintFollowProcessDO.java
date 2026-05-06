package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客诉单跟进记�?
 *
 * @TableName complaint_follow_process
 */
@Data
public class ComplaintFollowProcessDO implements Serializable {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 跟进记录类型 1 跟进记录 2 申请信息 3 审批信息 4 维保单信�?5 积分信息 ...
     */
    private String processType;

    /**
     * BPM 流程实例id
     */
    private String processInstanceId;

    /**
     * 记录内容
     */
    private String processContent;

    /**
     * 创建时间
     */
    private Date createTime;
}