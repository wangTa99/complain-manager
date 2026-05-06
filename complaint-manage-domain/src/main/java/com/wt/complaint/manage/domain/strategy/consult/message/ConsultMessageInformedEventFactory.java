package com.wt.complaint.manage.domain.strategy.consult.message;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class ConsultMessageInformedEventFactory {

    @Resource
    private Map<String, ConsultMessageInformedStrategy> messageInformedEventMap;

    public ConsultMessageInformedStrategy getStrategy(String key) {
        return messageInformedEventMap.get(key);
    }
}
