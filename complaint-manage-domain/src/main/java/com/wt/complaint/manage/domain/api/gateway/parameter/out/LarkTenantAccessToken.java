package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 飞书访问凭证响应
 *
 * @author zhangzheyang
 */
@Data
public class LarkTenantAccessToken {

    /**
     * �?表示失败
     */
    private Integer code;

    private String msg;

    /**
     * 租户访问凭证
     */
    @SerializedName("tenant_access_token")
    private String tenantAccessToken;

    /**
     * 过期时间，单位秒
     */
    private Integer expire;
}
