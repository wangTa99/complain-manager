package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 提交复盘入参（客诉三期）
 */
@Data
public class SubmitReviewSoIn {

    /**
     * 投诉单号
     */
    private String complaintNo;

    /**
     * 复盘材料-飞书云文档链�?
     */
    private String reviewMaterial;

    /**
     * 操作人MID
     */
    private Long operatorMid;

    /**
     * 操作人姓�?
     */
    private String operatorName;

    /**
     * 校验参数
     */
    public void checkSubmitReviewSoIn() {
        if (StringUtils.isBlank(this.getComplaintNo())) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "投诉单号不能为空");
        }
        if (StringUtils.isBlank(this.getReviewMaterial())) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "复盘材料不能为空");
        }
        if (!this.getReviewMaterial().matches("https://.*\\.feishu\\.cn/.*")) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "请使用小米飞书云文档");
        }
        if (Objects.isNull(this.getOperatorMid()) || this.getOperatorMid() == 0L) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "操作人不能为�?);
        }
    }
}
