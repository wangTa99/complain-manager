package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.infrastructure.model.ComplaintAuditDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuditConverter {
    AuditConverter INSTANCE = Mappers.getMapper(AuditConverter.class);

    ComplaintAuditDO toDO(ComplaintAuditGoIn source);
}
