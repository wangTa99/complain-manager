package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintProcessLastGoIn;
import com.wt.complaint.manage.infrastructure.model.ComplaintFollowProcessDO;
import com.wt.complaint.manage.infrastructure.model.param.ProcessApplyFinishListParam;
import com.wt.complaint.manage.infrastructure.model.param.ProcessListParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_follow_process(客诉单跟进记�?】的数据库操作Mapper
 * @createDate 2024-12-17 17:05:41
 * @Entity generator.domain.ComplaintFollowProcess
 */
@Repository
public interface ComplaintFollowProcessMapper {
    int insertSelective(ComplaintFollowProcessDO followProcessDO);

    List<ComplaintFollowProcessDO> selectByComplaintNo(String complaintNo);

    List<ComplaintFollowProcessDO> selectByProcessInstanceId(String processInstanceId);

    List<ComplaintFollowProcessDO> selectListByParam(ProcessListParam param);

    List<ComplaintFollowProcessDO> selectLastApplyFinishRecordByParam(ProcessApplyFinishListParam processApplyFinishListParam);

    /**
     * 批量查询最新一次提交复盘跟进记录（process_type=SUBMIT_REVIEW，按 complaint_no �?id 最大的一条）
     */
    List<ComplaintFollowProcessDO> selectLastSubmitReviewRecordByParam(ProcessApplyFinishListParam param);

    /**
     * 查询最后一条跟进记�?
     */
    List<ComplaintFollowProcessDO> selectLastProcessByParam(ComplaintProcessLastGoIn goIn);

    /**
     * 查询需要修改岗位名的交付客诉单
     * @return 操作记录列表
     */
    List<ComplaintFollowProcessDO> selectNeedFixDeliverProcessList();

    /**
     * 批量更新跟进记录的processContent字段，基于id
     *
     * @param updateList 批量更新参数列表
     * @return 更新的记录数
     */
    int batchUpdateProcessContentById(@Param("updateList") List<ComplaintFollowProcessDO> updateList);

}




