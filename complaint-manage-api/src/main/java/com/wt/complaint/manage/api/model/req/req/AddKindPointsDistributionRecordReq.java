package com.wt.complaint.manage.api.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddKindPointsDistributionRecordReq implements Serializable {
    /**
     * 客诉单号
     */
    private String complaintNo;
    /**
     * 分发记录id
     */
    private Long distributionId;
}
