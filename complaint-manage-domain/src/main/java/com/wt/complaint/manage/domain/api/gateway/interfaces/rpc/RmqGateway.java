package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.FinishOrderStatusMqMessageGoIn;

public interface RmqGateway {
    /**
     * 作业单完成消息通知
     *
     * @param messageGoIn
     * @return
     */
    boolean mrOrderStatusFinishMessage(FinishOrderStatusMqMessageGoIn messageGoIn);

    /**
     * 作业单完成消息延迟发�?
     * @param messageGoIn
     * @return
     */
    boolean mrOrderStatusFinishDelayMessage(FinishOrderStatusMqMessageGoIn messageGoIn);

}
