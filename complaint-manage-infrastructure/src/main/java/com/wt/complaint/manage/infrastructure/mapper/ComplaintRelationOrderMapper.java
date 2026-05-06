package com.wt.complaint.manage.infrastructure.mapper;

import com.wt.complaint.manage.infrastructure.model.ComplaintRelationOrderDO;
import com.wt.complaint.manage.infrastructure.model.param.RelationOrderListParam;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cwk
 * @description 针对表【complaint_relation_order(客诉单关联单据表)】的数据库操作Mapper
 * @createDate 2024-12-17 19:41:09
 * @Entity generator.domain.ComplaintRelationOrder
 */
@Repository
public interface ComplaintRelationOrderMapper {
    int insertSelective(ComplaintRelationOrderDO orderDO);
    List<ComplaintRelationOrderDO> selectList(RelationOrderListParam param);
    int updateByBizNoSelective(ComplaintRelationOrderDO orderDO);
}




