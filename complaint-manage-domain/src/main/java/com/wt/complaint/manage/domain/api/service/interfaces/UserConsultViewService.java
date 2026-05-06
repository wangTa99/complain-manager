package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultSelectorResp;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintFrameInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultStatisticsSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintFrameInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintProcessListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.ConsultStatisticsSoOut;

/**
 * 咨询单视图服务接�?
 */
public interface UserConsultViewService {

    /**
     * 获取咨询单枚举下拉列�?
     */
    ConsultSelectorResp getConsultSelectorList();

    /**
     * 查询咨询单统计项
     */
    ConsultStatisticsSoOut queryStatisticsItems(ConsultStatisticsSoIn soIn);

    /**
     * 分页查询咨询单列表（PAD端：vin脱敏�?
     */
    ConsultListSoOut queryConsultList(ConsultListSoIn soIn);

    /**
     * 查询咨询单详情（PAD端）
     */
    ConsultDetailSoOut queryConsultDetail(ConsultDetailSoIn soIn);

    /**
     * 分页查询咨询单列表（Web端：带门店信息，vin不脱敏）
     */
    ConsultListSoOut queryWebConsultList(ConsultListSoIn soIn);

    /**
     * 查询咨询单详情（Web端）
     */
    ConsultDetailSoOut queryWebConsultDetail(ConsultDetailSoIn soIn);

    ConsultHandlerListResp getConsultHandler(ConsultHandlerListReq req);

    ConsultListSoOut queryPadConsultList(PadConsultListReq req);


    ComplaintFrameInfoSoOut getComplaintAuth(ComplaintFrameInfoSoIn param);

    /**
     * 获取跟进记录列表
     *
     * @param param
     * @return
     */
    ComplaintProcessListSoOut getComplaintProcessRecords(ComplaintProcessSoIn param);
}
