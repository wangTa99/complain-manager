package com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintApplySoOut {
    private String processInstanceId;
}