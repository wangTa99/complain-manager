package com.wt.complaint.manage.domain.manager.componment;

import com.wt.complaint.manage.domain.utils.RedisUtil;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpcConfigLocalCacheTest {

    @InjectMocks
    private UpcConfigLocalCache localCache;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private UpcResourceAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(localCache, "env", "dev");
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP", new java.util.concurrent.ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP_TTL", 0L);
    }

    @Test
    void refreshCacheTtlShouldResetLocalTtl() {
        localCache.refreshCacheTtl();
        verify(redisUtil).setCacheThrow("COMPLAINT_UPC_CONFIG_MAP_TTL-dev", 0L, 15 * 60 * 60 * 24);
        Object ttl = ReflectionTestUtils.getField(UpcConfigLocalCache.class, "UPC_CONFIG_MAP_TTL");
        Assertions.assertEquals(0L, ttl);
    }

    @Test
    void getUpcConfigMapShouldReturnCopyWhenNotExpired() {
        Map<String, CopyOnWriteArrayList<String>> stored = new java.util.concurrent.ConcurrentHashMap<>();
        stored.put("complaintFrame|role", new CopyOnWriteArrayList<>(Collections.singletonList("tag")));
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP", stored);
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP_TTL", System.currentTimeMillis() + 60_000);

        Map<String, java.util.List<String>> result = localCache.getUpcConfigMap();

        Assertions.assertEquals(1, result.size());
        Assertions.assertNotSame(stored, result);
        verifyNoInteractions(redisUtil);
    }

    @Test
    void getUpcConfigMapShouldRefreshFromRedisWhenNotExpired() {
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP", new java.util.concurrent.ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP_TTL", 0L);

        long futureTtl = System.currentTimeMillis() + 120_000;
        Map<String, java.util.List<String>> redisMap = new HashMap<>();
        redisMap.put("complaintFrame|roleA", Collections.singletonList("tagA"));
        when(redisUtil.getCache("COMPLAINT_UPC_CONFIG_MAP_TTL-dev", Long.class)).thenReturn(futureTtl);
        when(redisUtil.getCache("COMPLAINT_UPC_CONFIG_MAP-dev", (java.lang.reflect.Type) ReflectionTestUtils.getField(UpcConfigLocalCache.class, "CONFIG_MAP_TYPE")))
                .thenReturn(redisMap);

        Map<String, java.util.List<String>> result = localCache.getUpcConfigMap();

        Assertions.assertEquals(redisMap, result);
    }

    @Test
    void getUpcConfigMapShouldRefreshFromRpcWhenRedisExpired() {
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP", new java.util.concurrent.ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
        ReflectionTestUtils.setField(localCache, "UPC_CONFIG_MAP_TTL", 0L);

        when(redisUtil.getCache("COMPLAINT_UPC_CONFIG_MAP_TTL-dev", Long.class)).thenReturn(0L);
        Map<String, java.util.List<String>> rpcMap = new HashMap<>();
        rpcMap.put("complaintFrame|roleB", Collections.singletonList("tagB"));
        when(analyzer.getLegalModuleKeys()).thenReturn(Collections.singletonList("complaintFrame"));
        when(analyzer.getUpcConfigByModules(Collections.singletonList("complaintFrame"))).thenReturn(rpcMap);

        Map<String, java.util.List<String>> result = localCache.getUpcConfigMap();

        Assertions.assertEquals(rpcMap, result);
        verify(redisUtil).setCacheThrow("COMPLAINT_UPC_CONFIG_MAP-dev", rpcMap, 15 * 60 * 60 * 24);
        verify(redisUtil).setCacheThrow(org.mockito.ArgumentMatchers.eq("COMPLAINT_UPC_CONFIG_MAP_TTL-dev"), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.eq(15 * 60 * 60 * 24L));
    }
}
