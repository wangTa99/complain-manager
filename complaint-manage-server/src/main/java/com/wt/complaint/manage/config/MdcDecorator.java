package com.wt.complaint.manage.config;

import com.google.common.collect.Maps;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

@SuppressWarnings("squid:S3740")
public class MdcDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map contextMap = MDC.getCopyOfContextMap();
        RpcContext rpcContext = RpcContext.getContext();
        Map<String, Object> attachments = rpcContext.getObjectAttachments();
        Map<String, Object> map = Maps.newHashMap();
        map.putAll(attachments);
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                if (rpcContext != null) {
                    RpcContext.getContext().setObjectAttachments(map);
                }
                runnable.run();
            } finally {
                MDC.clear();
                RpcContext.removeContext();
            }
        };
    }

}
