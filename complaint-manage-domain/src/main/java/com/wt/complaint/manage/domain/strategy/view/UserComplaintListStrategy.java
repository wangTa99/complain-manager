package com.wt.complaint.manage.domain.strategy.view;

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

public interface UserComplaintListStrategy {

    UserComplaintListSearchSoOut searchUserComplaintList(UserComplaintListSearchGoIn goIn);

    UserComplaintDetailFrameSoOut getUserComplaintFrame(UserComplaintDetailFrameGoIn goIn);

    UserComplaintDetailSoOut getUserComplaintDetail(UserComplaintDetailGoIn goIn);

    UcOrderBatchInfoSoOut getUcOrderInfo(UcOrderBatchInfoSoIn soIn);

    UcOrderBatchLightInfoSoOut getUcOrderLightInfo(UcOrderBatchLightInfoSoIn soIn);
}
