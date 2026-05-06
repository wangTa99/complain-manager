package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.infrastructure.model.UserComplaintOrderDO;
import com.wt.complaint.manage.infrastructure.model.UserComplaintOrderDetailDO;

import com.wt.complaint.manage.infrastructure.model.param.UserComplaintOrderSearchParam;
import com.wt.complaint.manage.infrastructure.model.param.UserComplaintOrderUpdateParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserComplaintOrderMapper {
    int insertSelective(UserComplaintOrderDO orderDO);

    UserComplaintOrderDO selectById(Long id);

    UserComplaintOrderDO selectByUcNo(@Param("ucNo") String ucNo);

    UserComplaintOrderDO selectByIdempotentKey(@Param("idempotentKey") String idempotentKey);

    List<UserComplaintOrderDO> selectByParam(UserComplaintOrderSearchParam param);

    int updateById(UserComplaintOrderDO orderDO);

    /**
     * 根据参数更新客诉单信�?
     *
     * @param ucNo 客诉单号
     * @param param 更新参数
     * @return 更新行数
     */
    int updateByParam(@Param("ucNo") String ucNo, @Param("param") UserComplaintOrderUpdateParam param);

    /**
     * 分页查询举报单信�?
     *
     * @param goIn 查询参数
     * @return 举报单信息列�?
     */
    List<UserComplaintOrderDetailDO> selectPageByParam(@Param("params") UserComplaintListSearchGoIn goIn);

    /**
     * 通过举报单号查询举报详情
     *
     * @param ucNo 举报单号
     * @return 举报详情
     */
    UserComplaintOrderDetailSoOut selectDetailByUcNo(@Param("ucNo") String ucNo);
}