package com.wt.complaint.manage.api.model.resp.operate;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 提交复盘响应（客诉三期）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiDocClassDefine(value = "success", description = "是否成功")
    private Boolean success;
}
