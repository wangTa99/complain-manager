package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/23 16:59
 */
@Data
public class UserComplaintExpandUpdateParam {
    private List<String> ucNoList;

    private String ucNo;

    private Long customerServiceMid;

    private Integer reminderTimes;

    private Integer judgeType;
}