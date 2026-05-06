package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RmqGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FinishOrderStatusMqMessageGoIn;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class RmqGatewayImpl implements RmqGateway {

    private final Integer DELAY_LEVEL = 2;
    private final Integer TIME_OUT = 3000;

    @Value("${rocketmq.topic.nr_car_soc_operate_work_order}")
    private String nrMessageOrderStatusFinishTopic;

    @Resource(name = "mqTemplate")
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public boolean mrOrderStatusFinishMessage(FinishOrderStatusMqMessageGoIn goIn) {
        log.info("mrOrderStatusFinishMessage req:{}", JacksonUtil.toStr(goIn));
        //发送消�?
        SendResult sendResult = rocketMQTemplate.syncSend(nrMessageOrderStatusFinishTopic,
            MessageBuilder.withPayload(GsonUtil.toJson(goIn)).setHeader("KEYS", goIn.getWorkNo()).build());
        log.info("send rocketmq data topic: [{}] data={}", nrMessageOrderStatusFinishTopic, sendResult);
        if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
            return true;
        } else {
            log.error("sendFail: {}", sendResult);
            return false;
        }
    }

    @Override
    public boolean mrOrderStatusFinishDelayMessage(FinishOrderStatusMqMessageGoIn goIn) {
        log.info("mrOrderStatusFinishDelayMessage req:{}", JacksonUtil.toStr(goIn));
        //发送消�?
        SendResult sendResult = rocketMQTemplate.syncSend(nrMessageOrderStatusFinishTopic,
            MessageBuilder.withPayload(GsonUtil.toJson(goIn)).setHeader("KEYS", goIn.getWorkNo()).build(), TIME_OUT, DELAY_LEVEL);
        log.info("send rocketmq data topic: [{}] data={}", nrMessageOrderStatusFinishTopic, sendResult);
        if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
            return true;
        } else {
            log.error("sendFail: {}", sendResult);
            return false;
        }
    }

}
