package com.wt.complaint.manage.api.model.resp;

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
public class TagDTO implements Serializable {

    private static final long serialVersionUID = 4411083089993524481L;

    @ApiDocClassDefine(value = "code", description = "tag 英文字符�?)
    private String code;

    @ApiDocClassDefine(value = "desc", description = "tag 中文描述")
    private String desc;
}
