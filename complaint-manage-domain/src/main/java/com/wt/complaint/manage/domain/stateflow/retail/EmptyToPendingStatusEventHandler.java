package com.wt.complaint.manage.domain.stateflow.retail;

import cn.hutool.core.date.StopWatch;
import com.wt.complaint.manage.api.model.enums.DeliverRetailSourceEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.bo.DeliverComplaintExpandBO;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

@Slf4j
@Component
public class EmptyToPendingStatusEventHandler extends BaseRetailUserComplaintStatusHandler
        implements UserComplaintStatusEventHandler<RetailComplaintOrderInfoGoIn, Boolean> {

    @Resource
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway complaintFollowProcessRepositoryGateway;

    @Override
    public UcOrderTypeEnum getUcOrderType() {
        return UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER;
    }

    @Override
    public List<Integer> getSourceList() {
        return Collections.emptyList();
    }

    @Override
    public Integer getTarget() {
        return RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode();
    }

    @Override
    @Transactional
    public Boolean handle(RetailComplaintOrderInfoGoIn goIn) {
        StopWatch stopWatch = new StopWatch();
        // 保存客诉�?
        stopWatch.start("客诉单保�?);
        boolean saveResult = retailComplaintGateway.saveComplaintInfo(
                goIn);
        // 保持客诉单扩展信�?
        DeliverComplaintExpandBO expandBO = new DeliverComplaintExpandBO();
        expandBO.setDrNo(goIn.getDrNo());
        expandBO.setClueId(goIn.getClueId());
        boolean saveExtendResult = deliverComplaintExpandGateway.insertSelective(expandBO) > 0;
        stopWatch.stop();
        // 交付客诉, 保存跟进记录
        boolean saveProcess = true;
        // 只有交付投诉才保持创建跟进记�?
        if (Objects.equals(DeliverRetailSourceEnum.DELIVER.getCode(), goIn.getSource())) {
            // 存跟进记�?
            saveProcess =
                    complaintFollowProcessRepositoryGateway.saveComplaintFollowProcess(goIn.getFollowUpRecord());
        }
        if (saveResult && saveExtendResult && saveProcess) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
