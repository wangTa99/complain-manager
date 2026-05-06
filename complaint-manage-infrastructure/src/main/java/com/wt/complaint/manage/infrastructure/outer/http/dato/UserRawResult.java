package com.wt.complaint.manage.infrastructure.outer.http.dato;

import lombok.Data;

/**
 * @author huwei
 */
@Data
public class UserRawResult {
    
    private long userId;
    
    private String userName;
    
    private String trueName;
    
    private String ucenterId;
    
    private String email;
    
    private String mobile;
    
    private String icon;
    
    private String city;
    
    private String sex;
}
