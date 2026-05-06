package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetSelectBasicDataSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailHasFirstResponseRecordFlagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetaiSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailHasFirstResposeRecordFlagSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.StaticTabCountSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.BubbleCountSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailAuthSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintListSearchSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.StaticRetailCountSoIn;

/**
 * 零售投诉视图服务
 *
 * @author p-wangkai95
 * @version 1.0
 */
public interface RetailComplaintViewService {

    /**
     * 获取下拉框基础数据
     *
     * @param miID 用户ID
     * @return 下拉框基础数据响应结果
     */
    GetSelectBasicDataSoOut getSelectBasicData(String miID);

    /**
     * 获取气泡数量
     *
     * @param miID 用户ID
     * @return 气泡数量响应结果
     */
    BubbleCountSoOut getBubbleCount(String miID);

    /**
     * 获取气泡数量V2
     *
     * @param miID    用户ID
     * @param orgCode 下钻门店
     * @return 气泡数量响应结果
     */
    BubbleCountSoOut getBubbleCountV2(String miID, String orgCode);

    /**
     * 统计TAB数量
     *
     * @param staticRetailCountSoIn 统计TAB数量请求参数
     * @return 统计TAB数量响应结果
     */
    StaticTabCountSoOut staticTabCount(StaticRetailCountSoIn staticRetailCountSoIn);

    /**
     * 获取投诉详情框架信息
     *
     * @param retailComplaintDetailFrameSoIn 详情框架请求参数
     * @return 详情框架响应结果
     */
    RetailComplaintDetailFrameSoOut getRetailComplaintDetailAuth(
            RetailComplaintDetailAuthSoIn retailComplaintDetailFrameSoIn);

    /**
     * 搜索投诉列表
     *
     * @param retailComplaintListSearchSoIn 搜索请求参数
     * @return 搜索响应结果
     */
    RetailComplaintListSearchSoOut searchRetailComplaintList(
            RetailComplaintListSearchSoIn retailComplaintListSearchSoIn);

    /**
     * 获取投诉详情
     *
     * @param retailComplaintDetailSoIn 投诉详情请求参数
     * @return 投诉详情响应结果
     */
    RetailComplaintDetaiSoOut getRetailComplaintDetail(RetailComplaintDetailSoIn retailComplaintDetailSoIn);

    /**
     * 获取投诉单是否有首响记录标识
     *
     * @param soIn 获取首响记录标识请求参数
     * @return 获取首响记录标识响应结果
     */
    RetailHasFirstResposeRecordFlagSoOut getRetailHasFirstResposeRecordFlag(
            RetailHasFirstResponseRecordFlagSoIn soIn);

}
