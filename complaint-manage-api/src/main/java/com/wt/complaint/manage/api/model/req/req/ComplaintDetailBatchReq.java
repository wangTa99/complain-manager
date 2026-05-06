package com.wt.complaint.manage.api.model.req;

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
public class ComplaintDetailBatchReq implements Serializable {
    @ApiDocClassDefine(value = "complaintNoList", description = "客诉单号列表", required = true)
    private List<String> complaintNoList;
}
