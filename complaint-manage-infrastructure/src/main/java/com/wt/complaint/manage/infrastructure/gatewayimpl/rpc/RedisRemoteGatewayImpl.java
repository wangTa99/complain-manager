package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisRemoteGatewayImpl implements RedisRemoteGateway {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean lock(String key, Long timeout, TimeUnit timeUnit) {
        try {
            String value = key + System.currentTimeMillis();
            Boolean bool = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
            return bool != null && bool;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean unLock(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
