package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户投诉列表搜索返回
 * @author linjiehong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserComplaintListSearchResp implements Serializable {
    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Integer total;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<UserComplaintListSearchDTO> dataList;
}
