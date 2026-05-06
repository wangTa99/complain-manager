package com.wt.complaint.manage.domain.api.gateway.parameter.in;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 飞书建群请求参数
 *
 * @author keyonyzhang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LarkChatCreateParam {

    /**
     * 用于去重
     */
    private String uuid;

    /**
     * 群名�?
     */
    private String name;

    /**
     * 群描�?
     */
    private String description;

    /**
     * 建群时指定的群主，不填默认为机器�?
     */
    @SerializedName("owner_id")
    private String ownerId;

    /**
     * 成员列表
     */
    @SerializedName("user_id_list")
    private List<String> userIdList;

}
