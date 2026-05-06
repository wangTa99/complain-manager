package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/26 19:30
 */
@Data
public class UcOrderBatchInfoSoIn {
    /**
     * 客诉类单�?
     */
    private List<String> ucNoList;
}
