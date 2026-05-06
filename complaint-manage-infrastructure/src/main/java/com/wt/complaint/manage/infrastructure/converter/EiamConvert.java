package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetZoneEmployeeGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserBaseInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.xiaomi.nr.eiam.api.vo.user.GetUserInfoResponse;
import com.xiaomi.nr.eiam.car.api.dto.GetZoneEmployeeResponse;
import com.xiaomi.nr.eiam.car.api.dto.employee.GetZonePositionUserResponse;
import com.xiaomi.nr.eiam.car.api.vo.position.ListByPositionIdAndStateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EiamConvert {
    EiamConvert INSTANCE = Mappers.getMapper(EiamConvert.class);

    EmployeeInfoGoOut toBo(GetUserInfoResponse source);

    List<EmployeeInfoGoOut> toBoList(List<GetUserInfoResponse> source);

    @Mapping(target = "mid", source = "miId")
    ZonePositionUserGoOut toGoOut(GetZonePositionUserResponse source);

    List<ZonePositionUserGoOut> toGoOutList(List<GetZonePositionUserResponse> source);

    UserBaseInfoGoOut toGoOut(ListByPositionIdAndStateResponse.UserBaseInfo source);

    List<UserBaseInfoGoOut> toUserGoOutList(List<ListByPositionIdAndStateResponse.UserBaseInfo> source);

    List<GetZoneEmployeeGoOut> toZoneEmployeeGoOut(List<GetZoneEmployeeResponse> source);
}
