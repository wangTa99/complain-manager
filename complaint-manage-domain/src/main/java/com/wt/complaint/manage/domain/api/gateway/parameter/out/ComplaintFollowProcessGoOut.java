package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.Date;

@Data
@AllArgsConstructor
@Nonnull
@Builder
public class ComplaintFollowProcessGoOut {
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
     * bpm审批流程ID
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
