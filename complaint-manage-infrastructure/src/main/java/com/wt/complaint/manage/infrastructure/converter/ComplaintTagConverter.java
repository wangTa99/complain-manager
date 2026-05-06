package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;
import com.wt.complaint.manage.infrastructure.model.ComplaintTagDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/25
 */
@Mapper
public interface ComplaintTagConverter {

    ComplaintTagConverter INSTANCE = Mappers.getMapper(ComplaintTagConverter.class);

    ComplaintTagDO toDo(ComplaintTagSoIn source);

    List<ComplaintTagDO> toDo(List<ComplaintTagSoIn> source);

    ComplaintTagGoOut toGoOut(ComplaintTagDO source);

    List<ComplaintTagGoOut> toGoOut(List<ComplaintTagDO> source);
}
