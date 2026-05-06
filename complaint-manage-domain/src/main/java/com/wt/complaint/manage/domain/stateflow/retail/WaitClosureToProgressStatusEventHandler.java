package com.wt.complaint.manage.domain.stateflow.retail;

import com.alibaba.fastjson.JSON;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UpdateRetailOrderGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailApplyRetailCallBackSoIn;
import com.wt.complaint.manage.domain.stateflow.UserComplaintStatusEventHandler;
import com.xiaomi.newretail.bpm.api.model.callback.ProcessAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class WaitClosureToProgressStatusEventHandler extends BaseRetailUserComplaintStatusHandler implements UserComplaintStatusEventHandler<RetailApplyRetailCallBackSoIn, Void> {

    @Resource
    RetailComplaintGateway retailComplaintGateway;

    @Resource
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Override
    public UcOrderTypeEnum getUcOrderType() {
        return UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER;
    }

    @Override
    public List<Integer> getSourceList() {
        return Collections.singletonList(RetailComplaintOrderStatusEnum.APPLICATION_FOR_CLOSURE.getCode());
    }

    @Override
    public Integer getTarget() {
        return RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode();
    }

    @Override
    @Transactional
    @SuppressWarnings("squid:S3599")
    public Void handle(RetailApplyRetailCallBackSoIn param) {
        // 更新主表
        UpdateRetailOrderGoIn orderGoIn = UpdateRetailOrderGoIn.builder()
                                                               .drNo(param.getDrNo())
                                                               .orderStatus(getTarget())
                                                               .build();
        retailComplaintGateway.updateOrderByDrNo(orderGoIn);

        // 临时逻辑：目�?bpm 审批撤回临时复用了驳回流程， 后续可能会迭代掉此处逻辑
        Boolean isCancel = ProcessAction.Cancel == param.getAction();

        // 更新记录�?
        followProcessGateway.saveComplaintFollowProcess(ComplaintFollowProcessGoIn.builder()
                                                                                  .complaintNo(param.getDrNo())
                                                                                  .processType(ProcessTypeEnum.AUDIT_FINISH_REJECT.getProcessCode())
                                                                                  .processContent(JSON.toJSONString(new HashMap<String, Object>() {{
                                                                                      put("processInstanceId", param.getProcessInstanceId()); // bpmId
                                                                                      put("taskNo", param.getTaskNo()); // 任务ID
                                                                                      put("operator", param.getOperator()); // 判责�?
                                                                                      put("action", param.getAction()); // 动作
                                                                                      put("auditReason", isCancel ? "主动撤回" : param.getRefuseReason()); // 拒绝原因
                                                                                      put("finished", param.getRefuseReason()); // bpm 结束动作
                                                                                      put("extra", param.getRefuseReason()); // bpm 拓展字段
                                                                                      put("orderStatus", param.getOrderStatus()); // 单据状�?
                                                                                      put("drNo", param.getDrNo()); // 客诉单据�?
                                                                                      put("auditMid", param.getAuditMid().toString()); // 判责�?Mid
                                                                                      put("auditName", param.getAuditName()); // 判责人姓�?
                                                                                      put("auditTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                                                                      put("auditResult", isCancel ? "主动撤回" : "审核失败");
                                                                                  }}))
                                                                                  .build());
        return null;
    }
}
