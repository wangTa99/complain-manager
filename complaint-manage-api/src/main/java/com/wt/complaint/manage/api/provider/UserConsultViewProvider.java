package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.ComplaintDetailFrameReq;
import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultDetailReq;
import com.wt.complaint.manage.api.model.req.consult.ConsultListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;

import com.wt.complaint.manage.api.model.req.consult.StatisticsItemReq;
import com.wt.complaint.manage.api.model.resp.ComplaintDetailFrameResp;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultDetailResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultListResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultSelectorResp;
import com.wt.complaint.manage.api.model.resp.consult.ConsultStatisticsItemResp;
import com.xiaomi.youpin.infra.rpc.Result;

import javax.validation.Valid;

/**
 * 咨询单视�?
 */
public interface UserConsultViewProvider {

    /**
     * 咨询单关键统计项查询
     * @param req 请求参数
     * @return 统计项列�?
     */
    Result<ConsultStatisticsItemResp> queryStatisticsItems(@Valid StatisticsItemReq req);

    /**
     * 咨询单列表查�?
     * @param req 请求参数
     * @return 咨询单列�?
     */
    Result<ConsultListResp> padList(@Valid PadConsultListReq req);

    /**
     * 咨询单详�?
     * @param req 请求参数
     * @return 咨询单详�?
     */
    Result<ConsultDetailResp> detail(@Valid ConsultDetailReq req);

    /**
     * 售后工作台咨询单列表查询
     * @param req 请求参数
     * @return 咨询单列�?
     */
    Result<ConsultListResp> webList(@Valid ConsultListReq req);

    /**
     * 售后工作台咨询单详情查询
     * @param req 请求参数
     * @return 咨询单详�?
     */
    Result<ConsultDetailResp> webDetail(@Valid ConsultDetailReq req);

    /**
     * 获取咨询单枚举下拉列�?
     * @return 枚举下拉列表
     */
    Result<ConsultSelectorResp> getConsultSelectorList();

    /**
     * 查询跟进人信�?
     * @param req
     * @return
     */
    Result<ConsultHandlerListResp> getConsultHandlerList(ConsultHandlerListReq req);

    Result<ComplaintDetailFrameResp> getComplaintAuth(ComplaintDetailFrameReq req);

}
