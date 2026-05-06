package com.wt.complaint.manage.api.model.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class ComplaintListExportRes implements Serializable {
    private static final long serialVersionUID = 8130210196269937621L;
    /**
     * 任务id
     */
    private String taskId;
}
