package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

import java.util.Map;

@Data
public class GetVidRelationMapResponseBo {
    private Map<String, RelationItemBo> vidRelationMap;

}