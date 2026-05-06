package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 创建交付/零售客诉单请求参�?
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRetailComplaintOrderSoIn implements Serializable {

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
     * 幂等ID
     */
    private String idempotentId;

    /**
     * 订单�?
     */
    private String tradeOrderId;

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
     * 测试标识, 0-非测试环�? 1-是测试环�?
     */
    private Integer testTag;

    /***
     * 创建人mid
     */
    private Long createMid;

    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 扩展信息
     */
    private RetailComplaintOrderCreateExpandSoIn expandSoIn;
}
