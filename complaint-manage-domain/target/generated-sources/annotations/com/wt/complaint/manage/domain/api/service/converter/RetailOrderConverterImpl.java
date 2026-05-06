package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.operate.FieldValue;
import com.wt.complaint.manage.api.model.req.operate.RetailComplaintOrderCreateExpandDTO;
import com.wt.complaint.manage.api.model.req.operate.TemplateField;
import com.wt.complaint.manage.api.model.req.operate.TemplateStructInfo;
import com.wt.complaint.manage.api.model.req.retail.CreateRetailComplaintOrderReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.CreateRetailComplaintOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintOrderCreateExpandSoIn;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:57+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class RetailOrderConverterImpl implements RetailOrderConverter {

    @Override
    public CreateRetailComplaintOrderSoIn toCreateSoIn(CreateRetailComplaintOrderReq req) {
        if ( req == null ) {
            return null;
        }

        CreateRetailComplaintOrderSoIn createRetailComplaintOrderSoIn = new CreateRetailComplaintOrderSoIn();

        createRetailComplaintOrderSoIn.setExpandSoIn( retailComplaintOrderCreateExpandDTOToRetailComplaintOrderCreateExpandSoIn( req.getExpand() ) );
        createRetailComplaintOrderSoIn.setWorkType( req.getWorkType() );
        createRetailComplaintOrderSoIn.setSoNo( req.getSoNo() );
        createRetailComplaintOrderSoIn.setSuperTicketNo( req.getSuperTicketNo() );
        createRetailComplaintOrderSoIn.setIdempotentId( req.getIdempotentId() );
        createRetailComplaintOrderSoIn.setContactName( req.getContactName() );
        createRetailComplaintOrderSoIn.setContactTel( req.getContactTel() );
        createRetailComplaintOrderSoIn.setContactTitle( req.getContactTitle() );
        createRetailComplaintOrderSoIn.setTestTag( req.getTestTag() );
        createRetailComplaintOrderSoIn.setCreateMid( req.getCreateMid() );

        return createRetailComplaintOrderSoIn;
    }

    @Override
    public TemplateFieldSoIn toFieldSoIn(TemplateField source) {
        if ( source == null ) {
            return null;
        }

        TemplateFieldSoIn templateFieldSoIn = new TemplateFieldSoIn();

        templateFieldSoIn.setAttachmentList( attachmentListToAttachmentSoInList( source.getAttachments() ) );
        templateFieldSoIn.setId( source.getId() );
        templateFieldSoIn.setOrder( source.getOrder() );
        templateFieldSoIn.setRequired( source.getRequired() );
        templateFieldSoIn.setFieldType( source.getFieldType() );
        templateFieldSoIn.setFieldName( source.getFieldName() );
        templateFieldSoIn.setFieldCode( source.getFieldCode() );
        templateFieldSoIn.setValue( fieldValueListToFieldValueSoInList( source.getValue() ) );

        return templateFieldSoIn;
    }

    protected List<TemplateFieldSoIn> templateFieldListToTemplateFieldSoInList(List<TemplateField> list) {
        if ( list == null ) {
            return null;
        }

        List<TemplateFieldSoIn> list1 = new ArrayList<TemplateFieldSoIn>( list.size() );
        for ( TemplateField templateField : list ) {
            list1.add( toFieldSoIn( templateField ) );
        }

        return list1;
    }

    protected TemplateStructSoIn templateStructInfoToTemplateStructSoIn(TemplateStructInfo templateStructInfo) {
        if ( templateStructInfo == null ) {
            return null;
        }

        TemplateStructSoIn templateStructSoIn = new TemplateStructSoIn();

        templateStructSoIn.setGroupName( templateStructInfo.getGroupName() );
        templateStructSoIn.setGroupOrder( templateStructInfo.getGroupOrder() );
        templateStructSoIn.setFields( templateFieldListToTemplateFieldSoInList( templateStructInfo.getFields() ) );

        return templateStructSoIn;
    }

    protected List<TemplateStructSoIn> templateStructInfoListToTemplateStructSoInList(List<TemplateStructInfo> list) {
        if ( list == null ) {
            return null;
        }

        List<TemplateStructSoIn> list1 = new ArrayList<TemplateStructSoIn>( list.size() );
        for ( TemplateStructInfo templateStructInfo : list ) {
            list1.add( templateStructInfoToTemplateStructSoIn( templateStructInfo ) );
        }

        return list1;
    }

    protected RetailComplaintOrderCreateExpandSoIn retailComplaintOrderCreateExpandDTOToRetailComplaintOrderCreateExpandSoIn(RetailComplaintOrderCreateExpandDTO retailComplaintOrderCreateExpandDTO) {
        if ( retailComplaintOrderCreateExpandDTO == null ) {
            return null;
        }

        RetailComplaintOrderCreateExpandSoIn retailComplaintOrderCreateExpandSoIn = new RetailComplaintOrderCreateExpandSoIn();

        retailComplaintOrderCreateExpandSoIn.setCustomerServiceMid( retailComplaintOrderCreateExpandDTO.getCustomerServiceMid() );
        retailComplaintOrderCreateExpandSoIn.setCarNo( retailComplaintOrderCreateExpandDTO.getCarNo() );
        retailComplaintOrderCreateExpandSoIn.setComplaintInfo( templateStructInfoListToTemplateStructSoInList( retailComplaintOrderCreateExpandDTO.getComplaintInfo() ) );
        retailComplaintOrderCreateExpandSoIn.setRelateOrderNo( retailComplaintOrderCreateExpandDTO.getRelateOrderNo() );

        return retailComplaintOrderCreateExpandSoIn;
    }

    protected AttachmentSoIn attachmentToAttachmentSoIn(Attachment attachment) {
        if ( attachment == null ) {
            return null;
        }

        AttachmentSoIn attachmentSoIn = new AttachmentSoIn();

        attachmentSoIn.setId( attachment.getId() );
        attachmentSoIn.setFileName( attachment.getFileName() );
        attachmentSoIn.setUrl( attachment.getUrl() );
        attachmentSoIn.setType( attachment.getType() );

        return attachmentSoIn;
    }

    protected List<AttachmentSoIn> attachmentListToAttachmentSoInList(List<Attachment> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentSoIn> list1 = new ArrayList<AttachmentSoIn>( list.size() );
        for ( Attachment attachment : list ) {
            list1.add( attachmentToAttachmentSoIn( attachment ) );
        }

        return list1;
    }

    protected FieldValueSoIn fieldValueToFieldValueSoIn(FieldValue fieldValue) {
        if ( fieldValue == null ) {
            return null;
        }

        FieldValueSoIn fieldValueSoIn = new FieldValueSoIn();

        fieldValueSoIn.setCode( fieldValue.getCode() );
        fieldValueSoIn.setDesc( fieldValue.getDesc() );
        fieldValueSoIn.setPathId( fieldValue.getPathId() );
        fieldValueSoIn.setPathName( fieldValue.getPathName() );

        return fieldValueSoIn;
    }

    protected List<FieldValueSoIn> fieldValueListToFieldValueSoInList(List<FieldValue> list) {
        if ( list == null ) {
            return null;
        }

        List<FieldValueSoIn> list1 = new ArrayList<FieldValueSoIn>( list.size() );
        for ( FieldValue fieldValue : list ) {
            list1.add( fieldValueToFieldValueSoIn( fieldValue ) );
        }

        return list1;
    }
}
