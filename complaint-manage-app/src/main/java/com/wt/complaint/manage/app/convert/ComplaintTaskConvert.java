package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.req.task.TimeOutTagTaskReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.task.TimeOutTagTaskSoIn;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComplaintTaskConvert {
    ComplaintTaskConvert INSTANCE = Mappers.getMapper(ComplaintTaskConvert.class);

    TimeOutTagTaskSoIn toSoIn(TimeOutTagTaskReq source);
}
