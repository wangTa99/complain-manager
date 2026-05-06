package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;
import java.util.Date;

@Data
public class UserComplaintOrderUpdateParam {
    private String ucNo;
    private Integer ucType;
    private String superTicketNo;
    private String soNo;
    private String vid;
    private Integer orderStatus;
    private String orgId;
    private String contactNameC;
    private String contactPhoneC;
    private Integer testTag;
    private Long operatorMid;
    private String complaintContent;
    private Date finishTime;
}