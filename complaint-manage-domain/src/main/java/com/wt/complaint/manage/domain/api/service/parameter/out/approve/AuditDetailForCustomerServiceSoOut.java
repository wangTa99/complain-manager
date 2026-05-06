package com.wt.complaint.manage.domain.api.service.parameter.out.approve;

import com.wt.complaint.manage.api.model.resp.ClosingTagDTO;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDetailForCustomerServiceSoOut implements Serializable {

    private static final long serialVersionUID = 8438724519342177664L;

    @ApiDocClassDefine(value = "id", description = "审批流id")
    private Long id;

    @ApiDocClassDefine(value = "createTime", description = "申请时间,即审批流的创建时�?格式为yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiDocClassDefine(value = "applicantName", description = "申请人姓�?)
    private String applicantName;

    @ApiDocClassDefine(value = "applicantMid", description = "申请人mid")
    private Long applicantMid;

    @ApiDocClassDefine(value = "solution", description = "解决方案")
    private String solution;

    @ApiDocClassDefine(value = "附件", description = "附件列表")
    private List<AttachmentSoIn> attachmentList;

    @ApiDocClassDefine(value = "closingTagList", description = "结案标签列表")
    private List<ClosingTagDTO> closingTagList;
}
