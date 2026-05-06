package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;

/**
 * 汽车信息查看服务
 */
public interface ComplaintViewService {
    /**
     * 获取客诉框架结构信息
     *
     * @param param
     * @return
     */
    ComplaintFrameInfoSoOut getComplaintFrameInfo(ComplaintFrameInfoSoIn param);

    ComplaintFrameInfoSoOut getComplaintAuth(ComplaintFrameInfoSoIn param);

    /**
     * 获取投诉信息tab页数�?
     *
     * @param param
     * @return
     */
    ComplaintDetailSoOut getComplaintDetail(ComplaintDetailSoIn param);

    /**
     * 批量获取投诉信息tab页数�?
     *
     * @param param
     * @return
     */
    ComplaintBatchDetailSoOut batchGetComplaintDetail(ComplaintBatchDetailSoIn param);

    /**
     * 获取跟进记录列表
     *
     * @param param
     * @return
     */
    ComplaintProcessListSoOut getComplaintProcessRecords(ComplaintProcessSoIn param);

    /**
     * 查询投诉单列表接�?
     */
    ComplaintListSearchSoOut searchComplaintList(ComplaintListSearchGoIn param);

    CountComplaintListTabSoOut countComplaintListTab(ComplaintListSearchGoIn param);

    SimpleComplaintDetailSoOut getSimpleComplaintDetail(SimpleComplaintDetailSoIn soIn);

    GetComplaintHandlerSoOut getComplaintHandler(GetComplaintHandlerSoIn soIn);

    /**
     * 获取投诉单编辑详情，用于编辑页回�?
     * �?complaint_content 解析投诉场景( fieldCode=complaint )，以�?riskLevel、mediaInvolved、mediaLink
     * riskLevel 返回 code (1, 2, 3, 4)，而不是描�?(L1, L2, L3, L4)
     *
     * @param param �?complaintNo
     * @return 投诉场景、风险等�?code)、是否涉媒、涉媒链�?
     */
    ComplaintEditDetailSoOut getComplaintEditDetail(ComplaintDetailSoIn param);
}
