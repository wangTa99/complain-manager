package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.CarUserAggGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;

public interface CarUserRemoteGateway {
    CarUserAggGoOut userAggQuery(CarUserAggGoIn goIn);
}
