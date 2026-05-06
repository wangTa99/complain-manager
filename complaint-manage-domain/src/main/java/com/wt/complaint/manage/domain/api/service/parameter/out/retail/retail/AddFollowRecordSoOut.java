package com.wt.complaint.manage.domain.api.service.parameter.out.retail;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 添加跟进记录出参
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFollowRecordSoOut implements Serializable {
    @ApiDocClassDefine(value = "result", description = "添加跟进记录结果")
    private String result;
}
