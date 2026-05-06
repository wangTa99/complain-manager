package com.wt.complaint.manage.infrastructure.model.param;

import lombok.Data;

/**
 * 交付客诉单批量更新参数类
 * 
 * @author system
 * @date 2025/6/23
 */
@Data
public class DeliverComplaintUpdateParam {

    /**
     * 客诉单号
     */
    private String drNo;
    
    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

}
