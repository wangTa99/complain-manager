package com.wt.complaint.manage.api.model.req.operate;

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
public class TemplateStructInfo implements Serializable {
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 分组顺序
     */
    private Integer groupOrder;
    /**
     * �?
     */
    private List<TemplateField> fields;
}
