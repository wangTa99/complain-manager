package com.wt.complaint.manage.domain.strategy.consult.message;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.CreateConsultOrderSoOut;
import com.wt.complaint.manage.domain.event.MessageInformedEvent;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;

import java.util.Map;

public interface ConsultMessageInformedStrategy {
    MessageInformedEvent createMessageInformedEvent(UserConsultOrderInfo soOut, Map<String, String> extraParam);
}
