package com.wt.complaint.manage.domain.strategy.message;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;

import java.util.Map;

public interface MessageInformedStrategy {
    MessageInformedEvent createMessageInformedEvent(ComplaintOrderGoOut complaintOrder, Map<String, String> extraParam);
}
