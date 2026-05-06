package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreEmployeeListGoIn implements Serializable {

    private static final long serialVersionUID = 8868652887926564900L;

    private String orgId;

    /**
     * 组织中台职位id枚举
     */
    private List<Integer> positionIdList;
}
