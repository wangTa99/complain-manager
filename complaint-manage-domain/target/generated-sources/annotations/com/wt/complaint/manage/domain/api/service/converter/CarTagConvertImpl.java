package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.api.model.resp.LabelDTO.LabelDTOBuilder;
import com.wt.complaint.manage.api.model.resp.LabelDTO.TagInfo;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarTagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarTagGoOut.TagInfoGoOut;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-31T21:19:58+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_462 (Amazon.com Inc.)"
)
public class CarTagConvertImpl implements CarTagConvert {

    @Override
    public LabelDTO toCarTag(CarTagGoOut source) {
        if ( source == null ) {
            return null;
        }

        LabelDTOBuilder labelDTO = LabelDTO.builder();

        labelDTO.tagType( source.getTagType() );
        labelDTO.tagList( tagInfoGoOutListToTagInfoList( source.getTagList() ) );

        return labelDTO.build();
    }

    @Override
    public TagInfo toCarTagInfo(TagInfoGoOut source) {
        if ( source == null ) {
            return null;
        }

        TagInfo tagInfo = new TagInfo();

        tagInfo.setTagCode( source.getTagCode() );
        tagInfo.setTagName( source.getTagName() );

        return tagInfo;
    }

    protected List<TagInfo> tagInfoGoOutListToTagInfoList(List<TagInfoGoOut> list) {
        if ( list == null ) {
            return null;
        }

        List<TagInfo> list1 = new ArrayList<TagInfo>( list.size() );
        for ( TagInfoGoOut tagInfoGoOut : list ) {
            list1.add( toCarTagInfo( tagInfoGoOut ) );
        }

        return list1;
    }
}
