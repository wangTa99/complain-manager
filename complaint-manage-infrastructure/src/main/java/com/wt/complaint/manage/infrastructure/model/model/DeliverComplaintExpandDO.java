package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.util.Date;

/**
 * 交付零售客诉单扩展表实体�?
 */
@Data
public class DeliverComplaintExpandDO {
    /**
     * 自增id
     */
    private Long id;
    
    /**
     * 客诉单号
     */
    private String drNo;
    
    /**
     * 群id
     */
    private String chatId;
    
    /**
     * 群名�?
     */
    private String chatName;
    
    /**
     * 建群失败原因
     */
    private String createChatFailReason;

    /**
     * 线索id
     */
    private Long clueId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
