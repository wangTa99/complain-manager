package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AttachmentConvert {
    AttachmentConvert INSTANCE = Mappers.getMapper(AttachmentConvert.class);

    AttachmentGoIn toAttachmentGoIn(AttachmentSoIn source);

    List<AttachmentGoIn> toAttachmentGoIn(List<AttachmentSoIn> source);
}
