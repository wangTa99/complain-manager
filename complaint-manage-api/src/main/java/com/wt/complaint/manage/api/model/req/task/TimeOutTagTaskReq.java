package com.wt.complaint.manage.api.model.req.task;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TimeOutTagTaskReq implements Serializable {
    /**
     * 客诉单号列表
     */
    private List<String> complaintNoList;
}
