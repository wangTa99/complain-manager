package com.wt.complaint.manage.api.model.resp;

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
public class DetailField implements Serializable {
    @ApiDocClassDefine(value = "id", description = "字段ID")
    private Integer id;

    @ApiDocClassDefine(value = "order", description = "字段顺序")
    private Integer order;

    @ApiDocClassDefine(value = "required", description = "是否必填")
    private Integer required;

    @ApiDocClassDefine(value = "fieldType", description = "字段类型")
    private Integer fieldType;

    @ApiDocClassDefine(value = "fieldName", description = "字段名称")
    private String fieldName;

    @ApiDocClassDefine(value = "filedCode", description = "字段代码")
    private String filedCode;

    @ApiDocClassDefine(value = "value", description = "字段值列�?)
    private List<Value> value;

    @ApiDocClassDefine(value = "attachments", description = "附件列表")
    private List<Attachment> attachments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Value implements Serializable {
        @ApiDocClassDefine(value = "code", description = "代码")
        private String code;

        @ApiDocClassDefine(value = "desc", description = "描述")
        private String desc;

        @ApiDocClassDefine(value = "路径ID全链�?, description = "路径ID全链�?)
        private String pathId;

        @ApiDocClassDefine(value = "路径名全链路", description = "路径名全链路")
        private String pathName;
    }
}
