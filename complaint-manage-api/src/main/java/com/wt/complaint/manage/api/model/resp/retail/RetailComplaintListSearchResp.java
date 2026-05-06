package com.wt.complaint.manage.api.model.resp.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 零售投诉列表搜索响应
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetailComplaintListSearchResp implements Serializable {

    private static final long serialVersionUID = -584857462591543409L;

    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Integer total;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<RetailComplaintListSearchDTO> dataList;
}
