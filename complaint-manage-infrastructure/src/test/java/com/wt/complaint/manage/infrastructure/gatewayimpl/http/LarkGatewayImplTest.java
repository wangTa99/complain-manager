package com.wt.complaint.manage.infrastructure.gatewayimpl.http;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchGetIdUserResp;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CommonResultResp;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.config.Constants;
import com.wt.complaint.manage.infrastructure.utils.HttpClientV4;
import com.wt.nr.common.utils.GsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LarkGatewayImpl filterValidUser 方法单元测试
 *
 * @author AIGC
 * @date 2025/01/XX
 */
@ExtendWith(MockitoExtension.class)
class LarkGatewayImplTest {

    @InjectMocks
    private LarkGatewayImpl larkGatewayImpl;

    @Mock
    private RedisRemoteGateway redisRemoteGateway;

    private String testAppId = "test_app_id";
    private String testAppSecret = "test_app_secret";
    private String testAccessToken = "test_access_token";

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 @Value 注解的字�?
        setFieldValue(larkGatewayImpl, "appId", testAppId);
        setFieldValue(larkGatewayImpl, "appSecret", testAppSecret);
    }

    /**
     * 使用反射设置字段�?
     */
    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testFilterValidUser_EmptyList() {
        // 测试空列�?
        List<String> result = larkGatewayImpl.filterValidUser(new ArrayList<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterValidUser_NullList() {
        // 测试null列表
        List<String> result = larkGatewayImpl.filterValidUser(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterValidUser_Success() {
        // 测试正常情况：单个用�?
        List<String> emailPrefixList = Arrays.asList("zhangsan");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存 - 直接返回token，避免mock getAccessToken的复杂流�?
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam - 返回空字符串
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock filterValidUser的HTTP响应
            BatchGetIdUserResp.UserResp userResp = new BatchGetIdUserResp.UserResp();
            userResp.setUserId("user_123");
            userResp.setEmail("zhangsan@xiaomi.com");

            BatchGetIdUserResp batchResp = new BatchGetIdUserResp();
            batchResp.setUserList(Arrays.asList(userResp));

            CommonResultResp<BatchGetIdUserResp> commonResp = CommonResultResp.<BatchGetIdUserResp>builder()
                    .code(0)
                    .data(batchResp)
                    .build();

            String filterResponseJson = "{\"code\":0,\"data\":{\"user_list\":[{\"user_id\":\"user_123\",\"email\":\"zhangsan@xiaomi.com\"}]}}";

            // Mock filterValidUser的HTTP请求
            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(filterResponseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.filterValidUser(emailPrefixList);

            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("zhangsan", result.get(0));

            // 验证方法调用
            verify(redisRemoteGateway, atLeastOnce()).get(Constants.LARK_ACCESS_TOKEN_REDIS);
        }
    }

    @Test
    void testFilterValidUser_MultipleUsers() {
        // 测试多个用户
        List<String> emailPrefixList = Arrays.asList("zhangsan", "lisi", "wangwu");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class);
             MockedStatic<KeyCenterUtil> keyCenterUtilMock = mockStatic(KeyCenterUtil.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应
            BatchGetIdUserResp.UserResp user1 = new BatchGetIdUserResp.UserResp();
            user1.setUserId("user_1");
            user1.setEmail("zhangsan@xiaomi.com");

            BatchGetIdUserResp.UserResp user2 = new BatchGetIdUserResp.UserResp();
            user2.setUserId("user_2");
            user2.setEmail("lisi@xiaomi.com");

            BatchGetIdUserResp.UserResp user3 = new BatchGetIdUserResp.UserResp();
            user3.setUserId("user_3");
            user3.setEmail("wangwu@xiaomi.com");

            BatchGetIdUserResp batchResp = new BatchGetIdUserResp();
            batchResp.setUserList(Arrays.asList(user1, user2, user3));

            String filterResponseJson = "{\"code\":0,\"data\":{\"user_list\":[{}]}}";

            httpClientMock.when(() -> HttpClientV4.post(anyString(), anyString(), any(Map.class)))
                    .thenReturn(filterResponseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.filterValidUser(emailPrefixList);

            // 验证结果
            assertNotNull(result);
            assertEquals(0, result.size());

        }
    }

    // ========== queryUserIdByEmailPrefix 方法测试 ==========

    @Test
    void testQueryUserIdByEmailPrefix_EmptyList() {
        // 测试空列�?
        List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(new ArrayList<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testQueryUserIdByEmailPrefix_NullList() {
        // 测试null列表
        List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testQueryUserIdByEmailPrefix_Success() {
        // 测试正常情况：单个有效用�?
        List<String> emailPrefixList = Arrays.asList("zhangsan");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应
            String responseJson = "{\"code\":0,\"data\":{\"user_list\":[{\"user_id\":\"user_123\",\"email\":\"zhangsan@xiaomi.com\"}]}}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);

            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("user_123", result.get(0));

            // 验证方法调用
            verify(redisRemoteGateway, atLeastOnce()).get(Constants.LARK_ACCESS_TOKEN_REDIS);
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_MultipleValidUsers() {
        // 测试多个有效用户
        List<String> emailPrefixList = Arrays.asList("zhangsan", "lisi", "wangwu");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应
            String responseJson = "{\"code\":0,\"data\":{\"user_list\":[" +
                    "{\"user_id\":\"user_1\",\"email\":\"zhangsan@xiaomi.com\"}," +
                    "{\"user_id\":\"user_2\",\"email\":\"lisi@xiaomi.com\"}," +
                    "{\"user_id\":\"user_3\",\"email\":\"wangwu@xiaomi.com\"}" +
                    "]}}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);

            // 验证结果
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals("user_1", result.get(0));
            assertEquals("user_2", result.get(1));
            assertEquals("user_3", result.get(2));
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_PartialValidUsers() {
        // 测试部分有效用户：有些用户有效，有些无效（userId为空或email为null�?
        List<String> emailPrefixList = Arrays.asList("zhangsan", "lisi", "wangwu");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应：第一个用户有效，第二个userId为空，第三个email为null
            String responseJson = "{\"code\":0,\"data\":{\"user_list\":[" +
                    "{\"user_id\":\"user_1\",\"email\":\"zhangsan@xiaomi.com\"}," +
                    "{\"user_id\":\"\",\"email\":\"lisi@xiaomi.com\"}," +
                    "{\"user_id\":\"user_3\",\"email\":null}" +
                    "]}}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);

            // 验证结果：只返回有效的userId
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("user_1", result.get(0));
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_AllInvalidUsers() {
        // 测试全部无效用户
        List<String> emailPrefixList = Arrays.asList("zhangsan", "lisi");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应：所有用户都无效
            String responseJson = "{\"code\":0,\"data\":{\"user_list\":[" +
                    "{\"user_id\":\"\",\"email\":\"zhangsan@xiaomi.com\"}," +
                    "{\"user_id\":\"user_2\",\"email\":null}" +
                    "]}}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);

            // 验证结果：返回空列表
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_EmptyUserList() {
        // 测试返回空用户列�?
        List<String> emailPrefixList = Arrays.asList("zhangsan");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应：返回空用户列表
            String responseJson = "{\"code\":0,\"data\":{\"user_list\":[]}}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试
            List<String> result = larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);

            // 验证结果：返回空列表
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_Exception() {
        // 测试异常情况：batchGetId抛出异常
        List<String> emailPrefixList = Arrays.asList("zhangsan");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP请求抛出异常
            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenThrow(new RuntimeException("网络异常"));

            // 执行测试并验证异�?
            assertThrows(BusinessException.class, () -> {
                larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);
            });
        }
    }

    @Test
    void testQueryUserIdByEmailPrefix_InvalidResponse() {
        // 测试无效响应：code不为0或data为null
        List<String> emailPrefixList = Arrays.asList("zhangsan");

        try (MockedStatic<HttpClientV4> httpClientMock = mockStatic(HttpClientV4.class)) {

            // Mock Redis缓存
            when(redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS)).thenReturn(testAccessToken);

            // Mock createQueryParam
            httpClientMock.when(() -> HttpClientV4.encodingParams(any(), eq("UTF-8")))
                    .thenReturn("");

            // Mock HTTP响应：code不为0
            String responseJson = "{\"code\":1,\"data\":null}";

            httpClientMock.when(() -> HttpClientV4.post(
                    eq(Constants.FILTER_VALID_USER),
                    anyString(),
                    any(Map.class)
            )).thenReturn(responseJson);

            // 执行测试并验证异�?
            assertThrows(BusinessException.class, () -> {
                larkGatewayImpl.queryUserIdByEmailPrefix(emailPrefixList);
            });
        }
    }

}

