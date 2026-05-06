package com.wt.complaint.manage.domain.api.service.parameter.in.opetate;

import com.wt.complaint.manage.api.model.req.operate.ComplaintOrderCreateExpandDTO;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateExpandSoIn;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

/**
 * @author linjiehong
 * @date 2025/5/21 16:22
 */
@Data
public class CreateOrderSoIn {
    /** 车vid */
    private String vid;

    /** 作业类型 */
    private Integer workType;

    /** 服务单号 */
    private String soNo;

    /** 超级工单�?*/
    private String superTicketNo;

    /** 幂等ID */
    private String idempotentId;

    /** 门店id */
    private String orgId;

    /** 联系人密�?*/
    private String contactName;

    /** 联系人手机密�?*/
    private String contactTel;

    /** 联系人尊�?*/
    private Integer contactTitle;

    /** 测试标识, 0-非测试环�? 1-是测试环�?*/
    private Integer testTag;

    /** 创建人mid */
    private Long createMid;

    /**
     * 扩展信息
     */
    private ComplaintOrderCreateExpandSoIn expandSoIn;
}
