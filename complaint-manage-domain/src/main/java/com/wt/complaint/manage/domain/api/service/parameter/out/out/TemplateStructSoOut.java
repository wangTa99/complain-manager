package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateStructSoOut implements Serializable {
    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组排序
     */
    private Integer groupOrder;

    /**
     * 字段列表
     */
    private List<DetailFieldSoOut> fields;
}
