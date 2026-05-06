package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.retail.CreateRetailComplaintOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailApplyRetailCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailFollowRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailRemindOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailSubmitFinishApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.ChangeOrgCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.AddFollowRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.CreateRetailComplaintOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RemindOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;

/**
 * 零售投诉视图服务
 *
 * @author p-wangkai95
 * @version 1.0
 */
public interface RetailComplaintOperateService {

    /**
     * 创建投诉�?
     *
     * @param soIn 创建投诉单请求参�?
     * @return 创建投诉单响应结�?
     */
    CreateRetailComplaintOrderSoOut createComplaintOrder(CreateRetailComplaintOrderSoIn soIn);

    /**
     * 添加跟进记录
     *
     * @param soIn 添加跟进记录入参
     * @return 添加跟进记录出参
     */
    AddFollowRecordSoOut addFollowRecord(RetailFollowRecordSoIn soIn);

    /**
     * 催单
     *
     * @param retailRemindOrderSoIn 催单入参
     * @return 催单出参
     */
    RemindOrderSoOut remindOrder(RetailRemindOrderSoIn retailRemindOrderSoIn);

    /**
     * 提交完成申请
     * @param soIn 提交完成入参
     * @return 提交完成出参
     */
    String submitFinishApply(RetailSubmitFinishApplySoIn soIn);

    /**
     * 完成申请回调处理
     * @param soIn 回调请求参数
     */
    void applyFinishCallback(RetailApplyRetailCallBackSoIn soIn);

    /**
     * 零售客诉提交改派申请
     * @param soIn
     * @return
     */
    RetailComplaintApplySoOut submitChangeOrgApply(RetailComplaintApplySoIn soIn);

    /**
     * 零售客诉改派申请回调处理
     * @param soIn
     */
    void applyOrgChangeCallback(ChangeOrgCallBackSoIn soIn);

}
