package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书接口返回通用响应
 *
 * @param <T> 具体接口返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResultResp<T> {
    /**
     * 返回code
     */
    @SerializedName("code")
    private Integer code;

    /**
     * 返回code
     */
    @SerializedName("errorCode")
    private Integer errorCode;

    /**
     * 返回msg
     */
    @SerializedName("errorMsg")
    private String errorMsg;

    /**
     * 返回msg
     */
    @SerializedName("success")
    private Boolean success;

    /**
     * 返回结果描述
     */
    @SerializedName("message")
    private String message;

    /**
     * 飞书返回msg
     */
    private String msg;

    /**
     * 返回数据
     */
    @SerializedName("data")
    private T data;
}
