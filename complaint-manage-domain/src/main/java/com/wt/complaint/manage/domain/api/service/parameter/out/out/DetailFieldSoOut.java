package com.wt.complaint.manage.domain.api.service.parameter.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailFieldSoOut {
    /**
     * 字段ID
     */
    private Integer id;

    /**
     * 字段顺序
     */
    private Integer order;

    /**
     * 是否必填
     */
    private Integer required;

    /**
     * 字段类型
     */
    private Integer fieldType;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段代码
     */
    private String filedCode;

    /**
     * 字段值列�?
     */
    private List<Value> value;

    /**
     * 附件列表
     */
    private List<AttachmentSoOut> attachments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Value implements Serializable {
        /**
         * 代码
         */
        private String code;

        /**
         * 描述
         */
        private String desc;

        private String pathId;
        private String pathName;
    }
}