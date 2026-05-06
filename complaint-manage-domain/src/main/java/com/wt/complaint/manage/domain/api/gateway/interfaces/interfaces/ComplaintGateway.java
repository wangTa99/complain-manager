package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;

import java.util.List;

/**
 * 客诉网关�?
 */
public interface ComplaintGateway {

    ComplaintOrderGoOut selectByComplaintNo(String complaintNo);

    ComplaintListSearchSoOut getComplaintOrderList(ComplaintListSearchGoIn goIn);

    Integer getComplaintOrderCount(ComplaintListSearchGoIn goIn);

    List<ComplaintOrderGoOut> selectFirstResponseToTimeoutList();

    List<ComplaintOrderGoOut> selectFinishToTimeoutList();

    /**
     * 查询所有未结案超时的投�?
     * @return 投诉单列�?
     */
    List<ComplaintOrderGoOut> selectUnFinishedToTimeoutList();

    /**
     * 分页查询投诉单（简化版，不填充额外数据�?
     * @param goIn 查询条件
     * @return 投诉单列�?
     */
    List<ComplaintOrderGoOut> selectPageByParam(ComplaintListSearchGoIn goIn);

}
