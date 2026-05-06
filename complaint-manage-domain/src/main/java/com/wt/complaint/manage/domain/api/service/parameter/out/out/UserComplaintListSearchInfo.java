package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintListSearchInfo implements Serializable {

    /**
     * 客诉类单�?
     */
    private String ucNo;

    /**
     * 客诉类单据类�?1-投诉�?2-举报�?
     */
    private Integer ucType;

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
     * 完整VIN�?
     */
    private String vin;

    /**
     * 举报单状�?1-待接�?2-待举报判�?3-已完�?
     */
    private Integer orderStatus;

    /**
     * 投诉单状态名�?
     */
    private String orderStatusName;

    /**
     * 门店Id
     */
    private String orgId;

    /**
     * 门店名称
     */
    private String orgName;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 联系人姓名密�?
     */
    private String contactNameC;

    /**
     * 联系人姓�?
     */
    private String contactName;

    /**
     * 联系人电话密�?
     */
    private String contactPhoneC;

    /**
     * 联系人电�?
     */
    private String contactPhone;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Byte testTag;

    /**
     * 处理人mid
     */
    private Long operatorMid;

    /**
     * 处理人姓�?
     */
    private String operatorName;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 完成时间
     */
    private String finishTime;

    /**
     * 创建人mid
     */
    private Long createMid;

    /**
     * 联系人性别 0 默认 1 �?2 �?
     */
    private Integer contactGender;

    /**
     * 催单次数
     */
    private Integer reminderTimes;

    /**
     * 举报场景：用,分隔
     */
    private String serviceScene;

}
