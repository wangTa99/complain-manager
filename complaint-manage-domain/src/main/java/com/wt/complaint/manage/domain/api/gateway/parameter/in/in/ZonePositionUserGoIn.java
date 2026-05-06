package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZonePositionUserGoIn {

    private List<Integer> bigZoneIdList;
    private List<Integer> littleZoneIdList;
    private List<Integer> cityZoneIdList;
    private Integer positionId;
}
