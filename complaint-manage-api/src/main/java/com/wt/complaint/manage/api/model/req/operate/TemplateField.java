package com.wt.complaint.manage.api.model.req.operate;

import com.wt.complaint.manage.api.model.Attachment;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
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
public class TemplateField implements Serializable {
    @ApiDocClassDefine(value = "id", description = "字段ID")
    private Integer id;

    @ApiDocClassDefine(value = "order", description = "排序")
    private Integer order;

    @ApiDocClassDefine(value = "required", description = "是否必填")
    private Integer required;

    @ApiDocClassDefine(value = "fieldType", description = "字段类型")
    private Integer fieldType;

    @ApiDocClassDefine(value = "fieldName", description = "字段名称")
    private String fieldName;

    @ApiDocClassDefine(value = "fieldCode", description = "字段编码")
    private String fieldCode;

    @ApiDocClassDefine(value = "value", description = "值列�?)
    private List<FieldValue> value;

    @ApiDocClassDefine(value = "attachments", description = "附件列表")
    private List<Attachment> attachments;
}
