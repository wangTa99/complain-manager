package com.wt.complaint.manage.app.mq.consumer;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.RelationOrderEnum;
import com.wt.complaint.manage.app.mq.MrsOrderStatusChangeData;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintRelationOrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(
    nameServer = "${rocketmq.nameserver}",
    topic = "${car.order.status.change.topic}",
    consumerGroup = "${car.complaint.manage.consumer}",
    accessKey = "${car.rocketmq.accessKey}",
    secretKey = "${car.rocketmq.secretKey}"
)
public class OrderStatusConsumer implements RocketMQListener<String> {
    @Resource
    ComplaintRelationOrderRepositoryGateway relationOrderRepositoryGateway;

    @Resource
    ComplaintFollowProcessRepositoryGateway followProcessRepositoryGateway;

    @Override
    public void onMessage(String messageInfo) {
        try {
            log.info("OrderStatusConsumer received data:{}", GsonUtil.toJson(messageInfo));
            MrsOrderStatusChangeData changeData = GsonUtil.fromJson(messageInfo, MrsOrderStatusChangeData.class);
            if (CollUtils.isNotEmpty(changeData.getExt())) {
                if (StringUtils.isNotEmpty((String) changeData.getExt().get("complaintNo"))) {
                    String complaintNo = (String) changeData.getExt().get("complaintNo");
                    List<ComplaintRelationOrderGoOut> list =
                            relationOrderRepositoryGateway.findList(ComplaintRelationOrderListGoIn.builder().complaintNoList(CollUtil.toList(complaintNo)).build());
                    List<ComplaintRelationOrderGoOut> relationList = Optional.ofNullable(list).orElse(new LinkedList<>()).stream().filter(e -> e.getBizNo().equals(changeData.getMrNo())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(relationList)) {
                        log.warn("OrderStatusConsumer relationOrderRepositoryGateway findList error:{}", RetailJsonUtil.toJson(relationList));
                        return;
                    }
                    ComplaintRelationOrderGoIn build = ComplaintRelationOrderGoIn.builder()
                        .complaintNo(complaintNo)
                        .bizNo(changeData.getMrNo())
                        .bizType(RelationOrderEnum.MR_NO.getCode())
                        .build();
                    Boolean orderSave = relationOrderRepositoryGateway.save(build);
                    // 构建跟进记录
                    String appointId = (String) changeData.getExt().get("appointId");
                    RecordInfoGoIn recordInfoGoIn = RecordInfoGoIn.builder()
                        .mrNo(changeData.getMrNo())
                        .build();
                    ComplaintFollowProcessGoIn followUpRecord = ComplaintFollowProcessGoIn.builder()
                        .complaintNo(complaintNo)
                        .processType("".equals(appointId) ? ProcessTypeEnum.TO_STORE_MAINTENANCE.getProcessCode() : ProcessTypeEnum.APPOINT_TO_STORE_MAINTENANCE.getProcessCode())
                        .processContent(GsonUtil.toJson(recordInfoGoIn))
                        .build();
                    Boolean recordSave = followProcessRepositoryGateway.saveComplaintFollowProcess(followUpRecord);
                    if (!orderSave || !recordSave) {
                        log.warn("OrderStatusConsumer save error:{}", RetailJsonUtil.toJson(build));
                        throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "持久化关系异�?);
                    }
                }
            }
        } catch (Exception e) {
            log.error("OrderStatusConsumer error", e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "客诉维保绑定关系异常");
        }
    }
}
