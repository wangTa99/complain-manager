package com.wt.complaint.manage.domain.api.service.parameter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintBatchDetailSoIn {
    private List<String> complaintNoList;
}
