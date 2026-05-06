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
public class ExemptionApplyReq implements Serializable {
    @ApiDocClassDefine(value = "客诉单号", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "申请门店id", description = "申请门店id")
    private String applyOrgId;

    @ApiDocClassDefine(value = "申请原因", description = "申请免责原因描述")
    private String applyReason;

    @ApiDocClassDefine(value = "附件", description = "申请免责附件")
    private List<Attachment> attachmentList;
}
