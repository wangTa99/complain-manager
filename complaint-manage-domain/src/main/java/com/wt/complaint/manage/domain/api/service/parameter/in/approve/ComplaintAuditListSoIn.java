package com.wt.complaint.manage.domain.api.service.parameter.in.approve;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintAuditListSoIn implements Serializable {

    private static final long serialVersionUID = 512127934016218652L;

    /**
     * 审批状态列�?0 默认 1 待审�?2 通过 3 驳回
     */
    private List<Integer> auditStatusList;

    /**
     * 审批单类�?1-改派门店 2-72H无法结案 3-申请免责 4-申请结案 5-产品风险申请结案 6-服务投诉判责
     */
    private List<Integer> auditTypeList;

    /**
     * 投诉单号
     */
    private String complaintNo;

    /**
     * 门店id列表,支持多�?
     */
    private List<String> orgIdList;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系电话MD5
     */
    private String contactPhoneMd5;

    /**
     * 车牌�?
     */
    private String carNo;

    /**
     * VIN�?
     */
    private String vin;

    private String vid;

    /**
     * 创建时间起始,格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTimeStart;

    /**
     * 创建时间结束,格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTimeEnd;

    /**
     * 当前登录人mid
     */
    private Long mid;

    /**
     * 审核人mid
     */
    private Long auditMid;

    /**
     * 大区id列表
     */
    private List<Integer> zoneIdList;

    /**
     * 小区域id
     */
    private List<Integer> littleZoneIdList;

    /**
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /**
     * 页码, 默认�?
     */
    private Integer pageNum;

    /**
     * 每一页的大小, 默认10
     */
    private Integer pageSize;
}
