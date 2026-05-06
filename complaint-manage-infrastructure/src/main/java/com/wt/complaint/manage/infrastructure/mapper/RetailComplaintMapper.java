package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintDO;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintDetailDO;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintListSearchDetailDO;
import com.wt.complaint.manage.infrastructure.model.RetailHasFirstResposeRecordFlagDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 零售客诉
 *
 * @author p-wangkai95
 * @version 1.0
 */
public interface RetailComplaintMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RetailComplaintDO row);

    int insertSelective(RetailComplaintDO row);

    RetailComplaintDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RetailComplaintDO row);

    int updateByPrimaryKeyWithBLOBs(RetailComplaintDO row);

    int updateByPrimaryKey(RetailComplaintDO row);

    int staticRetailCount(@Param("params") StaticRetailCountGoIn goIn);

    RetailComplaintDetailDO getRetailComplaintDetail(@Param("params") RetailComplaintDetailGoIn soIn);

    RetailHasFirstResposeRecordFlagDO getRetailHasFirstResposeRecordFlag(
            @Param("params") RetailHasFirstResponseRecordFlagGoIn goIn);

    List<RetailComplaintListSearchDetailDO> searchRetailComplaintList(@Param("params")
                                                                      RetailComplaintListSearchGoIn retailComplaintListSearchGoIn);

    int updateOrderByDrNo(@Param("params") UpdateRetailOrderGoIn updateOrderStatusGoIn);

    RetailComplaintDetailDO findByIdempotentKey(@Param("params") FindByIdempotentIdGoIn findByIdempotentIdGoIn);

    List<RetailComplaintDO> selectFirstResponseToTimeoutList();

    List<RetailComplaintDO> selectFinishToTimeoutList();

}
