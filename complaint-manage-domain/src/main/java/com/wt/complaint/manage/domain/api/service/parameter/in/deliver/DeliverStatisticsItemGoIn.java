package com.wt.complaint.manage.domain.api.service.parameter.in.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统计入参
 * @author huxiankang
 * @date 2025-06-24 14:15:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverStatisticsItemGoIn extends DeliverComplaintDataPermissionGoIn {

    @ApiDocClassDefine(value = "orgIds", description = "门店ids")
    private List<String> orgIds;

    @ApiDocClassDefine(value = "testTag", description = "测试数据", required = true)
    private Integer testTag;

}
