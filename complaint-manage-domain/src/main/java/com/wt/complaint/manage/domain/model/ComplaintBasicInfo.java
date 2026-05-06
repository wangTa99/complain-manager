package com.wt.complaint.manage.domain.model;

import lombok.Data;


/**
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Data
public class ComplaintBasicInfo {

    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 超级工单�?
     */
    private String stNo;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

    /**
     * 跟进人岗位类�?
     *
     */
    private Integer operatorPositionId;

    /**
     * 跟进人mid
     */
    private Long operatorMid;

    /**
     * 跟进人姓�?
     */
    private String operatorName;

    /**
     * 大区id
     * 对应省分公司标识
     */
    private Integer zoneId;

    /**
     * 小区id
     */
    private Integer littleZoneId;

    /**
     * 门店Id
     * 交付中心也是此字�?
     */
    private String orgId;
}
