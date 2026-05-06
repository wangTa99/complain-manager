package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:58+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class AttachmentConvertImpl implements AttachmentConvert {

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
}
