package com.wt.complaint.manage.api.model.resp;

import com.wt.complaint.manage.api.model.req.operate.FieldValue;
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
public class ComplaintEditDetailResp implements Serializable {

    private static final long serialVersionUID = -4951085684846240808L;

    @ApiDocClassDefine(value = "complaint", description = "投诉场景,模板字段")
    private FieldValue complaint;

    @ApiDocClassDefine(value = "riskLevel", description = "风险等级, 1, 2, 3, 4 (code)")
    private String riskLevel;

    @ApiDocClassDefine(value = "mediaInvolved", description = "是否涉媒 0-�?1-�?)
    private String mediaInvolved;

    @ApiDocClassDefine(value = "mediaLink", description = "涉媒链接")
    private String mediaLink;

}
