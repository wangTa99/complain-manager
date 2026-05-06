package com.wt.complaint.manage.domain.api.service.parameter.out.approve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintAuditDetailSoOut implements Serializable {

    private static final long serialVersionUID = 1324593169973848172L;

    /**
     * 审批ID
     */
    private Long id;

    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 车辆vid
     */
    private String vid;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 门店名称
     */
    private String orgName;

    /**
     * 大区id
     */
    private String zoneId;

    /**
     * 小区id
     */
    private String littleZoneId;

    /**
     * 申请内容, json格式,包括发起请求的参�?
     */
    private String applyContent;

    /**
     * 审批单类�?1 申请改派门店 2 申请72H无法结案 3 申请免责 4 申请结案
     */
    private Integer auditType;

    /**
     * 审核状�?0 默认 1 审核�?2 通过 3 驳回
     */
    private Integer auditStatus;

    /**
     * 审批人mid
     */
    private Long auditMid;

    /**
     * 申请人mid
     */
    private Long createMid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 审批意见，也等价于驳回原因，纯字符串
     */
    private String auditComment;

}
