package com.wt.complaint.manage.infrastructure.outer.http.dato;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huwei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkBenchResult<T> {
    
    /**
     * 返回code
     */
    @SerializedName("code")
    private Integer code;
    
    /**
     * 返回执行时间
     */
    @SerializedName("exec_time")
    private Long execTime;
    
    /**
     * 返回结果描述
     */
    @SerializedName("message")
    private String message;
    
    /**
     * 返回数据
     */
    @SerializedName("data")
    private T data;
}
