package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.WarrantyPeriodGoOut;

public interface WarrantyInfoGateway {

    WarrantyPeriodGoOut getCarWarrantyPeriodInfo(String vin);
}
