package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 投诉单编辑详情出参，用于 getComplaintEditDetail
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintEditDetailSoOut implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 投诉场景，从 complaint_content �?fieldCode=complaint 解析
     */
    private FieldValueSoIn complaint;

    /**
     * 风险等级, 1/2/3/4 (code)
     */
    private String riskLevel;

    /**
     * 是否涉媒 0-�?1-�?
     */
    private String mediaInvolved;

    /**
     * 涉媒链接
     */
    private String mediaLink;
}
