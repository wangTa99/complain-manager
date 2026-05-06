package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.enums.DeliverComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.ReminderTimesOptionEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibleEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.enums.TimeoutOptionEnum;
import com.wt.complaint.manage.api.model.req.common.CommonDataReq;
import com.wt.complaint.manage.api.model.resp.common.AllEnumListResp;
import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import com.wt.complaint.manage.api.provider.CommonDataProvider;
import com.wt.complaint.manage.api.provider.ComplaintViewProvider;
import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author linjiehong
 * @date 2025/5/19 13:44
 */
@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "客诉类单据通用信息接口", apiInterface = ComplaintViewProvider.class)
public class CommonDataProviderImpl implements CommonDataProvider {


    @Override
    @ApiDoc(value = "getStatusList", name = "获取枚举列表", description = "售后工作台调用：/mtop/proretailcar/complaint/common/getStatusList")
    public Result<AllEnumListResp> getStatusList(CommonDataReq req) {
        AllEnumListResp allEnumListResp = new AllEnumListResp();

        // 获取可见举报单状态枚�?
        allEnumListResp.setStatusEnumList(
                getEnumMap(ReportOrderStatusEnum.getViewableStatus())
        );

        return Result.success(allEnumListResp);
    }


    private <E extends Enum<?>> List<Map<String, Object>> getEnumMap(List<E> list) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        LinkedList<Map<String, Object>> resultList = new LinkedList<>();
        for (E e : list) {
            HashMap<String, Object> enumTmpMap = new HashMap<>();
            BeanUtil.copyProperties(e, enumTmpMap);
            resultList.add(enumTmpMap);
        }
        return resultList;
    }

    @Override
    @ApiDoc(value = "getOptionList", name = "获取下拉选列�?, description = "交付工作台：/mtop/delivery/complaint/common/getOptionList")
    public Result<Map<String, List<CommonOptionResp>>> getOptionList() {
        Map<String, List<CommonOptionResp>> optionMap = new HashMap<>();
        optionMap.put("orderStatus", DeliverComplaintOrderStatusEnum.getCommonOptionList());
        optionMap.put("responsible", ResponsibleEnum.getCommonOptionList());
        optionMap.put("operatorPositionId", DeliverPositionEnum.getCommonOptionList());
        optionMap.put("reminderTimes", ReminderTimesOptionEnum.getCommonOptionList());
        optionMap.put("firstResponseTag", TimeoutOptionEnum.getCommonOptionList());
        optionMap.put("finishTag", TimeoutOptionEnum.getCommonOptionList());
        optionMap.put("riskLevel", RiskLevelEnum.getCommonOptionList());
        return Result.success(optionMap);
    }

}
