package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.resp.UserComplaintListSearchDTO;
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
public class UserComplaintListSearchSoOut implements Serializable {

    private static final long serialVersionUID = 3390901888605448879L;

    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Long total;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<UserComplaintListSearchInfo> dataList;
}
