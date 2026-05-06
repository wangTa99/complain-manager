package com.wt.complaint.manage.domain.strategy.pushmessage;

import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;

import java.util.Map;

public interface ComplaintMessageInformedStrategy {
    MessageInformedEvent createMessageInformedEvent(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut,
                                                    Map<String, String> extraParam);
}
