package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.infrastructure.model.ComplaintRelationOrderDO;
import com.wt.complaint.manage.infrastructure.model.param.RelationOrderListParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RelationOrderConverter {
    RelationOrderConverter INSTANCE = Mappers.getMapper(RelationOrderConverter.class);

    ComplaintRelationOrderDO toDO(ComplaintRelationOrderGoIn goIn);

    RelationOrderListParam toListParam(ComplaintRelationOrderListGoIn source);

    ComplaintRelationOrderGoOut toGoOut(ComplaintRelationOrderDO source);

    List<ComplaintRelationOrderGoOut> toGoOut(List<ComplaintRelationOrderDO> source);
}
