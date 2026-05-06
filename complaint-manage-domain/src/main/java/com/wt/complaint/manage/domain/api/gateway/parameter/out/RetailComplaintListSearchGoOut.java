package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintListSearchInfo;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class RetailComplaintListSearchGoOut implements Serializable {

    @ApiDocClassDefine(value = "total", description = "总条�?)
    private Long total;

    @ApiDocClassDefine(value = "dataList", description = "数据列表")
    private List<RetailComplaintListSearchInfo> dataList;
}
