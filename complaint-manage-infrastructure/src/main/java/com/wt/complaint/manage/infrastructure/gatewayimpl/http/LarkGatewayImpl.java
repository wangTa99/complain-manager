package com.wt.complaint.manage.infrastructure.gatewayimpl.http;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.wt.complaint.manage.domain.api.gateway.interfaces.http.LarkGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.RedisRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.BatchGetIdUserReqBody;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.LarkChatCreateParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.LarkChatMessageParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.LarkCommonQueryParam;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchGetIdUserResp;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CommonResultResp;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.LarkChatInfo;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.LarkTenantAccessToken;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.config.Constants;
import static com.wt.complaint.manage.infrastructure.config.Constants.FILTER_VALID_USER_MAX_PAGESIZE;
import com.wt.complaint.manage.infrastructure.utils.HttpClientV4;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.annotation.meta.param;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 飞书相关接口对接
 *
 * @Author zhangzheyang
 * @Date 2025/5/27
 */
@Service
@Slf4j
public class LarkGatewayImpl implements LarkGateway {

    @Value("${lark.app.id}")
    private String appId;

    @Value("${lark.app.secret}")
    private String appSecret;

    @Resource
    private RedisRemoteGateway redisRemoteGateway;

    @Override
    public String getAccessToken() {
        // 从缓存中获取accessToken
        String cachedToken = redisRemoteGateway.get(Constants.LARK_ACCESS_TOKEN_REDIS);
        if (cachedToken != null) {
            return cachedToken;
        }

        String url = Constants.LARK_TENANT_ACCESS_TOKEN;
        Map<String, String> header = new HashMap<>();
        header.put(HttpClientV4.CONTENT_TYPE, HttpClientV4.JSON_TYPE);
        Map<String, String> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("app_secret", KeyCenterUtil.decrypt(appSecret));

        String result = null;
        try {
            result = HttpClientV4.post(url, GsonUtil.toJson(body), header);
            log.info("LarkGatewayImpl.getAccessToken,body: {}, result: {}", GsonUtil.toJson(body), result);
        } catch (Exception e) {
            log.error("LarkGatewayImpl.getAccessToken error, url:{}, body:{}", url, GsonUtil.toJson(body), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "飞书accessToken 获取失败");
        }

        LarkTenantAccessToken resp = GsonUtil.fromJson(result, LarkTenantAccessToken.class);
        if (resp == null || resp.getCode() != 0 || StringUtils.isBlank(resp.getTenantAccessToken())) {
            log.error("LarkGatewayImpl.getAccessToken invalid response, url:{}, result:{}", url, result);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "飞书accessToken 获取失败");
        }
        // 放缓�?
        redisRemoteGateway.set(Constants.LARK_ACCESS_TOKEN_REDIS,
                resp.getTenantAccessToken(),
                resp.getExpire() - 10L,
                TimeUnit.SECONDS);
        return resp.getTenantAccessToken();
    }

    public List<String> filterValidUser(List<String> emailPrefixList) {
        log.info("LarkGatewayImpl.filterValidUser start, emailPrefixList: {}", GsonUtil.toJson(emailPrefixList));
        if (CollUtil.isEmpty(emailPrefixList)) {
            return new ArrayList<>();
        }

        List<BatchGetIdUserResp.UserResp> userList = this.batchGetId(emailPrefixList);

        List<String> validEmailPrefixList = new ArrayList<>();
        for (BatchGetIdUserResp.UserResp userResp : userList) {
            if (StringUtils.isNotBlank(userResp.getUserId()) && userResp.getEmail() != null) {
                validEmailPrefixList.add(userResp.getEmail().replace("@xiaomi.com", ""));
            }
        }
        log.info("LarkGatewayImpl.filterValidUser end, validEmailPrefixList: {}", GsonUtil.toJson(validEmailPrefixList));
        return validEmailPrefixList;
    }

    @NotNull
    private List<BatchGetIdUserResp.UserResp> batchGetId(List<String> emailPrefixList) {
        String encodedContent = createQueryParam();
        // url拼接
        String url = Constants.FILTER_VALID_USER + ((StringUtils.isEmpty(encodedContent)) ? "" : ("?" + encodedContent));

        // 获取accessToken
        Map<String, String> header = createCommonHeader();

        List<BatchGetIdUserResp.UserResp> userList = new ArrayList<>();

        List<String> emailList = emailPrefixList.stream().map(s -> s + "@xiaomi.com").collect(Collectors.toList());
        List<List<String>> splits = CollUtil.split(emailList, FILTER_VALID_USER_MAX_PAGESIZE);

        for (List<String> splitEmailList : splits) {
            BatchGetIdUserReqBody body = new BatchGetIdUserReqBody();
            body.setEmails(splitEmailList);

            String result = null;
            try {
                log.info("LarkGatewayImpl.batchGetId start, body: {}, url: {}", GsonUtil.toJson(body), url);
                result = HttpClientV4.post(url, GsonUtil.toJson(body), header);
                log.info("LarkGatewayImpl.batchGetId, body: {}, result: {}, url: {}", GsonUtil.toJson(body), result, url);
            } catch (Exception e) {
                log.error("LarkGatewayImpl.batchGetId error, body:{}, url:{}", GsonUtil.toJson(body), url, e);
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "查询有效用户失败");
            }

            CommonResultResp<BatchGetIdUserResp> resp = GsonUtil.fromJson(result,
                    new TypeToken<CommonResultResp<BatchGetIdUserResp>>() {}.getType());
            if (resp == null || resp.getCode() != 0 || resp.getData() == null) {
                log.error("LarkGatewayImpl.batchGetId invalid response, url:{}, result:{}", url, result);
                throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "查询有效用户失败");
            }
            if (CollUtil.isNotEmpty(resp.getData().getUserList())) {
                userList.addAll(resp.getData().getUserList());
            }
        }
        return userList;
    }

    @Override
    public List<String> queryUserIdByEmailPrefix(List<String> emailPrefixList) {
        log.info("LarkGatewayImpl.queryUserIdByEmailPrefix start, emailPrefixList: {}", GsonUtil.toJson(emailPrefixList));
        if (CollUtil.isEmpty(emailPrefixList)) {
            return new ArrayList<>();
        }

        List<BatchGetIdUserResp.UserResp> userList = this.batchGetId(emailPrefixList);

        List<String> userIdList = new ArrayList<>();
        List<String> invalidEmailList = new ArrayList<>();
        for (BatchGetIdUserResp.UserResp userResp : userList) {
            if (StringUtils.isNotBlank(userResp.getUserId()) && userResp.getEmail() != null) {
                userIdList.add(userResp.getUserId());
            } else {
                invalidEmailList.add(userResp.getEmail());
            }
        }
        if (CollUtil.isNotEmpty(invalidEmailList)) {
            log.warn("LarkGatewayImpl.queryUserIdByEmailPrefix, invalidEmailList: {}", GsonUtil.toJson(invalidEmailList));
        }
        log.info("LarkGatewayImpl.queryUserIdByEmailPrefix end, userIdList: {}", GsonUtil.toJson(userIdList));
        return userIdList;
    }

    @Override
    public String createChat(LarkChatCreateParam param) {
        String encodedContent = createQueryParam();
        // url拼接
        String url = Constants.LARK_CREATE_CHAT + ((StringUtils.isEmpty(encodedContent)) ? "" : ("?" + encodedContent));

        // 获取accessToken
        Map<String, String> header = createCommonHeader();

        String result = null;
        try {
            log.info("LarkGatewayImpl.createChat start, url: {}, body: {}", url, GsonUtil.toJson(param));
            result = HttpClientV4.post(url, GsonUtil.toJson(param), header);
            log.info("LarkGatewayImpl.createChat, url: {}, body: {}, result: {}", url, GsonUtil.toJson(param), result);
        } catch (Exception e) {
            log.error("LarkGatewayImpl.createChat error, url:{}, body:{}", url, GsonUtil.toJson(param), e);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "创建飞书群聊失败");
        }

        CommonResultResp<LarkChatInfo> resp = GsonUtil.fromJson(result,
                new TypeToken<CommonResultResp<LarkChatInfo>>() {
                }.getType());
        if (resp == null || resp.getCode() != 0 || resp.getData() == null) {
            log.error("LarkGatewayImpl.createChat invalid response, url:{}, result:{}", url, result);
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "创建飞书群聊失败");
        }

        // 返回chatId
        return resp.getData().getChatId();
    }

    @Override
    public Boolean sendMessage(LarkChatMessageParam param) {
        if (param == null || StringUtils.isBlank(param.getReceiveId())) {
            log.warn("LarkGatewayImpl.sendMessage invalid params, param:{}", GsonUtil.toJson(param));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "飞书消息发送参数不合法");
        }

        String encodedContent = createQueryParam();
        // url拼接
        String url = Constants.LARK_SEND_MESSAGE + ((null == encodedContent) ? "" : ("?" + encodedContent));

        // 先获取accessToken
        Map<String, String> header = createCommonHeader();
        param.setUuid(UUID.randomUUID().toString());

        String result = null;
        try {
            result = HttpClientV4.post(url, GsonUtil.toJson(param), header);
            log.info("LarkGatewayImpl.sendMessage, url:{}, body:{}, result:{}", url, GsonUtil.toJson(param), result);
        } catch (Exception e) {
            log.error("LarkGatewayImpl.sendMessage error, url:{}, body:{}", url, GsonUtil.toJson(param), e);
            return false;
        }

        CommonResultResp resp = GsonUtil.fromJson(result, CommonResultResp.class);
        if (resp.getCode() != 0) {
            log.error("LarkGatewayImpl.sendMessage invalid response, url:{}, result:{}", url, result);
            return false;
        }

        return true;
    }

    /**
     * 飞书通用header构�?
     *
     * @return
     */
    private Map<String, String> createCommonHeader() {
        // 先获取accessToken
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取飞书accessToken失败");
        }

        // 请求header基本构�?
        Map<String, String> header = new HashMap<>();
        header.put(HttpClientV4.CONTENT_TYPE, HttpClientV4.JSON_TYPE);
        header.put(Constants.AUTHORIZATION, "Bearer " + accessToken);
        return header;
    }

    /**
     * 构造通用的飞书queryParam
     *
     * @return
     */
    private String createQueryParam() {
        LarkCommonQueryParam param = new LarkCommonQueryParam();
        // 直接转化会报错，先转为hashmap，再转到Multimap
        Map<String, String> paramMap =
                GsonUtil.fromJson(GsonUtil.toJson(param), new TypeToken<HashMap<String, String>>() {
                }.getType());
        Multimap<String, String> queryParam = ArrayListMultimap.create();
        paramMap.forEach(queryParam::put);

        String encodedContent = null;
        try {
            encodedContent = HttpClientV4.encodingParams(queryParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "飞书请求参数编码失败");
        }
        return encodedContent;
    }

}
