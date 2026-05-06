package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessApplyFinishListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessLastGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;

import java.util.List;
import java.util.Map;

public interface ComplaintFollowProcessRepositoryGateway {
    Boolean saveComplaintFollowProcess(ComplaintFollowProcessGoIn complaintFollowProcess);

    List<ComplaintFollowProcessGoOut> getProcessListByNo(String complaintId);

    /**
     * 根据流程ID获取跟进记录列表
     * @param processInstanceId 流程ID
     * @return List<跟进记录>
     */
    List<ComplaintFollowProcessGoOut> getProcessListByProcessInstanceId(String processInstanceId);

    List<ComplaintFollowProcessGoOut> getProcessList(ComplaintProcessListGoIn listGoIn);

    List<ComplaintFollowProcessGoOut> getLastApplyFinishRecordByParam(ComplaintProcessApplyFinishListGoIn complaintProcessApplyFinishListGoIn);

    /**
     * 批量查询最新一次提交复盘跟进记录（process_type=SUBMIT_REVIEW�?
     * @param goIn 投诉单号列表
     * @return 每个投诉单最新一条提交复盘记�?
     */
    List<ComplaintFollowProcessGoOut> getLastSubmitReviewRecordByParam(ComplaintProcessApplyFinishListGoIn goIn);

    /**
     * 获取最后一条跟进记�?
     * @param goIn 投诉编号, 操作类型
     * @return Map<投诉编号, 最近一条跟进记�?
     */
    Map<String ,ComplaintFollowProcessGoOut> getLastProcess(ComplaintProcessLastGoIn goIn);

    /**
     * 查询需要修改岗位名的交付客诉单
     * @return 操作记录列表
     */
    List<ComplaintFollowProcessGoOut> selectNeedFixDeliverProcessList();

    /**
     * 根据id更新processContent
     * @param updateProcessList 记录列表
     */
    void batchUpdateProcessContentById(List<ComplaintFollowProcessGoIn> updateProcessList);
}
