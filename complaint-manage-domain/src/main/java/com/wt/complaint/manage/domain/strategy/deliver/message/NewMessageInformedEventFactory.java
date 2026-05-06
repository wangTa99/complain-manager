package com.wt.complaint.manage.domain.strategy.deliver.message;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class NewMessageInformedEventFactory {

    @Resource
    private Map<String, NewComplaintMessageStrategy> newComplaintMessageStrategyMap;

    public NewComplaintMessageStrategy getStrategy(String key) {
        return newComplaintMessageStrategyMap.get(key);
    }
}
