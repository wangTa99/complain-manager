package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserComplaintOrderDetailSoOut implements Serializable {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 客诉类单据类�?1-投诉�?2-举报�?
     */
    private Integer ucType;

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
     * 车牌�?
     */
    private String carNo;

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

    /**
     * 大区id
     */
    private String zoneId;

    /**
     * 小区id
     */
    private String littleZoneId;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 举报场景：用,分隔
     */
    private String serviceScene;

    /**
     * 跟进客服mid
     */
    private Long customerServiceMid;

}