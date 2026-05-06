package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.req.deliver.DeliverComplaintFinishReq;
import com.wt.complaint.manage.api.model.req.deliver.DeliverComplaintReassignReq;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintFinishGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintReassignProcessGoIn;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Mapper
public interface DeliverComplaintAuditConvert {

    DeliverComplaintAuditConvert INSTANCE = Mappers.getMapper(DeliverComplaintAuditConvert.class);

    DeliverComplaintFinishGoIn toFinishGoIn(DeliverComplaintFinishReq source);

    DeliverComplaintReassignProcessGoIn toReassignGoIn(DeliverComplaintReassignReq req);
}
