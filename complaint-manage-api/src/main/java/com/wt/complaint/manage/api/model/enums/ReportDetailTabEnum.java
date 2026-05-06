package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 举报单详情页tab枚举�?
 */
@AllArgsConstructor
@Getter
public enum ReportDetailTabEnum {
    FOLLOW_UP_RECORDS("followUpRecords", "跟进记录", ReportOrderStatusEnum.PENDING_ORDER.getCode()),
    COMPLAINT_INFO("userComplaintInfo", "举报信息", ReportOrderStatusEnum.PENDING_ORDER.getCode()),
    ONLINE_SERVICE_RECORDS("onlineServiceRecords", "线上服务记录", ReportOrderStatusEnum.PENDING_ORDER.getCode());

    /**
     * tab类型
     */
    public final String type;

    /**
     * tab描述
     */
    public final String desc;

    /**
     * 最小cp状�?
     */
    public final Integer minCpStatus;

    /**
     * 根据cp状态获取tab列表
     * @param cpStatus cp状�?
     * @return tab列表
     */
    public static List<ReportDetailTabEnum> listTab(Integer cpStatus) {
        List<ReportDetailTabEnum> list = new ArrayList<>();
        for (ReportDetailTabEnum tabEnum : ReportDetailTabEnum.values()) {
            if (cpStatus >= tabEnum.getMinCpStatus()) {
                list.add(tabEnum);
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }
}
