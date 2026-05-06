package com.wt.complaint.manage.api.model.req;

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
public class FollowRecordReq implements Serializable {
    @Deprecated
    @ApiDocClassDefine(value = "complaintNo", required = true, description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "ucNo", required = true, description = "客诉类单�?)
    private String ucNo;

    @ApiDocClassDefine(value = "consultNo", description = "咨询单号")
    private String consultNo;

    @ApiDocClassDefine(value = "followInfo", required = true, description = "跟进详情")
    private String followInfo;

    @ApiDocClassDefine(value = "附件信息", description = "附件信息")
    private List<Attachment> attachmentList;
}
