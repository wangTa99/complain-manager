package com.wt.complaint.manage.api.model.resp;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 点赞信息
 *
 * @author huwei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoResp implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 小米id
     */
    @SerializedName("mid")
    private Long mid;
    
    /**
     * 头像
     */
    @SerializedName("avatarImage")
    private String avatarImage;
    
    /**
     * 是否点赞
     */
    @SerializedName("isLiked")
    private Boolean isLiked;
    
    /**
     * 操作时间
     */
    @SerializedName("operateTime")
    private Long operateTime;
}
