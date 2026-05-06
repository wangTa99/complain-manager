package com.wt.complaint.manage.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintExpandService;
import com.wt.complaint.manage.domain.bo.DeliverComplaintExpandBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliverComplaintExpandServiceImpl implements DeliverComplaintExpandService {

    @Autowired
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    @Override
    @Transactional
    public int updateBatchSelective(List<DeliverComplaintExpandBO> list) {
        // 将列表按200条分割成多个批次
        List<List<DeliverComplaintExpandBO>> batches = Lists.partition(list, 200);

        // 分批调用更新接口
        for (List<DeliverComplaintExpandBO> batch : batches) {
            deliverComplaintExpandGateway.updateBatchSelective(new ArrayList<>(batch));
        }
        return list.size();

    }
}
