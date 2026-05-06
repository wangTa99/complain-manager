package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindByIdempotentIdGoIn {

    /**
     * 幂等key
     */
    private String idempotentKey;

    private Boolean useMaster;

}
