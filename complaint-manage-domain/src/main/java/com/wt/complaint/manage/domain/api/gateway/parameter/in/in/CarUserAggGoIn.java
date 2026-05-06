package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarUserAggGoIn {
    private String vid;

    public void checkCarUserAggGoIn() {
        if (vid == null || vid.isEmpty()) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "vid不能为空");
        }
    }
}
