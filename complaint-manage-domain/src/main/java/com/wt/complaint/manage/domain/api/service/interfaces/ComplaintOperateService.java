package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.SubmitReviewSoOut;

public interface ComplaintOperateService {
    ComplaintOrderCreateSoOut createComplaintOrder(ComplaintOrderCreateSoIn soIn);

    OrderPickUpSoOut pickUpOrder(OrderPickUpSoIn soIn);

    OrderUpdateHandlerSoOut updateHandler(OrderUpdateHandlerSoIn soIn);

    OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn);

    /**
     * 添加跟进记录V2，支持车辆行驶里程校�?
     */
    OrderFollowUpRecordSoOut addFollowUpRecordsV2(OrderAddFollowUpRecordSoInV2 soIn);

    OrderAddDistributionRecordSoOut addDistributionRecords(OrderAddDistributionRecordSoIn soIn);

    OrderRemindSoOut remindOrder(OrderRemindSoIn soIn);

    OrderUpdateCustomerServiceSoOut updateCustomerService(OrderUpdateCustomerServiceSoIn soIn);

    /**
     * 升级客诉�?
     * @param soIn 升级参数
     * @return 升级处理结果
     */
    OrderUpdateHandlerSoOut upgradeComplaintOrder(ComplaintOrderUpgradeSoIn soIn);

    OrderEditComplaintSoOut editComplaint(OrderEditComplaintSoIn soIn);

    /**
     * 提交复盘（客诉三期）
     * 仅支持：创建来源=线上客服、投诉分�?服务投诉、未提交过复盘、状态≠申请改派门店待审�?
     *
     * @param soIn 提交复盘入参
     * @return 提交结果
     */
    SubmitReviewSoOut submitReview(SubmitReviewSoIn soIn);

}
