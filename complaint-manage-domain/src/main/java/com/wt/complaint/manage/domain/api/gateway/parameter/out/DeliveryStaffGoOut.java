package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

/**
 * 交付专员信息返回结果
 */
@Data
public class DeliveryStaffGoOut {
    /**
     * 交付单id
     */
    private Long deliveryId;
    /**
     * 交付专员岗位[1:A�?2:B岗]
     */
    private Integer positionId;
    /**
     * 交付专员miId
     */
    private Long miId;
    /**
     * 交付专员姓名
     */
    private String userName;
    /**
     * 交付专员邮箱
     */
    private String email;
    /**
     * 交付专员手机�?
     */
    private String mobile;
    /**
     * 交付专员头像
     */
    private String avatar;
}
