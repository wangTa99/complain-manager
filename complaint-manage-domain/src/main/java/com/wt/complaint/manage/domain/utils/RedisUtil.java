package com.wt.complaint.manage.domain.utils;

import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@SuppressWarnings("squid:S2696")
public class RedisUtil {

    /**
     * ---------------------- 常量部分 --------------------
     **/
    private static final String CREATE_LOCK_KEY = "CREATE_LOCK_KEY";
    private static final String REMIND_ORDER = "REMIND_ORDER";
    private static final Object UNDER_LINE = "_";

    public static String generateCreateLockKey(String idempotentId) {
        return CREATE_LOCK_KEY + UNDER_LINE + idempotentId;
    }

    public static String generateRemindKey(String complaintId) {
        return REMIND_ORDER + UNDER_LINE + complaintId;
    }

    /**
     * ---------------------- Redission --------------------
     **/
    static RedissonClient redissonClient;

    public static final Integer TTL = 6;

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        RedisUtil.redissonClient = redissonClient;
    }

    /**
     *  分布式锁: 不等待，超时时间 6 �?
     *  当前使用�?REDIS 集群是老集群，但是非阻塞情况下不会使用 SUBSCRIBE 命令
     */
    public static Boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(0, TTL, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取锁失�?);
        }
    }

    public static void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
    /** ------------------------------------------------------- **/
    /**
     * 写入缓存
     */
    public Boolean setCacheThrow(String key, Object value, long ttl) {
        String json = GsonUtil.toJson(value);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(json, ttl, TimeUnit.SECONDS);
        return Boolean.TRUE;
    }

    /**
     * 读取缓存
     */
    public <T> T getCacheThrow(String key, Class<T> type) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String json = bucket.get();
        return GsonUtil.fromJson(bucket.get(), type);
    }

    /**
     * 读取缓存
     */
    public <T> T getCacheThrow(String key, Type type) {
            RBucket<String> bucket = redissonClient.getBucket(key);
            return GsonUtil.fromJson(bucket.get(), type);
    }

    /**
     * 写入缓存（ttl 单位：秒，对�?SetParams().ex(ttl)�?
     */
    public Boolean setCache(String key, Object value, long ttl) {
        String json = GsonUtil.toJson(value);
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            // 等价：SET key value EX ttl
            bucket.set(json, ttl, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheUtil#setCache error key:{}, value:{}, ttl:{}", key, json, ttl, e);
            return false;
        }
    }

    /**
     * 读取缓存（Class 版本�?
     */
    public <T> T getCache(String key, Class<T> type) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        RFuture<String> future = bucket.getAsync();
        try {
            // Redisson 自带异步 + 100ms 超时控制
            String json = future.get(100, TimeUnit.MILLISECONDS);
            if (json == null) {
                return null;
            }
            return GsonUtil.fromJson(json, type);
        } catch (InterruptedException e) {
            // 和你原来的逻辑一样，恢复中断标记
            Thread.currentThread().interrupt();
            log.warn("RedisCacheUtil#getCache interrupted key:{}", key, e);
            return null;
        } catch (TimeoutException e) {
            log.warn("RedisCacheUtil#getCache timeOut key:{}", key, e);
            return null;
        } catch (Exception e) {
            log.error("RedisCacheUtil#getCache error key:{}", key, e);
            return null;
        }
    }

    /**
     * 读取缓存（Type 版本，支�?List<Foo> 等复杂泛型）
     */
    public <T> T getCache(String key, Type type) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        RFuture<String> future = bucket.getAsync();
        try {
            String json = future.get(100, TimeUnit.MILLISECONDS);
            if (json == null) {
                return null;
            }
            return GsonUtil.fromJson(json, type);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("RedisCacheUtil#getCache interrupted key:{}", key, e);
            return null;
        } catch (TimeoutException e) {
            log.warn("RedisCacheUtil#getCache timeOut key:{}", key, e);
            return null;
        } catch (Exception e) {
            log.error("RedisCacheUtil#getCache error key:{}", key, e);
            return null;
        }
    }

}
