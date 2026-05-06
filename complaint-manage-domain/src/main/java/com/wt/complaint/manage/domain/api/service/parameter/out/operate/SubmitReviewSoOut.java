package com.wt.complaint.manage.domain.api.service.parameter.out.operate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交复盘出参（客诉三期）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewSoOut {

    /**
     * 是否成功
     */
    private Boolean success;
}
