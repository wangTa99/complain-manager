package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.Data;

import java.util.Objects;

@Data
public class GetComplaintHandlerSoIn {
    /**
     * 门店id
     */
    private String orgId;

    public void checkParam() {
        if (Objects.isNull(this.orgId)) {
            throw new BusinessException(GeneralCodes.ParamError, "门店id不能为空�?);
        }
    }
}
