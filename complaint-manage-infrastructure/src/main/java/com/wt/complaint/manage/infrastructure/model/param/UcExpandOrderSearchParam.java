package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/23 17:26
 */
@Data
public class UcExpandOrderSearchParam {
    private String ucNo;

    private List<String> ucNoList;
}
