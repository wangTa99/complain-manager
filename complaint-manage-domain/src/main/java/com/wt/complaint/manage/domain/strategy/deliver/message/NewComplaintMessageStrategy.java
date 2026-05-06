package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;

import java.util.Map;

/**
 * 新客诉消息组装策�?
 */
public interface NewComplaintMessageStrategy {
    MessageInformedEvent createMessageInformedEvent(ComplaintBasicInfo complaintBasicInfo, Map<String, String> extraParam);
}
