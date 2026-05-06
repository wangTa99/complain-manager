package com.wt.complaint.manage.domain.api.service.parameter.in.retail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 催单提醒
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailRemindOrderSoIn implements Serializable {

    /**
     * 催单来源, 1-客服, 2-交付 ,3-零售
     */
    private Integer source;

    /**
     * 客诉单号
     */
    private String drNo;

    /**
     * 催单人mid
     */
    private String reminderMid;

    /**
     * 催单人姓�?
     */
    private String reminderName;

    /**
     * 客服催单时填写的催单信息
     */
    private String orderRemindInfo;
}
