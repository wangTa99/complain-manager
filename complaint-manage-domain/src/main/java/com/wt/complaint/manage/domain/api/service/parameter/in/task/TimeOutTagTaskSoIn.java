package com.wt.complaint.manage.domain.api.service.parameter.in.task;

import lombok.Data;

import java.util.List;

@Data
public class TimeOutTagTaskSoIn {
    private List<String> complaintNoList;
    private String orgId;
}
