package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.OwnerInfoItemGoOut;

import java.util.List;

public interface CarRemoteGateway {
    List<CarInfoGoOut> getCarSimpleInfo(List<String> vidList, List<String> vinList);

    List<OwnerInfoItemGoOut> getOwnerInfo(String midStr, List<String> vidList);

    GetDynamicInfoResponseGoOut getDynamicInfo(List<String> vidList);

    String getVinByVid(String vid);

    CarInfoGoOut getCarInfoByVid(String vid);

    String getVidByVin(String vin);
}
