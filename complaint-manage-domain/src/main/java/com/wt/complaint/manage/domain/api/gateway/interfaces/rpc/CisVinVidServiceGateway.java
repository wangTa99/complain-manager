package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;



import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetVidRelationMapResponseBo;

import java.util.List;

/**
 * @author: qis
 * @date: 2023/11/10 18:29
 */
public interface CisVinVidServiceGateway {
    GetVidRelationMapResponseBo getVidRelationMap(List<String> vidList);
}
