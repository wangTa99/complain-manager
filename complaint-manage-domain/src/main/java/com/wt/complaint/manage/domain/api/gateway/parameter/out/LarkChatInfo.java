package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 飞书建群返回信息
 *
 * @author zhangzheyang
 */
@Data
public class LarkChatInfo {

    /**
     * 群聊id
     */
    @SerializedName("chat_id")
    private String chatId;

    /**
     * 群分享链�?
     */
    @SerializedName("share_link")
    private String shareLink;
}
