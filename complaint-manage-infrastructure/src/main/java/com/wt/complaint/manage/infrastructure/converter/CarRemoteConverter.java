package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.OwnerInfoItemGoOut;
import com.xiaomi.nr.cis.api.dto.GetDynamicInfoResponse;
import com.xiaomi.nr.cis.api.dto.OwnerInfoItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/23
 */
@Mapper
public interface CarRemoteConverter {

    CarRemoteConverter INSTANCE = Mappers.getMapper(CarRemoteConverter.class);

    GetDynamicInfoResponseGoOut toGoOut(GetDynamicInfoResponse source);

    OwnerInfoItemGoOut toGoOut(OwnerInfoItemDto source);

    List<OwnerInfoItemGoOut> toGoOutList(List<OwnerInfoItemDto> source);
}
