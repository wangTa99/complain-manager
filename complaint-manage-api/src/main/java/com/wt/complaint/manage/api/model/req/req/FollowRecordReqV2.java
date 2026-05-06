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
public class FollowRecordReqV2 implements Serializable {

    private static final long serialVersionUID = -1891182362449279838L;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "followInfo", required = true, description = "跟进详情")
    private String followInfo;

    @ApiDocClassDefine(value = "附件信息", description = "附件信息")
    private List<Attachment> attachmentList;

    @ApiDocClassDefine(value = "mileage", required = false, description = "车辆行驶里程，手填需大于0且最多保�?位小�?)
    private String mileage;
}
