package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessApplyFinishListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetVidRelationMapResponseBo;
import com.wt.complaint.manage.infrastructure.model.ComplaintFollowProcessDO;
import com.wt.complaint.manage.infrastructure.model.param.ProcessApplyFinishListParam;
import com.wt.complaint.manage.infrastructure.model.param.ProcessListParam;
import com.xiaomi.nr.cis.api.dto.GetVidRelationMapResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProcessConverter {
    ProcessConverter INSTANCE = Mappers.getMapper(ProcessConverter.class);

    GetVidRelationMapResponseBo toGetVidRelationMapResponseBo(GetVidRelationMapResponse response);

    ComplaintFollowProcessDO toDO(ComplaintFollowProcessGoIn source);

    List<ComplaintFollowProcessDO> toDO(List<ComplaintFollowProcessGoIn> source);

    ComplaintFollowProcessGoOut toGoOut(ComplaintFollowProcessDO source);

    List<ComplaintFollowProcessGoOut> toGoOut(List<ComplaintFollowProcessDO> source);

    ProcessListParam toParam(ComplaintProcessListGoIn source);

    ProcessApplyFinishListParam toParam(ComplaintProcessApplyFinishListGoIn complaintProcessApplyFinishListGoIn);
}
