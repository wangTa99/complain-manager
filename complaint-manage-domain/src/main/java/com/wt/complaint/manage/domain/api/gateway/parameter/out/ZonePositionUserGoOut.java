package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import lombok.Data;

/**
 * @author zhangzheyang
 * @date 2025/1/1
 */
@Data
public class ZonePositionUserGoOut {

    private Long mid;

    private String name;

    private String email;

    /**
     * 用户状�?0-无效 1-有效 2-冻结
     */
    private Byte userState;
}
