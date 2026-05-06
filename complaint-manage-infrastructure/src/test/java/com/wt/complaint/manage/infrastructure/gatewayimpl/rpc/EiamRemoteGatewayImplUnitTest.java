package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetEmployeeInfoParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoResult;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.xiaomi.nr.eiam.car.api.dto.employee.EmployeeInfoResponse;
import com.xiaomi.nr.eiam.car.api.service.CarEmployeeService;
import com.xiaomi.nr.eiam.car.api.service.CarPositionService;
import com.xiaomi.youpin.infra.rpc.Result;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EiamRemoteGatewayImplUnitTest {

    @InjectMocks
    private EiamRemoteGatewayImpl gateway;

    @Mock
    private CarPositionService carPositionService;

    @Mock
    private CarEmployeeService carEmployeeService;

    @Mock
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gateway, "env", "dev");
    }

    @Test
    void getCarPositionRefShouldUseCacheWhenPresent() {
        Map<Integer, List<String>> cached = new HashMap<>();
        cached.put(1, Collections.singletonList("roleA"));
        when(redisUtil.getCache(anyString(), any(java.lang.reflect.Type.class))).thenReturn(cached);

        Map<Integer, List<String>> result = gateway.getCarPositionRef();

        Assertions.assertEquals(cached, result);
        verify(carPositionService, never()).getPositionRoleMap();
    }

    @Test
    void getCarPositionRefShouldFetchAndCacheWhenMiss() {
        when(redisUtil.getCache(anyString(), any(java.lang.reflect.Type.class))).thenReturn(null);
        Map<Integer, List<String>> fetched = new HashMap<>();
        fetched.put(2, Collections.singletonList("roleB"));
        Result<Map<Integer, List<String>>> rpcResult = Result.success(fetched);
        when(carPositionService.getPositionRoleMap()).thenReturn(rpcResult);

        Map<Integer, List<String>> result = gateway.getCarPositionRef();

        Assertions.assertEquals(fetched, result);
        verify(redisUtil).setCache(anyString(), any(), anyLong());
    }

    @Test
    void queryCarEmployeeV2ShouldReturnEmptyOnException() {
        when(carEmployeeService.getEmployeeInfoV2(any())).thenThrow(new RuntimeException("fail"));
        EmployeeInfoResult result = gateway.queryCarEmployeeV2(GetEmployeeInfoParam.builder().miId(1L).build());
        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getMiId());
    }

    @Test
    void queryCarEmployeeV2ShouldReturnDataOnSuccess() {
        EmployeeInfoResponse response = new EmployeeInfoResponse();
        response.setMiId(123L);
        Result<EmployeeInfoResponse> rpcResult = Result.success(response);
        when(carEmployeeService.getEmployeeInfoV2(any())).thenReturn(rpcResult);

        EmployeeInfoResult result = gateway.queryCarEmployeeV2(GetEmployeeInfoParam.builder().miId(123L).build());
        Assertions.assertEquals(123L, result.getMiId());
    }

    @Test
    void getCarPositionRefShouldThrowWhenRpcEmpty() {
        when(redisUtil.getCache(anyString(), any(java.lang.reflect.Type.class))).thenReturn(null);
        Result<Map<Integer, List<String>>> rpcResult = Result.success(Collections.emptyMap());
        when(carPositionService.getPositionRoleMap()).thenReturn(rpcResult);

        Assertions.assertThrows(RuntimeException.class, () -> gateway.getCarPositionRef());
    }
}
