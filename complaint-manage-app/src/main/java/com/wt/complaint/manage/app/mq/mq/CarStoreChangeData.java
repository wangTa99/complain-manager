package com.wt.complaint.manage.app.mq;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CarStoreChangeData implements Serializable {
    /**
     *  门店id
     */
    private String orgId;
    /**
     * 涉及改动的域 orgBase,orgAddress,orgBuild
     *     orgCategory,orgContact,orgCost
     *     orgExtension,orgFinance,orgImage
     *     orgInvoice,orgService,orgTime
     */
    private List<String> changeModule;
    /**
     * 操作类型 1新增 2修改
     */
    private Integer opType;
    private String areaId;
    private String businessType;
}
