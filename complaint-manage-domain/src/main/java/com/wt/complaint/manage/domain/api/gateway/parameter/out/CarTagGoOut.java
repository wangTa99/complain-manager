package com.wt.complaint.manage.domain.api.gateway.parameter.out;

import com.wt.complaint.manage.api.model.resp.LabelDTO;
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
public class CarTagGoOut {
    /**
     * 标签类型 1: 汽车标签, 2: 人员标签
     */
    private Integer tagType;
    /**
     * 标签信息
     */
    private List<TagInfoGoOut> tagList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TagInfoGoOut implements Serializable {

        /**
         * 标签编码
         */
        private String tagCode;

        /**
         *  标签�?
         */
        private String tagName;
    }
}
