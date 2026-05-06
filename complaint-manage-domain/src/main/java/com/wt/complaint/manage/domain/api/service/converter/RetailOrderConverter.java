package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.api.model.req.operate.TemplateField;
import com.wt.complaint.manage.api.model.req.retail.CreateRetailComplaintOrderReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.CreateRetailComplaintOrderSoIn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface RetailOrderConverter {
    RetailOrderConverter INSTANCE = Mappers.getMapper(RetailOrderConverter.class);

    @Mapping(source = "expand", target = "expandSoIn")
    CreateRetailComplaintOrderSoIn toCreateSoIn(CreateRetailComplaintOrderReq req);

    @Mapping(target = "attachmentList", source = "attachments")
    TemplateFieldSoIn toFieldSoIn(TemplateField source);
}
