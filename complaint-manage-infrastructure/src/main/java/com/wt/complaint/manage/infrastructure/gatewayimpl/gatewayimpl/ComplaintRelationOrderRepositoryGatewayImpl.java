package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.infrastructure.converter.RelationOrderConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintRelationOrderMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintRelationOrderDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ComplaintRelationOrderRepositoryGatewayImpl implements ComplaintRelationOrderRepositoryGateway {
    @Resource
    ComplaintRelationOrderMapper relationOrderMapper;

    @Override
    public Boolean save(ComplaintRelationOrderGoIn goIn) {
        int i = relationOrderMapper.insertSelective(RelationOrderConverter.INSTANCE.toDO(goIn));
        return i > 0;
    }

    @Override
    public Boolean update(ComplaintRelationOrderGoIn goIn) {
        int i = relationOrderMapper.updateByBizNoSelective(RelationOrderConverter.INSTANCE.toDO(goIn));
        return i >= 0;
    }

    @Override
    public List<ComplaintRelationOrderGoOut> findList(ComplaintRelationOrderListGoIn goIn) {
        List<ComplaintRelationOrderDO> complaintRelationOrderDOS = relationOrderMapper.selectList(RelationOrderConverter.INSTANCE.toListParam(goIn));
        return RelationOrderConverter.INSTANCE.toGoOut(complaintRelationOrderDOS);
    }
}
