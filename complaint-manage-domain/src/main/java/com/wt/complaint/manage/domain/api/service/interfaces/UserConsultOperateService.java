package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;

/**
 * 咨询单操作服务接�?
 */
public interface UserConsultOperateService {

    /**
     * 创建咨询�?
     * @param soIn 创建咨询单入�?
     * @return 创建咨询单出�?
     */
    CreateConsultOrderSoOut createConsultOrder(CreateConsultOrderSoIn soIn);

    /**
     * 编辑咨询�?
     * @param soIn 编辑咨询单入�?
     * @return 编辑咨询单出�?
     */
    OrderEditConsultSoOut editConsult(OrderEditConsultSoIn soIn);

    /**
     * 接单
     * @param soIn 接单入参
     * @return 接单出参
     */
    ConsultOrderPickUpSoOut pickUpOrder(ConsultOrderPickUpSoIn soIn);

    /**
     * 添加跟进记录
     * @param soIn 添加跟进记录入参
     * @return 添加跟进记录出参
     */
    OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn);

    /**
     * 改派
     * @param soIn 改派入参
     * @return 改派出参
     */
    ConsultReassignSoOut reassign(ConsultReassignSoIn soIn);

    /**
     * 申请改派门店
     * @param soIn 申请改派门店入参
     * @return 申请改派门店出参
     */
    ConsultOrgChangeApplySoOut submitChangeOrgApply(ConsultOrgChangeApplySoIn soIn);

    /**
     * 更新处理�?
     * @param soIn 更新处理人入�?
     * @return 更新处理人出�?
     */
    ConsultUpdateHandlerSoOut updateHandler(ConsultUpdateHandlerSoIn soIn);

    /**
     * 结案
     * @param soIn 结案入参
     * @return 结案出参
     */
    ConsultFinishSoOut finish(ConsultFinishSoIn soIn);

    /**
     * 催单
     *
     * @param retailRemindOrderSoIn 催单入参
     * @return 催单出参
     */
    RemindOrderSoOut remindOrder(RetailRemindOrderSoIn retailRemindOrderSoIn);
}
