package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

import java.util.Date;

@Data
public class UserConsultOrderUpdateParam {
    private String consultNo;
    private Integer consultType;
    private String superTicketNo;
    private String soNo;
    private String vid;
    private String carNo;
    private String carType;
    private String problemDesc;
    private Integer orderStatus;
    private Integer reminderTimes;
    private Integer priority;
    private String orgId;
    private String contactNameC;
    private String contactPhoneC;
    private Integer testTag;
    private Long operatorMid;
    private Date expectingBackTime;
    private Date finishTime;
    private Integer handleResult;
    private String finishDesc;
}
