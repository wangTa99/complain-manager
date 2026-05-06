package com.wt.complaint.manage.api.model.resp;

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
public class ConsultHandlerListResp implements Serializable {
    @ApiDocClassDefine(value = "可派单人员列�?)
    private List<ComplaintHandlerInfo> handlerList;
}
