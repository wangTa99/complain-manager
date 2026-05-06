package com.wt.complaint.manage.domain.api.gateway.interfaces;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;

import java.util.List;

public interface ComplaintOrderRepositoryGateway {
    Boolean saveComplaintInfo(ComplaintOrderInfoGoIn infoGoIn);

    Boolean updateComplaintInfo(ComplaintOrderInfoGoIn infoGoIn);

    Boolean batchUpdateComplaintInfo(List<ComplaintOrderInfoGoIn> listGoIn);

    List<ComplaintOrderInfoGoIn> findList(OrderListGoIn listGoIn);
}
