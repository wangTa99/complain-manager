package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.car.soc.api.dto.GroupValueDto;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;

import java.util.List;

@Data
public class OrderEditComplaintSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 投诉场景,模板字段
     */
    private FieldValueSoIn complaint;

    /**
     * 风险等级, 1, 2, 3, 4 (code)
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

    /**
     * 操作人mid
     */
    private Long operateMid;

    /**
     * 操作人name
     */
    private String operateName;

    /**
     * 投诉字段详情
     */
    private List<GroupValueDto> complaintContent;

    public void checkEditComplaint() {
        if (complaintNo == null || complaintNo.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不可为空");
        }
    }

}
