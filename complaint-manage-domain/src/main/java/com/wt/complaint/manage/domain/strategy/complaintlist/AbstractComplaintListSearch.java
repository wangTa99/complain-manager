package com.wt.complaint.manage.domain.strategy.complaintlist;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
public abstract class AbstractComplaintListSearch implements ComplaintListStrategy {

    @Resource
    private ComplaintGateway complaintGateway;


    @Override
    public ComplaintListSearchSoOut searchComplaintList(ComplaintListSearchGoIn complaintListSearchGoIn) {
        return complaintGateway.getComplaintOrderList(preHandler(complaintListSearchGoIn));
    }

    protected abstract ComplaintListSearchGoIn preHandler(ComplaintListSearchGoIn complaintListSearchGoIn);

    protected abstract void postHandler(ComplaintListSearchGoIn complaintListSearchGoIn,
                                ComplaintListSearchSoOut complaintListSearchSoOut);
}
