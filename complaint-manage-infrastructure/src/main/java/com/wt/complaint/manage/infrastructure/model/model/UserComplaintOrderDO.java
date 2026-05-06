package com.wt.complaint.manage.infrastructure.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserComplaintOrderDO implements Serializable {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 客诉类单据类�?1-投诉�?2-举报�?
     */
    private Integer ucType;

    /**
     * 业务幂等key
     */
    private String idempotentKey;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 举报单状�?1-待接�?2-待举报判�?3-已完�?
     */
    private Integer orderStatus;

    /**
     * 门店Id
     */
    private String orgId;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Byte testTag;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人mid
     */
    private Long createMid;

    /**
     * 客诉内容
     */
    private String complaintContent;

}