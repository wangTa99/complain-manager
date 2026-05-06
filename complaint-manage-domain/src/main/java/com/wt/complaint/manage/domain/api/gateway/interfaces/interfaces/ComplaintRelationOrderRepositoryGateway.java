package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;

import java.util.List;

public interface ComplaintRelationOrderRepositoryGateway {
    Boolean save(ComplaintRelationOrderGoIn goIn);
    Boolean update(ComplaintRelationOrderGoIn goIn);
    List<ComplaintRelationOrderGoOut> findList(ComplaintRelationOrderListGoIn goIn);
}
