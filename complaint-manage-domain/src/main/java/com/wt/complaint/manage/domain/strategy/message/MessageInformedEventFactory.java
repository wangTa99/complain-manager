package com.wt.complaint.manage.domain.strategy.message;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class MessageInformedEventFactory {

    @Resource
    private Map<String, MessageInformedStrategy> messageInformedEventMap;

    public MessageInformedStrategy getStrategy(String key) {
        return messageInformedEventMap.get(key);
    }
}
