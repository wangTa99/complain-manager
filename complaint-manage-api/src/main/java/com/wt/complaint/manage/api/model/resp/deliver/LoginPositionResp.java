package com.wt.complaint.manage.api.model.resp.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客诉单统计信息响应体
 *
 * @author huxiankang
 * @date 2025/6/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginPositionResp implements Serializable {

    @ApiDocClassDefine(value = "loginMid", description = "登录人mid")
    private Long loginMid;

    @ApiDocClassDefine(value = "loginUsername", description = "登录人name")
    private String loginUsername;

    @ApiDocClassDefine(value = "positionId", description = "岗位id")
    private Integer positionId;

    @ApiDocClassDefine(value = "positionName", description = "岗位name")
    private String positionName;

}
