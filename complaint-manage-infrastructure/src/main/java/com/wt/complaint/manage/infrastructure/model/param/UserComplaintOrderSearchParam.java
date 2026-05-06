package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/22 20:34
 */
@Data
public class UserComplaintOrderSearchParam {
    private String ucNo;

    private List<String> ucNoList;

    private List<String> stNoList;

    private String idempotentKey;

    private Integer ucType;

    private boolean master;
}
