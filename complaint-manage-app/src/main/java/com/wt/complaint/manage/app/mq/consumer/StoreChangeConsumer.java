package com.wt.complaint.manage.app.mq.consumer;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.app.mq.CarStoreChangeData;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(
    nameServer = "${rocketmq.nameserver}",
    topic = "${car.org.change.topic}",
    consumerGroup = "${car.complaint.manage.consumer}",
    accessKey = "${car.rocketmq.accessKey}",
    secretKey = "${car.rocketmq.secretKey}"
)
public class StoreChangeConsumer implements RocketMQListener<String> {

    @Resource
    ComplaintOrderRepositoryGateway orderRepositoryGateway;

    @Resource
    StoreRemoteGateway storeRemoteGateway;

    @Override
    public void onMessage(String messageInfo) {
        CarStoreChangeData changeData = GsonUtil.fromJson(messageInfo, CarStoreChangeData.class);
        if (Objects.nonNull(changeData) && StringUtils.isNotEmpty(changeData.getOrgId()) && changeData.getChangeModule().contains("orgBase")) {
            // 数据准备
            StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(changeData.getOrgId());
            if (Objects.nonNull(storeInfo)) {
                log.warn("数字门店信息为空：{}", changeData.getOrgId());
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "获取不到对应的门店信�?);
            }
            List<ComplaintOrderInfoGoIn> orderList = orderRepositoryGateway.findList(OrderListGoIn.builder().orgId(changeData.getOrgId()).build());
            // 数据比对
            if (CollUtil.isEmpty(orderList)) {
                log.info("无对应门店的客诉信息，orgId:{}", changeData.getOrgId());
                return;
            }
            ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
            Boolean zoneChanged = compareStoreInfo(storeInfo, complaintOrderInfoGoIn);
            if (zoneChanged) {
                log.info("门店所属区域发生变�?需要批量更新门店数�?);
                List<ComplaintOrderInfoGoIn> complaintOrderInfoGoIns = constructComplaintOrderInfo(storeInfo, orderList);
                Boolean orderChange = orderRepositoryGateway.batchUpdateComplaintInfo(complaintOrderInfoGoIns);
                if (!orderChange) {
                    log.warn("onMessage batchUpdateComplaintInfo fail");
                    throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "修改客诉单门店信息失�?);
                }
            }
        }
    }

    private Boolean compareStoreInfo(StoreInfoGoOut storeInfo, ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        if (!complaintOrderInfoGoIn.getLittleZoneId().equals(storeInfo.getLittleZoneId().toString())
            || !complaintOrderInfoGoIn.getZoneId().equals(storeInfo.getZoneId().toString())
            || !complaintOrderInfoGoIn.getCityId().equals(storeInfo.getCityId())) {
            log.info("onMessage storeInfo:{}", GsonUtil.toJson(storeInfo));
            log.info("onMessage complaintOrderInfoGoIn:{}", GsonUtil.toJson(complaintOrderInfoGoIn));
            return false;
        }
        return true;
    }

    private List<ComplaintOrderInfoGoIn> constructComplaintOrderInfo(StoreInfoGoOut storeInfo, List<ComplaintOrderInfoGoIn> orderList) {
        List<ComplaintOrderInfoGoIn> tempList = new ArrayList<>();
        orderList.stream().forEach(order -> tempList.add(ComplaintOrderInfoGoIn.builder()
            .complaintNo(order.getComplaintNo())
            .zoneId(storeInfo.getZoneId().toString())
            .littleZoneId(storeInfo.getLittleZoneId().toString())
            .cityId(storeInfo.getCityId())
            .build()));
        return tempList;
    }
}
