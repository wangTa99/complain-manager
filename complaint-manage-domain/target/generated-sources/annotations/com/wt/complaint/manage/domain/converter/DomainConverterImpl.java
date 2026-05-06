package com.wt.complaint.manage.domain.converter;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.resp.ClosingTagDTO;
import com.wt.complaint.manage.api.model.resp.ClosingTagDTO.ClosingTagDTOBuilder;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintRelationClosingTagSoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.complaint.manage.domain.model.CreateChatGroupEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:58+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class DomainConverterImpl implements DomainConverter {

    @Override
    public ClosingTagDTO toTagDto(ComplaintRelationClosingTagSoOut source) {
        if ( source == null ) {
            return null;
        }

        ClosingTagDTOBuilder closingTagDTO = ClosingTagDTO.builder();

        closingTagDTO.closingTagIdLink( source.getClosingTagIdLink() );
        closingTagDTO.closingTagNameLink( source.getClosingTagNameLink() );

        return closingTagDTO.build();
    }

    @Override
    public List<ClosingTagDTO> toTagDtoList(List<ComplaintRelationClosingTagSoOut> source) {
        if ( source == null ) {
            return null;
        }

        List<ClosingTagDTO> list = new ArrayList<ClosingTagDTO>( source.size() );
        for ( ComplaintRelationClosingTagSoOut complaintRelationClosingTagSoOut : source ) {
            list.add( toTagDto( complaintRelationClosingTagSoOut ) );
        }

        return list;
    }

    @Override
    public AttachmentSoIn toAttachment(Attachment source) {
        if ( source == null ) {
            return null;
        }

        AttachmentSoIn attachmentSoIn = new AttachmentSoIn();

        attachmentSoIn.setId( source.getId() );
        attachmentSoIn.setFileName( source.getFileName() );
        attachmentSoIn.setUrl( source.getUrl() );
        attachmentSoIn.setType( source.getType() );

        return attachmentSoIn;
    }

    @Override
    public List<AttachmentSoIn> toAttachmentList(List<Attachment> source) {
        if ( source == null ) {
            return null;
        }

        List<AttachmentSoIn> list = new ArrayList<AttachmentSoIn>( source.size() );
        for ( Attachment attachment : source ) {
            list.add( toAttachment( attachment ) );
        }

        return list;
    }

    @Override
    public ComplaintOrderGoOut toGoOut(ComplaintOrderInfoGoIn source) {
        if ( source == null ) {
            return null;
        }

        ComplaintOrderGoOut complaintOrderGoOut = new ComplaintOrderGoOut();

        complaintOrderGoOut.setVinSufix( stringToInt( source.getVinSufix() ) );
        complaintOrderGoOut.setId( source.getId() );
        complaintOrderGoOut.setIdempotentKey( source.getIdempotentKey() );
        complaintOrderGoOut.setComplaintNo( source.getComplaintNo() );
        complaintOrderGoOut.setComplaintType( source.getComplaintType() );
        complaintOrderGoOut.setRiskLevel( source.getRiskLevel() );
        complaintOrderGoOut.setVid( source.getVid() );
        complaintOrderGoOut.setCarNo( source.getCarNo() );
        complaintOrderGoOut.setCarType( source.getCarType() );
        complaintOrderGoOut.setResponsibility( source.getResponsibility() );
        complaintOrderGoOut.setSuperTicketNo( source.getSuperTicketNo() );
        complaintOrderGoOut.setSoNo( source.getSoNo() );
        complaintOrderGoOut.setOrgId( source.getOrgId() );
        complaintOrderGoOut.setContactNameC( source.getContactNameC() );
        complaintOrderGoOut.setContactGender( source.getContactGender() );
        complaintOrderGoOut.setContactPhoneC( source.getContactPhoneC() );
        complaintOrderGoOut.setContactPhoneMd5( source.getContactPhoneMd5() );
        complaintOrderGoOut.setContactPhoneSufix( source.getContactPhoneSufix() );
        complaintOrderGoOut.setStatus( source.getStatus() );
        complaintOrderGoOut.setProblemDesc( source.getProblemDesc() );
        complaintOrderGoOut.setComplaintContent( source.getComplaintContent() );
        complaintOrderGoOut.setReminderTimes( source.getReminderTimes() );
        complaintOrderGoOut.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintOrderGoOut.setOperatorMid( source.getOperatorMid() );
        complaintOrderGoOut.setCreateMid( source.getCreateMid() );
        complaintOrderGoOut.setFinishTime( source.getFinishTime() );
        complaintOrderGoOut.setFirstResponseTime( source.getFirstResponseTime() );
        complaintOrderGoOut.setCreateTime( source.getCreateTime() );
        complaintOrderGoOut.setUpdateTime( source.getUpdateTime() );
        complaintOrderGoOut.setCityId( source.getCityId() );
        complaintOrderGoOut.setZoneId( source.getZoneId() );
        complaintOrderGoOut.setLittleZoneId( source.getLittleZoneId() );
        complaintOrderGoOut.setTestTag( source.getTestTag() );
        complaintOrderGoOut.setProblemCategory( source.getProblemCategory() );
        complaintOrderGoOut.setUserDemand( source.getUserDemand() );
        complaintOrderGoOut.setOnlyView( source.getOnlyView() );
        complaintOrderGoOut.setMediaInvolved( source.getMediaInvolved() );
        complaintOrderGoOut.setMediaLink( source.getMediaLink() );
        complaintOrderGoOut.setUpgradeTime( source.getUpgradeTime() );
        complaintOrderGoOut.setExemptionApplyTimes( source.getExemptionApplyTimes() );
        complaintOrderGoOut.setCreateSource( source.getCreateSource() );

        return complaintOrderGoOut;
    }

    @Override
    public ComplaintOrderInfoGoIn toGoIn(ComplaintOrderGoOut source) {
        if ( source == null ) {
            return null;
        }

        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = new ComplaintOrderInfoGoIn();

        complaintOrderInfoGoIn.setVinSufix( intToString( source.getVinSufix() ) );
        complaintOrderInfoGoIn.setId( source.getId() );
        complaintOrderInfoGoIn.setIdempotentKey( source.getIdempotentKey() );
        complaintOrderInfoGoIn.setComplaintNo( source.getComplaintNo() );
        complaintOrderInfoGoIn.setComplaintType( source.getComplaintType() );
        complaintOrderInfoGoIn.setRiskLevel( source.getRiskLevel() );
        complaintOrderInfoGoIn.setVid( source.getVid() );
        complaintOrderInfoGoIn.setCarNo( source.getCarNo() );
        complaintOrderInfoGoIn.setCarType( source.getCarType() );
        complaintOrderInfoGoIn.setResponsibility( source.getResponsibility() );
        complaintOrderInfoGoIn.setSuperTicketNo( source.getSuperTicketNo() );
        complaintOrderInfoGoIn.setSoNo( source.getSoNo() );
        complaintOrderInfoGoIn.setOrgId( source.getOrgId() );
        complaintOrderInfoGoIn.setContactNameC( source.getContactNameC() );
        complaintOrderInfoGoIn.setContactGender( source.getContactGender() );
        complaintOrderInfoGoIn.setContactPhoneC( source.getContactPhoneC() );
        complaintOrderInfoGoIn.setContactPhoneMd5( source.getContactPhoneMd5() );
        complaintOrderInfoGoIn.setContactPhoneSufix( source.getContactPhoneSufix() );
        complaintOrderInfoGoIn.setStatus( source.getStatus() );
        complaintOrderInfoGoIn.setProblemDesc( source.getProblemDesc() );
        complaintOrderInfoGoIn.setComplaintContent( source.getComplaintContent() );
        complaintOrderInfoGoIn.setReminderTimes( source.getReminderTimes() );
        complaintOrderInfoGoIn.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintOrderInfoGoIn.setOperatorMid( source.getOperatorMid() );
        complaintOrderInfoGoIn.setCreateMid( source.getCreateMid() );
        complaintOrderInfoGoIn.setFinishTime( source.getFinishTime() );
        complaintOrderInfoGoIn.setFirstResponseTime( source.getFirstResponseTime() );
        complaintOrderInfoGoIn.setCreateTime( source.getCreateTime() );
        complaintOrderInfoGoIn.setUpdateTime( source.getUpdateTime() );
        complaintOrderInfoGoIn.setLittleZoneId( source.getLittleZoneId() );
        complaintOrderInfoGoIn.setZoneId( source.getZoneId() );
        complaintOrderInfoGoIn.setCityId( source.getCityId() );
        complaintOrderInfoGoIn.setTestTag( source.getTestTag() );
        complaintOrderInfoGoIn.setProblemCategory( source.getProblemCategory() );
        complaintOrderInfoGoIn.setUserDemand( source.getUserDemand() );
        complaintOrderInfoGoIn.setOnlyView( source.getOnlyView() );
        complaintOrderInfoGoIn.setMediaInvolved( source.getMediaInvolved() );
        complaintOrderInfoGoIn.setMediaLink( source.getMediaLink() );
        complaintOrderInfoGoIn.setUpgradeTime( source.getUpgradeTime() );
        complaintOrderInfoGoIn.setCreateSource( source.getCreateSource() );
        complaintOrderInfoGoIn.setExemptionApplyTimes( source.getExemptionApplyTimes() );

        return complaintOrderInfoGoIn;
    }

    @Override
    public ComplaintOrderInfoGoIn convertToGoIn(DeliverComplaintBO source) {
        if ( source == null ) {
            return null;
        }

        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = new ComplaintOrderInfoGoIn();

        complaintOrderInfoGoIn.setComplaintNo( source.getDrNo() );
        complaintOrderInfoGoIn.setDeliverRetailComplaintStatus( source.getOrderStatus() );
        complaintOrderInfoGoIn.setResponsibility( responsibleToResponsibility( source.getResponsible() ) );
        complaintOrderInfoGoIn.setFinishTime( source.getRealFinishTime() );
        complaintOrderInfoGoIn.setFirstResponseTime( source.getRealFirstResponseTime() );
        complaintOrderInfoGoIn.setId( source.getId() );
        complaintOrderInfoGoIn.setIdempotentKey( source.getIdempotentKey() );
        complaintOrderInfoGoIn.setComplaintType( source.getComplaintType() );
        complaintOrderInfoGoIn.setRiskLevel( source.getRiskLevel() );
        complaintOrderInfoGoIn.setSuperTicketNo( source.getSuperTicketNo() );
        complaintOrderInfoGoIn.setSoNo( source.getSoNo() );
        complaintOrderInfoGoIn.setOrgId( source.getOrgId() );
        complaintOrderInfoGoIn.setContactNameC( source.getContactNameC() );
        complaintOrderInfoGoIn.setContactPhoneC( source.getContactPhoneC() );
        complaintOrderInfoGoIn.setContactPhoneMd5( source.getContactPhoneMd5() );
        complaintOrderInfoGoIn.setProblemDesc( source.getProblemDesc() );
        complaintOrderInfoGoIn.setComplaintContent( source.getComplaintContent() );
        complaintOrderInfoGoIn.setReminderTimes( source.getReminderTimes() );
        complaintOrderInfoGoIn.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintOrderInfoGoIn.setOperatorMid( source.getOperatorMid() );
        complaintOrderInfoGoIn.setCreateMid( source.getCreateMid() );
        complaintOrderInfoGoIn.setCreateTime( source.getCreateTime() );
        complaintOrderInfoGoIn.setUpdateTime( source.getUpdateTime() );
        if ( source.getLittleZoneId() != null ) {
            complaintOrderInfoGoIn.setLittleZoneId( String.valueOf( source.getLittleZoneId() ) );
        }
        if ( source.getZoneId() != null ) {
            complaintOrderInfoGoIn.setZoneId( String.valueOf( source.getZoneId() ) );
        }
        if ( source.getCityId() != null ) {
            complaintOrderInfoGoIn.setCityId( String.valueOf( source.getCityId() ) );
        }
        complaintOrderInfoGoIn.setTestTag( source.getTestTag() );
        complaintOrderInfoGoIn.setProblemCategory( source.getProblemCategory() );

        return complaintOrderInfoGoIn;
    }

    @Override
    public CreateChatGroupEvent convertToCreateChatGroupEvent(RetailComplaintOrderInfoGoIn source) {
        if ( source == null ) {
            return null;
        }

        CreateChatGroupEvent createChatGroupEvent = new CreateChatGroupEvent();

        createChatGroupEvent.setDrNo( source.getDrNo() );
        createChatGroupEvent.setOrgId( source.getOrgId() );
        createChatGroupEvent.setZoneId( source.getZoneId() );
        createChatGroupEvent.setLittleZoneId( source.getLittleZoneId() );
        createChatGroupEvent.setCustomerServiceMid( source.getCustomerServiceMid() );
        createChatGroupEvent.setOperatorMid( source.getOperatorMid() );
        createChatGroupEvent.setOperatorPositionId( source.getOperatorPositionId() );
        createChatGroupEvent.setCreateTime( source.getCreateTime() );
        createChatGroupEvent.setComplaintContent( source.getComplaintContent() );
        createChatGroupEvent.setRiskLevel( source.getRiskLevel() );
        createChatGroupEvent.setContactNameC( source.getContactNameC() );
        createChatGroupEvent.setContactPhoneC( source.getContactPhoneC() );
        createChatGroupEvent.setProblemDesc( source.getProblemDesc() );

        return createChatGroupEvent;
    }

    @Override
    public List<ComplaintOrderInfoGoIn> convertToGoInList(List<DeliverComplaintBO> source) {
        if ( source == null ) {
            return null;
        }

        List<ComplaintOrderInfoGoIn> list = new ArrayList<ComplaintOrderInfoGoIn>( source.size() );
        for ( DeliverComplaintBO deliverComplaintBO : source ) {
            list.add( convertToGoIn( deliverComplaintBO ) );
        }

        return list;
    }

    @Override
    public ComplaintBasicInfo convertToBasicInfo(RetailComplaintOrderInfoGoIn source) {
        if ( source == null ) {
            return null;
        }

        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();

        complaintBasicInfo.setStNo( source.getSuperTicketNo() );
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
    public ComplaintBasicInfo convertToBasicInfo(RetailComplaintDetaiGoOut source) {
        if ( source == null ) {
            return null;
        }

        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();

        complaintBasicInfo.setStNo( source.getSuperTicketNo() );
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
    public ComplaintBasicInfo convertToBasicInfo(DeliverComplaintBO source) {
        if ( source == null ) {
            return null;
        }

        ComplaintBasicInfo complaintBasicInfo = new ComplaintBasicInfo();

        complaintBasicInfo.setStNo( source.getSuperTicketNo() );
        complaintBasicInfo.setDrNo( source.getDrNo() );
        complaintBasicInfo.setCustomerServiceMid( source.getCustomerServiceMid() );
        complaintBasicInfo.setOperatorPositionId( source.getOperatorPositionId() );
        complaintBasicInfo.setOperatorMid( source.getOperatorMid() );
        complaintBasicInfo.setZoneId( source.getZoneId() );
        complaintBasicInfo.setLittleZoneId( source.getLittleZoneId() );
        complaintBasicInfo.setOrgId( source.getOrgId() );

        return complaintBasicInfo;
    }
}
