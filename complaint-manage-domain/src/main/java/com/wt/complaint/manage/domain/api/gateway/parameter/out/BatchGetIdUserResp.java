package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author huxiankang
 * @date 2025/11/5
 */
@Data
public class BatchGetIdUserResp {

    @SerializedName("user_list")
    private List<UserResp> userList;

    @Data
    public static class UserResp {

        @SerializedName("user_id")
        private String userId;

        @SerializedName("email")
        private String email;

    }
}
