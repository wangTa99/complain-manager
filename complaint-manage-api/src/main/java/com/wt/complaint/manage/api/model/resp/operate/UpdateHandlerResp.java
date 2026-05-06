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
public class UpdateHandlerResp implements Serializable {
    @ApiDocClassDefine(value = "更新处理人结�?)
    private String result;
}
