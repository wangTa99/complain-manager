package com.wt.complaint.manage.domain.strategy.pushmessage;

import org.springframework.stereotype.Service;

import java.util.Map;
import javax.annotation.Resource;

@Service
public class ComplaintMessageInformedEventFactory {

    @Resource
    private Map<String, ComplaintMessageInformedStrategy> messageInformedEventMap;

    public ComplaintMessageInformedStrategy getStrategy(String key) {
        return messageInformedEventMap.get(key);
    }
}
