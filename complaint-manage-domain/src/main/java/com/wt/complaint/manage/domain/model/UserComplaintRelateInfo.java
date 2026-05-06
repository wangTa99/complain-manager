package com.wt.complaint.manage.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * @author linjiehong
 * @date 2025/5/23 10:54
 */
@Data
public class UserComplaintRelateInfo {
    /**
     * 客诉单号/投诉单号
     */
    private String ucNo;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 单据类型 1 维保�?2 工单
     */
    private Integer bizType;

    /**
     * 业务单扩展信�?
     */
    private String bizExtendInfo;

    /**
     * 创建时间
     */
    private Date createTime;
}
