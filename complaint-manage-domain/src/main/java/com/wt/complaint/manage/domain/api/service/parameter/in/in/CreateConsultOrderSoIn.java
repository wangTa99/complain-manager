package com.wt.complaint.manage.domain.api.service.parameter.in;

import com.wt.complaint.manage.api.model.req.operate.CsEnquireInfo;
import com.wt.complaint.manage.api.model.req.operate.IssueTypeContent;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建咨询单入�?
 */
@Data
public class CreateConsultOrderSoIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * �?vid
     */
    private String vid;

    /**
     * vin�?�?
     */
    private String vinSufix;

    /**
     * 作业类型
     */
    private Integer workType;

    /**
     * 服务单号
     */
    private String soNo;

    /**
     * 超级工单�?
     */
    private String superTicketNo;

    /**
     * 幂等 ID
     */
    private String idempotentId;

    /**
     * 门店 id
     */
    private String orgId;

    /**
     * 联系人密�?
     */
    private String contactName;

    /**
     * 联系人手机密�?
     */
    private String contactTel;

    /**
     * 联系人尊�?
     */
    private Integer contactTitle;

    /**
     * 测试标识�?-非测试环境，1-是测试环�?
     */
    private Integer testTag;

    /**
     * 创建�?mid
     */
    private Long createMid;

    /**
     * 跟进人mid
     */
    private Long operatorMid;

    /**
     * 跟进人岗位id
     */
    private Integer operatorPositionId;

    /**
     * 扩展信息
     */
    private ConsultCreateExpandSoIn expandSoIn;
}
