package com.wt.complaint.manage.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ConsultDetailTabEnum {
    CONSULT_INFO("consultInfo", "咨询单信�?, ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(OnlyViewEnum.NO.getCode(), OnlyViewEnum.YES.getCode())),
    FOLLOW_UP_RECORDS("followUpRecords", "跟进记录", ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(OnlyViewEnum.NO.getCode())),
    ONLINE_SERVICE_RECORDS("onlineServiceRecords", "线上服务记录", ComplaintStatusEnum.PENDING_ORDER.getCode(), Arrays.asList(OnlyViewEnum.NO.getCode(), OnlyViewEnum.YES.getCode()));
    public final String type;
    public final String desc;
    public final Integer minCpStatus;
    public List<Integer> viewTypeList;

    public static List<ConsultDetailTabEnum> listTab(Integer viewType, Integer cpStatus) {
        boolean viewOnly = OnlyViewEnum.YES.getCode().equals(viewType);
        List<ConsultDetailTabEnum> list = new ArrayList<>();
        for (ConsultDetailTabEnum tabEnum : ConsultDetailTabEnum.values()) {
            if (tabEnum.getViewTypeList().contains(viewType)) {
                if (!viewOnly && cpStatus != null) {
                    if (cpStatus >= tabEnum.getMinCpStatus()){
                        list.add(tabEnum);
                    }
                } else {
                    list.add(tabEnum);
                }
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

}
