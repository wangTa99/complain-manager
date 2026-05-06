package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderOperationConverter {
    OrderOperationConverter INSTANCE = Mappers.getMapper(OrderOperationConverter.class);

    ComplaintOrderInfoGoIn toCreateGoIn(ComplaintOrderCreateSoIn source);

    AttachmentGoIn toAttachmentGoIn(AttachmentSoIn source);

    List<AttachmentGoIn> toAttachmentGoIn(List<AttachmentSoIn> source);

    ComplaintBasicInfo toBasicInfo(RetailComplaintListGoOut source);

    ComplaintBasicInfo toBasicInfo(DeliverComplaintListGoOut source);

}
