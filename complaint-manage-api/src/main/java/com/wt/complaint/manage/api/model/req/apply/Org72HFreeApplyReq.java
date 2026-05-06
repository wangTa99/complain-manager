package com.wt.complaint.manage.api.model.req.apply;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
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
public class Org72HFreeApplyReq implements Serializable {
    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "applyOrgId", description = "申请门店id")
    private String applyOrgId;

    @ApiDocClassDefine(value = "deliveryTime", description = "车辆交付日期")
    private String deliveryTime;

    @ApiDocClassDefine(value = "mileage", description = "里程�?)
    private Double mileage;

    @ApiDocClassDefine(value = "申请原因", description = "申请72H无法结案原因描述")
    private String applyReason;

    @ApiDocClassDefine(value = "附件", description = "申请72H无法结案附件")
    private List<Attachment> attachmentList;
}
