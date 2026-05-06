package com.wt.complaint.manage.domain.statemachine.interfaceImpl.report;

import com.wt.complaint.manage.api.model.enums.UcOrderEventEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.statemachine.UcOrderContext;
import org.springframework.stereotype.Component;

/**
 * @author linjiehong
 * @date 2025/5/22 14:12
 */
@Component
public class CreateEventHandler extends AbstractHandler {
    /**
     * 构建跟进记录表内�?
     * @param context 信息
     * @return
     */
    @Override
    protected ComplaintFollowProcessGoIn buildRecordInfoGoIn(UcOrderEventEnum event, UcOrderContext context) {
        return null;
    }
}
