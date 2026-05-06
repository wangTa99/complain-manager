package com.wt.complaint.manage.api.model.req.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldValue implements Serializable {
    /**
     * 针对选项有枚举值的场景
     */
    @ApiDocClassDefine(value = "code", description = "针对选项有枚举值的场景")
    private String code;
    /**
     * 选项�?
     */
    @ApiDocClassDefine(value = "desc", description = "选项�?)
    private String desc;
    /**
     * 级联全路径id
     */
    @ApiDocClassDefine(value = "pathId", description = "级联全路径id")
    private String pathId;
    /**
     * 级联全路径名�?
     */
    @ApiDocClassDefine(value = "pathName", description = "级联全路径名�?)
    private String pathName;
}
