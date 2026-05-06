package com.wt.complaint.manage.domain.api.service.interfaces;

import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchLightInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailFrameGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchLightInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchSoOut;

/**
 * @author: p-wangkai95
 * @date: 2024/8/22 10:42
 * @description: 用户单据查看服务接口
 * @version: 1.0
 */
public interface UserComplaintViewService {

    /**
     * 查询用户单据列表
     */
    UserComplaintListSearchSoOut searchUserComplaintList(UserComplaintListSearchGoIn goIn);

    /**
     * 获取用户单据详情框架
     */
    UserComplaintDetailFrameSoOut getUserComplaintFrame(UserComplaintDetailFrameGoIn goIn);

    /**
     * 获取用户单据详情
     */
    UserComplaintDetailSoOut getUserComplaintDetail(UserComplaintDetailGoIn goIn);

    /**
     * 获取投诉单信�?
     */
    UcOrderBatchInfoSoOut getUcOrderInfo(UcOrderBatchInfoSoIn soIn);

    /**
     * 获取投诉单精简信息
     */
    UcOrderBatchLightInfoSoOut getUcOrderLightInfo(UcOrderBatchLightInfoSoIn soIn);
}
