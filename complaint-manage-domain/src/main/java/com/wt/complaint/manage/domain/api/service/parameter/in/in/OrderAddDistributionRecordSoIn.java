package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAddDistributionRecordSoIn {
    /**
     * 客诉单号
     */
    private String complaintNo;
    /**
     * 分发id
     */
    private Long distributionId;

    public void checkDistributionRecord() {
        if (StringUtils.isEmpty(this.complaintNo)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "客诉单号不可为空");
        }
        if (Objects.isNull(this.distributionId)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "积分发放id不可为空");
        }
    }
}
