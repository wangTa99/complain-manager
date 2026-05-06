package com.wt.complaint.manage.infrastructure.converter;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.RetailComplaintCreateBPMGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintRelationClosingTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.SubmitForApprovalSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintRelationClosingTagSoOut;
import com.wt.complaint.manage.infrastructure.model.ComplaintAuditDO;
import com.wt.complaint.manage.infrastructure.model.ComplaintRelationClosingTagDO;
import com.xiaomi.newretail.bpm.api.model.dto.ProcessCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Mapper
public interface ComplaintAuditConverter {
    ComplaintAuditConverter INSTANCE = Mappers.getMapper(ComplaintAuditConverter.class);

    ComplaintRelationClosingTagDO toTagDO(ComplaintRelationClosingTagSoIn source);

    List<ComplaintRelationClosingTagDO> toTagDO(List<ComplaintRelationClosingTagSoIn> source);

    @Mapping(source = "targetOrgId", target = "orgId")
    @Mapping(source = "targetOrgName", target = "orgName")
    @Mapping(source = "targetZoneId", target = "zoneId")
    @Mapping(source = "targetLittleZoneId", target = "littleZoneId")
    ComplaintAuditDO toDO(SubmitForApprovalSoIn source);

    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updateTime", target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ComplaintAuditSoOut toSoOut(ComplaintAuditDO source);

    List<ComplaintAuditSoOut> toSoOutList(List<ComplaintAuditDO> source);

    ComplaintRelationClosingTagSoOut toSoOut(ComplaintRelationClosingTagDO source);

    List<ComplaintRelationClosingTagSoOut> toClosingTagList(List<ComplaintRelationClosingTagDO> source);

    ProcessCreateDTO toCreateDTO(RetailComplaintCreateBPMGoIn source);

}
