package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.car.soc.gw.api.dto.res.MrOrderSimple;

import java.util.List;

public interface MrOrderGateway {
    List<MrOrderSimple> getSimpleMrOrderInfo(List<String> stNoList);
}
