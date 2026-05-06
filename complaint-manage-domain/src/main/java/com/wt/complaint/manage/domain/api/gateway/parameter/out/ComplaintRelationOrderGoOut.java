package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintRelationOrderGoOut {
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
