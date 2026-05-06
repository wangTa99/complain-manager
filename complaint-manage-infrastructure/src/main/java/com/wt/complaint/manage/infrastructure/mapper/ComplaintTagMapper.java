package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.infrastructure.model.ComplaintTagDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_tag(客诉标签�?】的数据库操作Mapper
 * @createDate 2024-12-17 19:42:24
 * @Entity generator.domain.ComplaintTag
 */
@Repository
public interface ComplaintTagMapper {

    List<ComplaintTagDO> selectByComplaintNoList(@Param("complaintNoList") List<String> complaintNoList);

    int insertSelective(ComplaintTagDO complaintTagDO);

    int batchInsert(@Param("list") List<ComplaintTagDO> complaintTagDOList);

    List<ComplaintTagDO> selectTag(@Param("complaintNo") String complaintNo, @Param("tagType") String tagType);

    /**
     * 软删除标�?
     * @param complaintNo 投诉单号
     * @param tagType 标签类型
     * @return 更新行数
     */
    int deleteTag(@Param("complaintNo") String complaintNo, @Param("tagType") String tagType);
}




