package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderExpandGoIn;
import com.wt.complaint.manage.infrastructure.model.UserComplaintExpandDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/5/27 10:56
 */
@Mapper
public interface UcOrderExpandConverter {
    UcOrderExpandConverter INSTANCE = Mappers.getMapper(UcOrderExpandConverter.class);

    @Mapping(target = "serviceScene", expression = "java(transToSceneStr(goIn.getServiceScene()))")
    UserComplaintExpandDO toDO(UcOrderExpandGoIn goIn);

    default String transToSceneStr(List<String> sceneList) {
        return String.join(",", sceneList);
    }
}
