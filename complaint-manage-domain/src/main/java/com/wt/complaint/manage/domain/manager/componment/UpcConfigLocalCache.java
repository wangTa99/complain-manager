package com.wt.complaint.manage.domain.manager.componment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.complaint.manage.domain.utils.UpcConfigBotHookUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 天工配置 本地缓存
 */
@Component
@Slf4j
@SuppressWarnings({"squid:S2696", "squid:S2245"})
public class UpcConfigLocalCache {

    private static final String UPC_CONFIG_MAP_KEY = "COMPLAINT_UPC_CONFIG_MAP";
    private static final String UPC_CONFIG_MAP_TTL_KEY = "COMPLAINT_UPC_CONFIG_MAP_TTL";
    private static final long DEFAULT_TTL = 0L;
    private static final Type CONFIG_MAP_TYPE = new TypeToken<Map<String, List<String>>>() {
    }.getType();

    // <moduleKey|roleKey, List<UpcConfig>>
    private static volatile ConcurrentHashMap<String, CopyOnWriteArrayList<String>> UPC_CONFIG_MAP;

    // 配置缓存过期时间�?
    private static volatile Long UPC_CONFIG_MAP_TTL;

    @Value("${server.type}")
    private String env;

    @Resource
    private RedisUtil redisCacheUtil;

    @Resource
    private UpcResourceAnalyzer analyzer;

    /** ----------------------- 启动加载 ------------------- **/
    @PostConstruct
    private void localCacheInit() {
        try {
            // 1. 获取 redis 缓存
            Map<String, List<String>> configMap = redisCacheUtil.getCacheThrow(buildRedisKey(UPC_CONFIG_MAP_KEY), CONFIG_MAP_TYPE);
            Long configTtl = redisCacheUtil.getCacheThrow(buildRedisKey(UPC_CONFIG_MAP_TTL_KEY), Long.class);

            // 2. 如果 configMap 不为空，刷新 configMap 缓存
            if (MapUtil.isNotEmpty(configMap)) {
                refreshConfigCache(configMap, normalizeTtl(configTtl), "redis");
            } else {
                log.warn("[LocalCache] init refresh role map empty. env:{}", env);
                UpcConfigBotHookUtil.text("[complaint-manage] 服务启动时，拉取角色 map 时获取到了空配置 ", env);
                UPC_CONFIG_MAP = new ConcurrentHashMap<>();
                UPC_CONFIG_MAP_TTL = normalizeTtl(configTtl);
            }

            log.info("[LocalCache] cache init success. configMap:{}", GsonUtil.toJson(UPC_CONFIG_MAP));
        } catch (Exception e) {
            log.error("[LocalCache] cache init failed. env:{}", env, e);
            UpcConfigBotHookUtil.text("[complaint-manage] 服务启动时，拉取配置失败. error: " + e, env);
            throw e;
        }
    }

    /**
     * ----------------------- 主要方法 -------------------
     **/

    // 重置缓存时间
    public void refreshCacheTtl() {
        redisCacheUtil.setCacheThrow(buildRedisKey(UPC_CONFIG_MAP_TTL_KEY), 0L, 15 * 60 * 60 * 24);
        UPC_CONFIG_MAP_TTL = 0L;
    }


    // 获取 天工 配置 map
    public Map<String, List<String>> getUpcConfigMap() {
        // 1. 如果本地缓存还没过期，优先使用本地缓�?
        long now = System.currentTimeMillis();
        if (isNotExpired(UPC_CONFIG_MAP_TTL, now)) {
            return mapCopy(UPC_CONFIG_MAP);
        }


        // 2. redis 缓存没过期，则刷新本地缓�?返回刷新后的本地缓存
        try {
            Long redisTtl = redisCacheUtil.getCache(buildRedisKey(UPC_CONFIG_MAP_TTL_KEY), Long.class);
            if (isNotExpired(redisTtl, now)) {
                Map<String, List<String>> configMap = redisCacheUtil.getCache(buildRedisKey(UPC_CONFIG_MAP_KEY), CONFIG_MAP_TYPE);
                // 本地缓存超时时间 5 ~ 7 min
                long refreshExpiredTime = now + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(5, 7));
                refreshConfigCache(configMap, refreshExpiredTime, "redis");
                return mapCopy(UPC_CONFIG_MAP);
            }
        } catch (Exception e) {
            log.error("[LocalCache] get config map cache from redis failed. env:{}", env, e);
        }


