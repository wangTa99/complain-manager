package com.wt.complaint.manage.api.model.req.apply;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.ClosingTag;
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
public class OrgFinishApplyReq implements Serializable {
    @ApiDocClassDefine(value = "客诉单号", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "申请门店id", description = "申请门店id")
    private String applyOrgId;

    @ApiDocClassDefine(value = "解决方案", description = "解决方案")
    private String solutionDesc;

    @ApiDocClassDefine(value = "结案标签链路", description = "结案标签链路，用/连接�?/32/333/4513")
    @Deprecated
    private List<ClosingTag> closingTagList;

    @ApiDocClassDefine(value = "附件", description = "附件列表")
    private List<Attachment> attachmentList;

    @ApiDocClassDefine(value = "是否与用户达成一�?, description = "是否与用户达成一�?0-�?1-�?)
    private Integer userAgreement;

    @ApiDocClassDefine(value = "车辆异常是否修复", description = "车辆异常是否修复 0-�?1-�?2-不涉�?)
    private Integer vehicleRepaired;

    @ApiDocClassDefine(value = "涉媒信息", description = "涉媒信息 1-用户已删�?2-用户未删�?3-不涉�?)
    private Integer mediaInfo;
}
