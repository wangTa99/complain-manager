package com.wt.complaint.manage.domain.strategy.operate;

import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;

/**
 * @author linjiehong
 * @date 2025/5/21 16:36
 */
public interface UserComplaintOperateStrategy {
    /**
     * 创建客诉类单
     */
    String createOrderWithLock(CreateOrderSoIn soIn);

    /**
     * 接单
     */
    String PickUpOrder(OrderPickUpSoIn soIn);

    /**
     * 催单
     */
    String remindOrderWithLock(OrderRemindSoIn soIn);

    /**
     * 添加跟进记录
     */
    String addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn);

    /**
     * 举报判定
     */
    String judgeOrder(JudgeOrderSoIn soIn);
}