        // 3. 都过期了则重新请求下游，刷新redis并刷新本地缓存，返回刷新后的本地缓存
        try {
            // 3.1 重新请求
            Map<String, List<String>> rpcRoleMap = analyzer.getUpcConfigByModules(analyzer.getLegalModuleKeys());

            // 3.2 刷新 redis 缓存
            // redis 超时时间 10 ~ 15 min
            long refreshExpiredTime = now + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(10, 15));
            redisCacheUtil.setCacheThrow(buildRedisKey(UPC_CONFIG_MAP_KEY), rpcRoleMap, 15 * 60 * 60 * 24);
            redisCacheUtil.setCacheThrow(buildRedisKey(UPC_CONFIG_MAP_TTL_KEY), refreshExpiredTime, 15 * 60 * 60 * 24);

            // 3.3 刷新本地缓存
            // 本地缓存超时时间 5 ~ 7 min
            long refreshLocalExpiredTime = now + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(5, 7));
            refreshConfigCache(rpcRoleMap, refreshLocalExpiredTime, "rpc");

            return mapCopy(UPC_CONFIG_MAP);
        } catch (Exception e) {
            log.error("[LocalCache] get config map cache from rpc failed. env:{}", env, e);
        }

        // 失败也更新时间：本地缓存超时时间 5 ~ 7 min
        if (MapUtil.isNotEmpty(mapCopy(UPC_CONFIG_MAP))) {
            UPC_CONFIG_MAP_TTL = now + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(5, 7));
        }
        log.error("[LocalCache] not refresh config map local cache , ttl:{}, configMap:{}", UPC_CONFIG_MAP_TTL, GsonUtil.toJson(UPC_CONFIG_MAP));
        UpcConfigBotHookUtil.text("[complaint-manage] 更新缓存失败，config map 延续使用本地缓存", env);
        return mapCopy(UPC_CONFIG_MAP);
    }


    /** ----------------------- 本地缓存更新 ------------------- **/

    private void refreshConfigCache(Map<String, List<String>> configMap, Long ttl, String source) {
        if (MapUtil.isEmpty(configMap)) {
            log.warn("[LocalCache] config map local cache refresh failed, configMap is empty");
            return;
        }
        ConcurrentHashMap<String, CopyOnWriteArrayList<String>> local = new ConcurrentHashMap<>();
        configMap.forEach((key, value) -> local.put(key,
                new CopyOnWriteArrayList<>(CollUtil.isEmpty(value)
                        ? Collections.emptyList()
                        : value)));
        UPC_CONFIG_MAP = local;
        UPC_CONFIG_MAP_TTL = normalizeTtl(ttl);
        log.info("[LocalCache] refresh local cache success config map from {}, ttl:{}, configMap:{}", source, ttl, GsonUtil.toJson(configMap));
    }


    /** ----------------------- 辅助方法 ------------------- **/
    // 如果没过�?
    private boolean isNotExpired(Long ttl, long now) {
        return !(ttl == null || ttl <= now);
    }

    // 初始化生效时�?
    private long normalizeTtl(Long ttl) {
        return ttl == null ? DEFAULT_TTL : ttl;
    }

    // 拼装 redis key
    private String buildRedisKey(String originKey) {
        return originKey + "-" + env;
    }

    // map 拷贝
    private <T> Map<String, List<T>> mapCopy(ConcurrentHashMap<String, CopyOnWriteArrayList<T>> map) {
        Map<String, List<T>> result = new HashMap<>();
        if (MapUtil.isEmpty(map)) {
            return result;
        }

        map.forEach((key, value) -> {
            result.put(key,  CollUtil.isEmpty(value) ? new ArrayList<>() : new ArrayList<>(value));
        });

        return result;
    }
}
