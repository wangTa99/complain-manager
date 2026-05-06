package com.wt.complaint.manage.domain.stateflow;


import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;

import java.util.List;

public interface UserComplaintStatusEventHandler<T, R> {

    /**
     * 获取 客诉类单据类�?
     */
    UcOrderTypeEnum getUcOrderType();

    /**
     * 源状�?
     */
    List<Integer> getSourceList();

    /**
     * 目标状�?
     */
    Integer getTarget();

    /**
     * 处理状态变更逻辑
     */
    R handle(T param);

}
