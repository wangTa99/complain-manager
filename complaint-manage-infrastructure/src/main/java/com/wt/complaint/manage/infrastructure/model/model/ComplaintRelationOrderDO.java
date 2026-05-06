package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客诉单关联单据表
 * @TableName complaint_relation_order
 */
@Data
public class ComplaintRelationOrderDO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 单据类型 1 维保�?
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