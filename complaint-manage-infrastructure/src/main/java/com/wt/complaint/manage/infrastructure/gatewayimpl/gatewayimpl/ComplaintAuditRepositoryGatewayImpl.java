package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.AuditListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintAuditGoOut;
import com.wt.complaint.manage.infrastructure.converter.AuditConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintAuditMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ComplaintAuditRepositoryGatewayImpl implements ComplaintAuditRepositoryGateway {
    @Resource
    private ComplaintAuditMapper complaintAuditMapper;

    @Override
    public Boolean save(ComplaintAuditGoIn complaintAudit) {
        int i = complaintAuditMapper.insertSelective(AuditConverter.INSTANCE.toDO(complaintAudit));
        return i>0;
    }
}
