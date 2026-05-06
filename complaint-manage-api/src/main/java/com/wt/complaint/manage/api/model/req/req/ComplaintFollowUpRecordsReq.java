package com.wt.complaint.manage.api.model.req;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintFollowUpRecordsReq implements Serializable {
    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "ucNo", description = "客诉类单据号")
    private String ucNo;

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    private String consultNo;

    @ApiDocClassDefine(value = "consultSuperTicketNo", description = "咨询单超级工单号")
    private String consultSuperTicketNo;

    @ApiDocClassDefine(value = "source", description = "来源：零售通PAD_DETAIL，售后工作台AFTER_SALE_WORKBENCH")
    private String source;


}
