package com.wt.complaint.manage.api.model.resp;

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
public class LabelDTO implements Serializable {
    @ApiDocClassDefine(value = "tagType", description = "标签类型 1: 汽车标签, 2: 人员标签")
    private Integer tagType;
    @ApiDocClassDefine(value = "tagList", description = "标签信息")
    private List<TagInfo> tagList;

    @Data
    public static class TagInfo implements Serializable {

        @ApiDocClassDefine(value = "tagCode", description = "标签编码")
        private String tagCode;

        @ApiDocClassDefine(value = "tagName", description = "标签名称")
        private String tagName;
    }
}
