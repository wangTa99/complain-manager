package com.wt.complaint.manage.api.model.resp.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class EditComplaintResp implements Serializable {

    private static final long serialVersionUID = -4761847288153840277L;

    @ApiDocClassDefine(value = "result", description = "编辑结果")
    private String result;
}
