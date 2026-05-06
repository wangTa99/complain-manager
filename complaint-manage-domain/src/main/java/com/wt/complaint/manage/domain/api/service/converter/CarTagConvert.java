package com.wt.complaint.manage.domain.api.service.converter;

import com.wt.complaint.manage.api.model.resp.LabelDTO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarTagGoOut;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarTagConvert {
    CarTagConvert INSTANCE = Mappers.getMapper(CarTagConvert.class);

    LabelDTO toCarTag(CarTagGoOut source);

    LabelDTO.TagInfo toCarTagInfo(CarTagGoOut.TagInfoGoOut source);
}
