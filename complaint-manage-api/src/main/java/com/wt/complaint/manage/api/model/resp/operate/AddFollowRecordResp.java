package com.wt.complaint.manage.api.model.resp.operate;

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
public class AddFollowRecordResp implements Serializable {

    private static final long serialVersionUID = -1799786915801935663L;

    @ApiDocClassDefine(value = "result", description = "添加跟进记录结果")
    private String result;
}
