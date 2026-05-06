package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 投诉订单升级请求�?
 */
@Data
@Slf4j
public class ComplaintOrderUpgradeReq implements Serializable {

    private static final long serialVersionUID = 5612064857927100323L;

    @ApiDocClassDefine(value = "complaintNo", description = "投诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "targetType", description = "目标投诉类型: 1-产品投诉, 2-服务投诉")
    private Integer targetType;

    @ApiDocClassDefine(value = "upgradeReason", description = "升级原因")
    private String upgradeReason;

    @ApiDocClassDefine(value = "operateSource", description = "操作来源：PAD_DETAIL:Pad零售�? CUSTOMER_SERVICE_WORKBENCH:客服工作�?)
    private String operateSource;

}
