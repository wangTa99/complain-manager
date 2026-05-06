package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.infrastructure.model.UserComplaintExpandDO;
import com.wt.complaint.manage.infrastructure.model.param.UcExpandOrderSearchParam;
import com.wt.complaint.manage.infrastructure.model.param.UserComplaintExpandUpdateParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserComplaintExpandMapper {
    int insertSelective(UserComplaintExpandDO expandDO);

    UserComplaintExpandDO selectById(Long id);

    UserComplaintExpandDO selectByUcNo(@Param("ucNo") String ucNo);

    List<UserComplaintExpandDO> selectByParam(UcExpandOrderSearchParam param);

    int updateById(UserComplaintExpandDO expandDO);

    int updateByParam(@Param("ucNo") String ucNo, @Param("expand")UserComplaintExpandUpdateParam param);

    /**
     * 批量更新跟进客服mid
     */
    int batchUpdateByUcNo(@Param("expandList") List<UserComplaintExpandUpdateParam> param);
}
