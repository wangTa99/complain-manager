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
public class ComplaintFollowUpRecordsResp implements Serializable {
    @ApiDocClassDefine(value = "records", description = "跟进记录列表")
    private List<ComplaintFollowUpRecordInfo> records;
}