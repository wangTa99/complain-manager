package com.wt.complaint.manage.api.model.resp.apply;

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
public class OrgApplyResp implements Serializable {
    @ApiDocClassDefine(value = "applyId", description = "申请流程ID")
    private String applyId;
}
