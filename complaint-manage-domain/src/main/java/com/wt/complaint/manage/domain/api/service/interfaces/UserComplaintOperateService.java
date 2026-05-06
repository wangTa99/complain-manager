package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderFollowUpRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderPickUpSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderRemindSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderUpdateCustomerServiceSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.CreateOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.JudgeOrderSoOut;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * @author linjiehong
 * @date 2025/5/21 16:19
 */
public interface UserComplaintOperateService {
    /**
     * 创建客诉类单�?
     * @param soIn
     * @return
     */
    CreateOrderSoOut createOrder(CreateOrderSoIn soIn);

    /**
     * 催单
     * @param soIn
     * @return
     */
    OrderRemindSoOut remindOrder(OrderRemindSoIn soIn);

    /**
     * 接单
     * @param soIn
     * @return
     */
    OrderPickUpSoOut pickUpOrder(OrderPickUpSoIn soIn);

    /**
     * 添加跟进记录
     * @param soIn
     * @return
     */
    OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn);

    /**
     * 举报判定
     * @param soIn
     * @return
     */
    JudgeOrderSoOut judgeOrder(JudgeOrderSoIn soIn);

    /**
     * 更新客服信息
     * @param soIn
     * @return
     */
    OrderUpdateCustomerServiceSoOut updateCustomer(OrderUpdateCustomerServiceSoIn soIn);

}
