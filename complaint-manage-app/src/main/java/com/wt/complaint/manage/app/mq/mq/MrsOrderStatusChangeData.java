package com.wt.complaint.manage.app.mq;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MrsOrderStatusChangeData implements Serializable {
    private String mrNo;

    private Integer mrStatus;

    private Integer mrType;

    private Map<String, Object> ext;
}
