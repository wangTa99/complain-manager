package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.List;

@Data
public class RelationOrderListParam {
    private List<String> complaintNoList;

    private int ucType;

    private List<String> bizNoList;

    private boolean master;
}
