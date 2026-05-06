package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import lombok.Builder;
import lombok.Data;

/**
 * 交付客诉单批量更新参数类
 * 
 * @author system
 * @date 2025/6/23
 */
@Data
@Builder
public class DeliverComplaintUpdateGoIn {

    /**
     * 客诉单号
     */
    private String drNo;
    
    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

}
