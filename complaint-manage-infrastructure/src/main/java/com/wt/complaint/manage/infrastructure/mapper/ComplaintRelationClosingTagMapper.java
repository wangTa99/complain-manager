package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.infrastructure.model.ComplaintRelationClosingTagDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_relation_closing_tag(客诉单与结案标签关联�?】的数据库操作Mapper
 * @createDate 2024-12-17 19:39:46
 * @Entity generator.domain.ComplaintRelationClosingTag
 */
@Repository
public interface ComplaintRelationClosingTagMapper {

    List<ComplaintRelationClosingTagDO> selectByComplaintNo(String complaintNo);

    int deleteByComplaintNo(String complaintNo);

    int insertSelective(ComplaintRelationClosingTagDO complaintRelationClosingTagDO);

    int batchInsertSelective(@Param("list") List<ComplaintRelationClosingTagDO> list);
}




