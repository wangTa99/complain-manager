package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;
import java.util.Date;

@Data
public class UserComplaintExpandDO {
    private Long id;
    private String ucNo;
    private Integer reminderTimes;
    private Integer cityId;
    private Integer zoneId;
    private Integer littleZoneId;
    private String serviceScene;
    private Integer contactPhoneSuffix;
    private String contactPhoneMd5;
    private Integer contactGender;
    private String vinSuffix;
    private Integer judgeType;
    private String carNo;
    private Long customerServiceMid;
    private String expand;
    private Date createTime;
    private Date updateTime;
}
