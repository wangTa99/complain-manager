package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderCreateSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListGoOut;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:57+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class OrderOperationConverterImpl implements OrderOperationConverter {

    @Override
    public ComplaintOrderInfoGoIn toCreateGoIn(ComplaintOrderCreateSoIn source) {
        if ( source == null ) {
            return null;
        }

        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = new ComplaintOrderInfoGoIn();

        complaintOrderInfoGoIn.setComplaintNo( source.getComplaintNo() );
        complaintOrderInfoGoIn.setVid( source.getVid() );
        complaintOrderInfoGoIn.setVin( source.getVin() );
        complaintOrderInfoGoIn.setCarType( source.getCarType() );
        complaintOrderInfoGoIn.setSuperTicketNo( source.getSuperTicketNo() );
        complaintOrderInfoGoIn.setSoNo( source.getSoNo() );
        complaintOrderInfoGoIn.setCreateMid( source.getCreateMid() );
        complaintOrderInfoGoIn.setCityId( source.getCityId() );
        complaintOrderInfoGoIn.setTestTag( source.getTestTag() );
        complaintOrderInfoGoIn.setCreateSource( source.getCreateSource() );

        return complaintOrderInfoGoIn;
    }

    @Override
    public AttachmentGoIn toAttachmentGoIn(AttachmentSoIn source) {
        if ( source == null ) {
            return null;
        }

        AttachmentGoIn attachmentGoIn = new AttachmentGoIn();

        attachmentGoIn.setId( source.getId() );
        attachmentGoIn.setUrl( source.getUrl() );
        attachmentGoIn.setFileName( source.getFileName() );
        attachmentGoIn.setType( source.getType() );

        return attachmentGoIn;
    }

    @Override
    public List<AttachmentGoIn> toAttachmentGoIn(List<AttachmentSoIn> source) {
        if ( source == null ) {
            return null;
        }

        List<AttachmentGoIn> list = new ArrayList<AttachmentGoIn>( source.size() );
        for ( AttachmentSoIn attachmentSoIn : source ) {
            list.add( toAttachmentGoIn( attachmentSoIn ) );
        }

        return list;
    }

    @Override
    public ComplaintBasicInfo toBasicInfo(RetailComplaintListGoOut source) {
        if ( source == null ) {
            return null;
        }

        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();

        complaintBasicInfo.setDrNo( source.getDrNo() );
        complaintBasicInfo.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintBasicInfo.setOperatorPositionId( source.getOperatorPositionId() );
        complaintBasicInfo.setOperatorMid( source.getOperatorMid() );
        complaintBasicInfo.setZoneId( source.getZoneId() );
        complaintBasicInfo.setLittleZoneId( source.getLittleZoneId() );
        complaintBasicInfo.setOrgId( source.getOrgId() );

        return complaintBasicInfo;
    }

    @Override
    public ComplaintBasicInfo toBasicInfo(DeliverComplaintListGoOut source) {
        if ( source == null ) {
            return null;
        }

        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();

        complaintBasicInfo.setDrNo( source.getDrNo() );
        complaintBasicInfo.setOperatorPositionId( source.getOperatorPositionId() );
        complaintBasicInfo.setOperatorMid( source.getOperatorMid() );
        complaintBasicInfo.setOperatorName( source.getOperatorName() );
        complaintBasicInfo.setZoneId( source.getZoneId() );
        complaintBasicInfo.setLittleZoneId( source.getLittleZoneId() );
        complaintBasicInfo.setOrgId( source.getOrgId() );

        return complaintBasicInfo;
    }
}
