package com.wt.complaint.manage.api.model.resp.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有权限的审批类型选项响应，内嵌选项列表�?
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditTypeOptionResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiDocClassDefine(value = "list", description = "审批类型选项列表，按 id 递增排序")
    private List<AuditTypeOptionItemDto> list;
}
