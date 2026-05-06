package com.wt.complaint.manage.domain.manager;

import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author linjiehong
 * @date 2025/6/3 11:16
 */
@Data
@Builder
public class RoleContext {
    /**
     * 客诉服务对应的角色枚�?
     */
    private PositionEnum positionEnum;

    /**
     * 角色区域信息
     */
    private List<Integer> roleAreaId;
}
