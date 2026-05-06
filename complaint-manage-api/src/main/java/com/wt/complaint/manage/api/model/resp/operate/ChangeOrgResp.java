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
public class ChangeOrgResp implements Serializable {

    private static final long serialVersionUID = -4761847288153840277L;

    @ApiDocClassDefine(value = "result", description = "结果")
    private String result;
}