package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintAuditListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.ComplaintPreNextSoIn;
import com.wt.complaint.manage.infrastructure.model.ComplaintAuditDO;
import com.wt.complaint.manage.infrastructure.model.param.AuditListParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_audit(客诉审批�?】的数据库操作Mapper
 * @createDate 2024-12-17 19:37:40
 * @Entity generator.domain.ComplaintAudit
 */
@Repository
public interface ComplaintAuditMapper {
    int insertSelective(ComplaintAuditDO auditDO);

    List<ComplaintAuditDO> list(AuditListParam param);

    List<ComplaintAuditDO> selectPageByParam(ComplaintAuditListSoIn param);

    List<Long> selectPreAndAfter(ComplaintPreNextSoIn param);

    ComplaintAuditDO selectById(Long id);

    int updateById(ComplaintAuditDO auditDO);

    ComplaintAuditDO selectByComplaintNo(@Param("complaintNo") String complaintNo, @Param("auditType") Integer auditType);

}




