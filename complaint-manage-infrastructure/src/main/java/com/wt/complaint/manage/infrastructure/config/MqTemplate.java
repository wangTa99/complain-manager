package com.wt.complaint.manage.infrastructure.config;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service("mqTemplate")
@ExtRocketMQTemplateConfiguration(
    nameServer = "${rocketmq.nameserver}",
    group = "${rocketmq.producer.group}",
    accessKey = "${car.rocketmq.accessKey}",
    secretKey = "${car.rocketmq.secretKey}"
)
public class MqTemplate extends RocketMQTemplate {

}