package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import com.wt.complaint.manage.infrastructure.model.param.OrderListParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_order(客诉�?】的数据库操作Mapper
 * @createDate 2024-12-17 15:24:34
 * @Entity generator.domain.ComplaintOrder
 */
public interface ComplaintOrderMapper {

    int insertSelective(ComplaintOrderDO orderDO);

    int updateByComplaintNo(ComplaintOrderDO orderDO);

    int batchUpdateByComplaintNo(@Param("list") List<ComplaintOrderDO> list);

    ComplaintOrderDO selectByComplaintNo(String complaintNo);

    List<ComplaintOrderDO> selectPageByParam(@Param("params") ComplaintListSearchGoIn param);

    Integer countByParam(@Param("params") ComplaintListSearchGoIn param);

    List<ComplaintOrderDO> list(OrderListParam listParam);

    List<ComplaintOrderDO> selectComplaintTimeoutList();

    List<ComplaintOrderDO> selectFirstResponseToTimeoutList(@Param("statusList") List<Integer> statusList);

    List<ComplaintOrderDO> selectFinishToTimeoutList();

    List<ComplaintOrderDO> selectUnFinishedToTimeoutList();

}




