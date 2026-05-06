package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUpdateCustomerServiceInfo {
    private String stNo;
    private Long customerServiceMid;
}
