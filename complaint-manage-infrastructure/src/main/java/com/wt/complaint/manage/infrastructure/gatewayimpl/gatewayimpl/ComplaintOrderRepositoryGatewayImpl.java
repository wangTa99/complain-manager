package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.infrastructure.converter.OrderConverter;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintOrderMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ComplaintOrderRepositoryGatewayImpl implements ComplaintOrderRepositoryGateway {
    @Resource
    private ComplaintOrderMapper complaintOrderMapper;

    @Override
    public Boolean saveComplaintInfo(ComplaintOrderInfoGoIn infoGoIn) {
        int i = complaintOrderMapper.insertSelective(OrderConverter.INSTANCE.toOrderDO(infoGoIn));
        return i > 0;
    }

    @Override
    public Boolean updateComplaintInfo(ComplaintOrderInfoGoIn infoGoIn) {
        log.info("ComplaintOrderRepositoryGatewayImpl#updateComplaintInfo, infoGoIn:{}",
                RetailJsonUtil.toJson(infoGoIn));
        int i = complaintOrderMapper.updateByComplaintNo(OrderConverter.INSTANCE.toOrderDO(infoGoIn));
        return i>0;
    }

    @Override
    public Boolean batchUpdateComplaintInfo(List<ComplaintOrderInfoGoIn> infoGoIn) {
        int i = complaintOrderMapper.batchUpdateByComplaintNo(OrderConverter.INSTANCE.toOrderDO(infoGoIn));
        return i==infoGoIn.size();
    }

    @Override
    public List<ComplaintOrderInfoGoIn> findList(OrderListGoIn listGoIn) {
        List<ComplaintOrderDO> list = complaintOrderMapper.list(OrderConverter.INSTANCE.toOrderListParam(listGoIn));
        List<ComplaintOrderInfoGoIn> orderInfoGoIn = OrderConverter.INSTANCE.toOrderInfoGoIn(list);
        return orderInfoGoIn;
    }
}
