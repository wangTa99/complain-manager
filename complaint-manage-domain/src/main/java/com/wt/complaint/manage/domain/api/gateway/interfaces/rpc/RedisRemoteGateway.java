package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import java.util.concurrent.TimeUnit;

public interface RedisRemoteGateway {

    void set(String key, String value, Long timeout, TimeUnit timeUnit);

    String get(String key);

    boolean lock(String key, Long timeout, TimeUnit timeUnit);

    boolean unLock(String key);
}
